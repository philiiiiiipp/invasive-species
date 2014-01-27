package nl.uva.species.agent;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.Reach;
import nl.uva.species.model.RiverState;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.types.Action;

public abstract class AbstractLearnerAgent extends AbstractAgent {

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
     * @param model
     *            The current anticipated model
     * @return The action which will presumably give us the most knowledge about the real model
     */
    protected Action getHeuristicNextAction(final RiverState rState, final int currentTimestep,
            final int nothingActionAmount, final int eradicateActionAmount,
            final int eradicateRestoreActionAmount, final EnvModel model) {

        int[] resultAction = new int[rState.getReaches().size()];
        int total = nothingActionAmount + eradicateActionAmount + eradicateRestoreActionAmount;
        double budget = Math.abs(model.getRiver().getBudget());

        for (Reach reach : rState.getReaches()) {

            if (currentTimestep == 0) {
                // First action, always restore
                if (reach.getHabitatsEmpty() > 0) {
                    resultAction[reach.getIndex()] = Utilities.ACTION_RESTORE;
                } else {
                    resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
                }
            } else if (currentTimestep % total < nothingActionAmount) {
                // Nothing
                resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;

            } else if (currentTimestep % total < eradicateActionAmount) {
                // Eradicate
                if (reach.getValidActions().contains(Utilities.ACTION_ERADICATE)) {
                    resultAction[reach.getIndex()] = Utilities.ACTION_ERADICATE;
                } else {
                    resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
                }
            } else {
                // Eradicate + Restore
                if (reach.getValidActions().contains(Utilities.ACTION_ERADICATE_RESTORE)) {
                    resultAction[reach.getIndex()] = Utilities.ACTION_ERADICATE_RESTORE;
                } else {
                    resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
                }
            }

            final double cost = Math.abs(model.getSingleActionReward(reach,
                    resultAction[reach.getIndex()]));
            if (cost <= budget) {
                budget -= cost;
            } else {
                // We cannot afford this action
                resultAction[reach.getIndex()] = Utilities.ACTION_NOTHING;
            }
        }

        Action action = new Action();
        action.intArray = resultAction;
        return action;
    }
}
