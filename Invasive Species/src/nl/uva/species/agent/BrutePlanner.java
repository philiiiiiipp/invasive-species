package nl.uva.species.agent;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.JFrame;

import nl.uva.species.genetic.GeneticModelCreator;
import nl.uva.species.model.EnvModel;
import nl.uva.species.model.Reach;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;
import nl.uva.species.ui.GraphInterface;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class BrutePlanner implements AgentInterface {

	private enum Phase {
		LEARNING, PLANNING
	};

	private Phase mCurrentPhase = Phase.LEARNING;

	private River mRiver;

	private GeneticModelCreator mModelGenerator;

	private EnvModel mCurrentModel;

	private int counter = 0;

	private boolean skippedFirstPlanning = false;

	private GraphInterface mGraphInterface = new GraphInterface();

	@Override
	public void agent_init(final String taskSpecification) {
		System.out.print(taskSpecification);
		TaskSpec theTaskSpec = new TaskSpec(taskSpecification);
		mRiver = new River(theTaskSpec);

		mGraphInterface.init();
		JFrame frame = new JFrame();
		frame.getContentPane().add(mGraphInterface);
		frame.setTitle("The invasive species domain");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		mCurrentModel = new EnvModel(mRiver, false);

		try {
			System.setErr(new PrintStream("/dev/null"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mModelGenerator == null)
			mModelGenerator = new GeneticModelCreator(mRiver);
	}

	int episodeCount = 0;

	@Override
	public Action agent_start(final Observation observation) {
		episodeCount = 0;

		if (mCurrentPhase == Phase.LEARNING) {
			mModelGenerator.finishEpisode();
			System.out.println("\n START Learning \n");
		} else {
			System.out.println("\n START Planning \n");
		}

		RiverState currentState = new RiverState(mRiver, observation);
		Action action = null;

		if (mCurrentPhase == Phase.LEARNING) {
			action = getHeuristicNextAction(observation);

			mModelGenerator.addRiverState(new RiverState(mRiver, observation));
			mModelGenerator.addAction(action);
		} else {
			action = getBestAction(currentState);
		}

		episodeCount++;

		return action;
	}

	private Action getNothingAction() {
		Action best = new Action();
		best.intArray = new int[] { Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING,
				Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING };

		return best;
	}

	int showStuff = 0;

	@Override
	public Action agent_step(final double reward, final Observation observation) {

		RiverState currentState = new RiverState(mRiver, observation);
		Action action = null;

		if (mCurrentPhase == Phase.LEARNING) {
			action = getHeuristicNextAction(observation);

			mModelGenerator.addRiverState(new RiverState(mRiver, observation));
			mModelGenerator.addAction(action);
		} else {
			action = getBestAction(currentState);
		}

		mGraphInterface.update(currentState);
		mGraphInterface.showActions(action.intArray);

		if (mCurrentPhase == Phase.PLANNING && showStuff % 100 < 10) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		showStuff++;

		episodeCount++;
		return action;
	}

	@Override
	public void agent_end(final double reward) {
		// TODO Auto-generated method stub

	}

	@Override
	public void agent_cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public String agent_message(final String inMessage) {
		System.out.println();
		System.out.println("Agent_Message: " + inMessage);

		// Message Description
		// 'freeze learning'
		// Action{ Set flag to stop updating policy
		//
		if (inMessage.startsWith("freeze learning")) {
			mCurrentPhase = Phase.PLANNING;
			mCurrentModel = mModelGenerator.getBestModel(mRiver);

			return "message understood, policy frozen";
		}
		// Message Description
		// unfreeze learning
		// Action{ Set flag to resume updating policy
		//
		if (inMessage.startsWith("unfreeze learning")) {
			mCurrentPhase = Phase.LEARNING;
			skippedFirstPlanning = true;

			mModelGenerator.reinitialise(mRiver);
			return "message understood, policy unfrozen";
		}
		// Message Description
		// freeze exploring
		// Action{ Set flag to stop exploring (greedy actions only)
		//
		if (inMessage.startsWith("freeze exploring")) {
			return "message understood, exploring frozen";
		}
		// Message Description
		// unfreeze exploring
		// Action{ Set flag to resume exploring (e-greedy actions)
		//
		if (inMessage.startsWith("unfreeze exploring")) {
			return "message understood, exploring frozen";
		}
		return "Invasive agent does not understand your message.";
	}

	private Action getBestAction(final RiverState riverState) {
		Action best = new Action();
		best.intArray = new int[] { Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING,
				Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING };

		double bestReward = Double.NEGATIVE_INFINITY;

		for (int a = Utilities.ACTION_NOTHING; a <= Utilities.ACTION_ERADICATE_RESTORE; ++a) {
			for (int b = Utilities.ACTION_NOTHING; b <= Utilities.ACTION_ERADICATE_RESTORE; ++b) {
				for (int c = Utilities.ACTION_NOTHING; c <= Utilities.ACTION_ERADICATE_RESTORE; ++c) {
					for (int d = Utilities.ACTION_NOTHING; d <= Utilities.ACTION_ERADICATE_RESTORE; ++d) {
						for (int e = Utilities.ACTION_NOTHING; e <= Utilities.ACTION_ERADICATE_RESTORE; ++e) {
							for (int f = Utilities.ACTION_NOTHING; f <= Utilities.ACTION_ERADICATE_RESTORE; ++f) {
								for (int g = Utilities.ACTION_NOTHING; g <= Utilities.ACTION_ERADICATE_RESTORE; ++g) {
									Action current = new Action();
									current.intArray = new int[] { a, b, c, d, e, f, g };

									double reward = mCurrentModel.getExpectedNextStateReward(riverState, current)
											+ mCurrentModel.getActionReward(riverState, current);

									if (reward > bestReward) {
										bestReward = reward;
										best = current;
									}
								}
							}
						}
					}
				}
			}
		}

		return best;
	}

	private void showArray(final int[] array) {
		for (int i : array) {
			System.out.print(i + " ");
		}
	}

	private Action getHeuristicNextAction(final Observation observation) {
		RiverState rState = new RiverState(mRiver, observation);
		int[] resultAction = new int[rState.getReaches().size()];

		if (episodeCount == 0) {
			// First action, always restorate
			for (Reach reach : rState.getReaches()) {
				if (reach.getHabitatsEmpty() > 0) {
					resultAction[reach.getIndex()] = Utilities.ACTION_RESTORE;
				} else {
					resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
				}
			}
		} else if (episodeCount % 300 < 100) {
			// Nothing
			for (Reach reach : rState.getReaches()) {
				resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
			}
		} else if (episodeCount % 300 < 200) {
			// Eradicate
			for (Reach reach : rState.getReaches()) {
				if (reach.getValidActions().contains(Utilities.ACTION_ERADICATE)) {
					resultAction[reach.getIndex()] = Utilities.ACTION_ERADICATE;
				} else {
					resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
				}
			}
		} else {
			// Eradicate + Restore
			for (Reach reach : rState.getReaches()) {
				if (reach.getValidActions().contains(Utilities.ACTION_ERADICATE_RESTORE)) {
					resultAction[reach.getIndex()] = Utilities.ACTION_ERADICATE_RESTORE;
				} else {
					resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
				}
			}
		}

		Action action = new Action();
		action.intArray = resultAction;
		return action;
	}

	/**
	 * Load our agent with the AgentLoader and automatically connect to the rl_glue server.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		AgentLoader theLoader = new AgentLoader(new BrutePlanner());
		theLoader.run();
	}
}
