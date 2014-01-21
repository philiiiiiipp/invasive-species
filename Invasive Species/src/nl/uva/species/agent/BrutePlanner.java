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
import nl.uva.species.utils.Pair;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class BrutePlanner implements AgentInterface {

	/** The supported phases of the environment */
	private enum Phase {
		LEARNING, PLANNING
	};

	/** The current phase */
	private Phase mCurrentPhase = Phase.LEARNING;

	/** The river where our model bases on */
	private River mRiver;

	/** The generic algorithm */
	private GeneticModelCreator mModelGenerator;

	/** The last calculated most probable model */
	private EnvModel mCurrentModel;

	/** Episode counter */
	int mEpisodeCount = 0;

	/** Graph update counter */
	private int mGraphVisibilityCounter = 0;

	/** The graph interface */
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
			// Set the err output to /dev/null, most probable works only under linux/mac (Guess who wrote that). This is
			// there to prevent the graph from spmaming the whole console.
			System.setErr(new PrintStream("/dev/null"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (mModelGenerator == null)
			mModelGenerator = new GeneticModelCreator(mRiver);
	}

	@Override
	public Action agent_start(final Observation observation) {
		mEpisodeCount = 0;

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

		mEpisodeCount++;

		return action;
	}

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

		if (mCurrentPhase == Phase.PLANNING && mGraphVisibilityCounter % 100 < 10) {

			mGraphInterface.update(currentState);
			mGraphInterface.showActions(action.intArray);

			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		mGraphVisibilityCounter++;

		mEpisodeCount++;
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

	/**
	 * Brute-force method to determine the best possible action, considering a horizon of 1
	 * 
	 * @param riverState
	 *            The current state
	 * @return The best possible action
	 */
	private Action getBestAction(final RiverState riverState) {
		return getBestAction(riverState, 0, new int[riverState.getReaches().size()]).getLeft();
	}

	/**
	 * Search recursive through all possible actions to determine the best action
	 * 
	 * @param riverState
	 *            The current state
	 * @param reachPosition
	 *            The position which needs to be set next
	 * @param action
	 *            The so far action list
	 * @return Returns the best Action with its rewards value
	 */
	private Pair<Action, Double> getBestAction(final RiverState riverState, final int reachPosition, final int[] action) {
		if (reachPosition == riverState.getReaches().size()) {
			Action current = new Action();
			current.intArray = action;

			double reward = mCurrentModel.getExpectedNextStateReward(riverState, current)
					+ mCurrentModel.getActionReward(riverState, current);

			return new Pair<Action, Double>(current, reward);
		}

		Reach currentReach = riverState.getReach(reachPosition);
		Pair<Action, Double> temp = null, resultAction = null;
		for (Integer a : currentReach.getValidActions()) {
			action[currentReach.getIndex()] = a;

			temp = getBestAction(riverState, reachPosition + 1, action);

			if (resultAction == null || resultAction.getRight() < temp.getRight()) {
				resultAction = temp;
			}
		}

		return resultAction;
	}

	/**
	 * Clever heuristic in order to determine a good model with our genetic algorihm's
	 * 
	 * @param observation
	 *            The last observation
	 * @return The action which will presumably give us the more knowledge about the real model
	 */
	private Action getHeuristicNextAction(final Observation observation) {
		RiverState rState = new RiverState(mRiver, observation);
		int[] resultAction = new int[rState.getReaches().size()];

		if (mEpisodeCount == 0) {
			// First action, always restore
			for (Reach reach : rState.getReaches()) {
				if (reach.getHabitatsEmpty() > 0) {
					resultAction[reach.getIndex()] = Utilities.ACTION_RESTORE;
				} else {
					resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
				}
			}
		} else if (mEpisodeCount % 300 < 100) {
			// Nothing
			for (Reach reach : rState.getReaches()) {
				resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
			}
		} else if (mEpisodeCount % 300 < 200) {
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
