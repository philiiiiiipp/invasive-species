package nl.uva.species.agent;

import java.util.Arrays;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.Reach;
import nl.uva.species.model.RiverState;
import nl.uva.species.utils.Pair;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.types.Action;

public abstract class AbstractLearnerAgent extends AbstractAgent {

	/**
	 * Brute-force method to determine the best possible action, considering a horizon of 1
	 * 
	 * @param riverState
	 *            The current state
	 * @param model
	 *            The current model
	 * @return The best possible action
	 */
	protected Action getBestAction(final RiverState riverState, final EnvModel model) {
		return getBestAction(riverState, 0, new int[riverState.getReaches().size()], model).getLeft();
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
	 * @param model
	 *            The current model
	 * @return Returns the best Action with its rewards value
	 */
	private Pair<Action, Double> getBestAction(final RiverState riverState, final int reachPosition,
			final int[] action, final EnvModel model) {
		if (reachPosition == riverState.getReaches().size()) {
			Action current = new Action();
			current.intArray = action;

			double stateReward = model.getExpectedNextStateReward(riverState, current);
			double actionReward = model.getActionReward(riverState, current);
			System.out.println(stateReward + " Action: " + actionReward);

			stateReward *= 3;
			stateReward += actionReward;

			return new Pair<Action, Double>(current, stateReward);
		}

		Reach currentReach = riverState.getReach(reachPosition);
		Pair<Action, Double> temp = null, resultAction = null;
		for (Integer a : currentReach.getValidActions()) {
			action[currentReach.getIndex()] = a;

			temp = getBestAction(riverState, reachPosition + 1, action, model);

			if (resultAction == null || resultAction.getRight() < temp.getRight()) {
				Action best = new Action();
				best.intArray = Arrays.copyOf(temp.getLeft().intArray, temp.getLeft().intArray.length);

				resultAction = new Pair<Action, Double>(best, temp.getRight());
			}
		}

		return resultAction;
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
	protected Action getHeuristicNextAction(final RiverState rState, final int currentTimestep,
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
}
