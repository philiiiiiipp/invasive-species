package nl.uva.species.genetic.test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;

import javax.swing.JFrame;

import nl.uva.species.genetic.GeneticModelCreator;
import nl.uva.species.model.EnvModel;
import nl.uva.species.model.Reach;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;
import nl.uva.species.ui.GraphInterface;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public class GeneticModelCreatorTest {

	/** The amount of nothing action taken during learning */
	private static final int AMOUNT_NOTHING_ACTION = 100;

	/** The amount of eradicate action taken during learning */
	private static final int AMOUNT_ERADICATE_ACTION = 100;

	/** The amount of eradicate and restore action taken during learning */
	private static final int AMOUNT_ERADICATE_RESTORE_ACTION = 100;

	public static void main(final String[] args) {
		test5();

	}

	public static void test5() {

		GraphInterface mGraphInterface = new GraphInterface();

		mGraphInterface.init();
		JFrame frame = new JFrame();
		frame.getContentPane().add(mGraphInterface);
		frame.setTitle("The invasive species domain");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		try {
			// Set the err output to /dev/null, most probable works only under linux/mac (Guess who wrote that). This is
			// there to prevent the graph from spmaming the whole console.
			System.setErr(new PrintStream("/dev/null"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		River testRiver = createRiver();
		Observation startObservation = new Observation();
		startObservation.intArray = new int[] { Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED,
				Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED,
				Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED,
				Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED,
				Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED,
				Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED,
				Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED,
				Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED,
				Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED,
				Utilities.HABITAT_INVADED, Utilities.HABITAT_INVADED };

		// startObservation.intArray = new int[] { Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE,
		// Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE,
		// Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE,
		// Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE,
		// Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE,
		// Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE,
		// Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE,
		// Utilities.HABITAT_NATIVE, Utilities.HABITAT_NATIVE };

		EnvModel trueModel = new EnvModel(testRiver, false);
		RiverState state = new RiverState(testRiver, startObservation);

		Action nextAction = new Action();

		nextAction.intArray = new int[] { Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING,
				Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING, Utilities.ACTION_NOTHING };

		Action totalEradication = new Action();
		totalEradication.intArray = new int[] { Utilities.ACTION_ERADICATE_RESTORE, Utilities.ACTION_ERADICATE_RESTORE,
				Utilities.ACTION_ERADICATE_RESTORE, Utilities.ACTION_ERADICATE_RESTORE,
				Utilities.ACTION_ERADICATE_RESTORE, Utilities.ACTION_ERADICATE_RESTORE,
				Utilities.ACTION_ERADICATE_RESTORE };

		mGraphInterface.update(state);
		mGraphInterface.showActions(nextAction.intArray);

		DecimalFormat df = new DecimalFormat("#.###");

		int h = 0;
		while (h < 100) {
			h++;

			for (Reach reach : state.getReaches()) {
				if (reach.getValidActions().contains(Utilities.ACTION_ERADICATE_RESTORE)
						&& reach.getHabitatsInvaded() >= 1) {
					totalEradication.intArray[reach.getIndex()] = Utilities.ACTION_ERADICATE_RESTORE;

				} else if (reach.getValidActions().contains(Utilities.ACTION_RESTORE)) {
					totalEradication.intArray[reach.getIndex()] = Utilities.ACTION_RESTORE;

				} else {
					totalEradication.intArray[reach.getIndex()] = Utilities.ACTION_NOTHING;
				}
			}

			double nothingState = trueModel.getExpectedNextStateReward(state, nextAction);
			double eradicationState = trueModel.getExpectedNextStateReward(state, totalEradication)
					+ trueModel.getActionReward(state, totalEradication);

			System.out.print(df.format(eradicationState) + " - " + df.format(nothingState) + " = "
					+ (eradicationState - nothingState) + " ");

			if (eradicationState - nothingState > -0.01) {
				System.out.println(" Eradication");
				state = trueModel.getPossibleNextState(state, totalEradication);
				mGraphInterface.update(state);
				mGraphInterface.removeActions();
				mGraphInterface.showActions(totalEradication.intArray);
			} else {
				System.out.println(" Nothing");
				state = trueModel.getPossibleNextState(state, nextAction);
				mGraphInterface.update(state);
				mGraphInterface.removeActions();
				mGraphInterface.showActions(nextAction.intArray);
			}

			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Test the generated model against a true model
	 */
	public static void testGeneratedModel() {
		River testRiver = createRiver();
		Observation startObservation = new Observation();
		startObservation.intArray = new int[] { 1, 1, 3, 2, 3, 1, 1, 3, 1, 3, 2, 1, 3, 1, 3, 1, 2, 2, 1, 3, 2, 2, 2, 2,
				3, 3, 2, 1 };

		EnvModel trueModel = new EnvModel(testRiver, false);

		RiverState oldState = new RiverState(testRiver, startObservation);

		Action lastAction = getHeuristicNextAction(oldState, 0, AMOUNT_NOTHING_ACTION, AMOUNT_ERADICATE_ACTION,
				AMOUNT_ERADICATE_RESTORE_ACTION);
		RiverState newState = trueModel.getPossibleNextState(oldState, lastAction);

		GeneticModelCreator gModel = new GeneticModelCreator(testRiver);

		int simulationSteps = 2500;
		gModel.finishEpisode();
		gModel.addRiverState(oldState);
		gModel.addRiverState(newState);
		gModel.addAction(lastAction);

		for (int i = 0; i < simulationSteps; ++i) {
			lastAction = getHeuristicNextAction(newState, i, AMOUNT_NOTHING_ACTION, AMOUNT_ERADICATE_ACTION,
					AMOUNT_ERADICATE_RESTORE_ACTION);
			newState = trueModel.getPossibleNextState(newState, lastAction);

			gModel.addRiverState(newState);
			gModel.addAction(lastAction);
		}

		EnvModel generatedModel = gModel.getBestModel(testRiver, trueModel);

		System.out.println("Distance true <-> generated");
		trueModel.printComparison(generatedModel);

		int comparisonSteps = 100;
		oldState = new RiverState(testRiver, startObservation);
		double evaluation = 0;
		for (int i = 0; i < comparisonSteps; ++i) {
			lastAction = genereateActions(oldState.getObservation());
			newState = trueModel.getPossibleNextState(oldState, lastAction);

			evaluation += generatedModel.evaluateModel(oldState, lastAction, newState) / comparisonSteps;

			oldState = newState;
		}
		System.out.println("Eval gen vs. true: " + evaluation);

		oldState = new RiverState(testRiver, startObservation);
		evaluation = 0;
		for (int i = 0; i < comparisonSteps; ++i) {
			lastAction = genereateActions(oldState.getObservation());
			newState = trueModel.getPossibleNextState(oldState, lastAction);

			evaluation += trueModel.evaluateModel(oldState, lastAction, newState) / comparisonSteps;

			oldState = newState;
		}
		System.out.println("Eval true vs. true: " + evaluation);
	}

	/**
	 * Clever heuristic in order to determine a good model with our genetic algorihm's
	 * 
	 * @param rState
	 *            The current river state
	 * @param currentTimestep
	 *            The current timestep
	 * @param nothingActionAmount
	 *            The amount of nothing actions which should be performed
	 * @param eradicateActionAmount
	 *            The amount of eradicate actions which should be performed
	 * @param eradicateRestoreActionAmount
	 *            The amount of eradicate and restore actions that should be performed
	 * @return The action which will presumably give us the most knowledge about the real model
	 */
	public static Action getHeuristicNextAction(final RiverState rState, final int currentTimestep,
			final int nothingActionAmount, final int eradicateActionAmount, final int eradicateRestoreActionAmount) {

		int[] resultAction = new int[rState.getReaches().size()];
		int total = nothingActionAmount + eradicateActionAmount + eradicateRestoreActionAmount;

		if (currentTimestep == 0) {
			// First action, always restore
			for (Reach reach : rState.getReaches()) {
				if (reach.getHabitatsEmpty() > 0) {
					resultAction[reach.getIndex()] = Utilities.ACTION_RESTORE;
				} else {
					resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
				}
			}
		} else if (currentTimestep % total < nothingActionAmount) {
			// Nothing
			for (Reach reach : rState.getReaches()) {
				resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
			}
		} else if (currentTimestep % total < eradicateActionAmount) {
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
	 * Generate an action based on heuristics
	 * 
	 * @param observation
	 *            The observed state
	 * @return The appropriate action for this observation
	 */
	private static Action genereateActions(final Observation observation) {

		int[] theState = new int[7];
		Action returnAction = new Action();

		for (int i = 0; i < 7; ++i) {
			theState[i] = Utilities.ACTION_NOTHING;

			// make nothing with 30%
			if (Utilities.RNG.nextDouble() > 0.7)
				continue;

			int tam = 0;
			int empty = 0;

			for (int j = 0; j < 4; ++j) {
				if (observation.intArray[i * 4 + j] == Utilities.HABITAT_INVADED) {
					++tam;
				}
				if (observation.intArray[i * 4 + j] == Utilities.HABITAT_EMPTY) {
					++empty;
				}
			}
			if (tam > 2) {
				theState[i] = Utilities.ACTION_ERADICATE_RESTORE;
			} else if (empty >= 1) {
				theState[i] = Utilities.ACTION_RESTORE;
			}
		}

		returnAction.intArray = theState;

		return returnAction;
	}

	/**
	 * Create the default river from the Website
	 * 
	 * @return The default test river
	 */
	public static River createRiver() {
		return new River(
				new TaskSpec(
						"VERSION RL-Glue-3.0 PROBLEMTYPE non-episodic DISCOUNTFACTOR 0.9 OBSERVATIONS INTS (28 1 3) ACTIONS INTS (7 1 4) REWARDS (-10000 -16.1) EXTRA [(0, 7), (1, 4), (2, 6), (3, 6), (4, 0), (5, 4), (6, 0)] BUDGET 100 by Majid Taleghan."));
	}

	/**
	 * Testrun 1 generated with the rl_glue and python environment
	 */
	public void test1() {
		River river = new River(
				new TaskSpec(
						"VERSION RL-Glue-3.0 PROBLEMTYPE non-episodic DISCOUNTFACTOR 0.9 OBSERVATIONS INTS (28 1 3) ACTIONS INTS (7 1 4) REWARDS (-10000 -16.1) EXTRA [(0, 7), (1, 4), (2, 6), (3, 6), (4, 0), (5, 4), (6, 0)] BUDGET 100 by Majid Taleghan."));
		GeneticModelCreator g = new GeneticModelCreator(river);

		Observation o;
		Action a;

		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 2, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
	}

	/**
	 * Testrun 2 generated with the rl_glue and python environment
	 */
	public static void test2() {
		River river = new River(
				new TaskSpec(
						"VERSION RL-Glue-3.0 PROBLEMTYPE non-episodic DISCOUNTFACTOR 0.9 OBSERVATIONS INTS (28 1 3) ACTIONS INTS (7 1 4) REWARDS (-10000 -16.1) EXTRA [(0, 7), (1, 4), (2, 6), (3, 6), (4, 0), (5, 4), (6, 0)] BUDGET 100 by Majid Taleghan."));
		GeneticModelCreator g = new GeneticModelCreator(river);

		Observation o;
		Action a;

		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// // g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// // g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 2, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// // g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// // g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		// // g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);

	}
}
