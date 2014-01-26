package nl.uva.species.agent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.Reach;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;
import nl.uva.species.utils.Messages;
import nl.uva.species.utils.Pair;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * An agent that uses a sparse setting to make reaches act like cooperative agents. This class
 * implements Agent Based Decomposition as described in (Kok & Vlassis, 2006).
 */
public class SparseCooperativeAgent extends AbstractAgent {

    /** The amount of planning steps to not yield new local states before planning is done */
    private static final int PLAN_LIMIT = 200;

    /** The learning rate alpha as described in (16) by (Kok & Vlassis, 2006) */
    private static final double LEARNING_RATE = 0.2;

    /** The discount factor gamma as described in (16) by (Kok & Vlassis, 2006) */
    private static final double DISCOUNT = 0.9;

    /** Whether or not to print out the actions performed */
    private static final boolean PRINT_ACTIONS = false;

    /** The river in which the agent handles */
    private River mRiver;

    /** The model containing the environment's parameters */
    private EnvModel mModel;

    /** A cache of reach keys to use in the Q mapping */
    final Map<Reach, ReachKey> mKeyCache = new HashMap<>();

    /** The Q values */
    private final Map<ReachKey, HashMap<Integer, Double>> mQ = new HashMap<>();

    @Override
    public void init(final River river) {
        mRiver = river;
    }

    /**
     * Sets the model to base the Q values on. Automatically trains Q to suit the new model.
     * 
     * @param model
     *            The model containing the environment's parameters
     */
    public void setModel(final EnvModel model) {
        if (mModel != null && mModel.compareTo(model) == 0) {
            // Model did not change, no need for recalculation of Q
            System.out.println("Same model, not recalculating Q");
            return;
        }

        System.out.println("Recalculating Q");
        mModel = model;
        trainQ();
    }

    @Override
    public Action start(final Observation observation) {
        final RiverState state = new RiverState(mRiver, observation);
        final double budget = mRiver.getBudget();
        double actionReward = 0;

        if (PRINT_ACTIONS) System.out.print("BEST ACTION: ");

        // Find the action that maximises the Q sum
        final Action bestActions = new Action();
        bestActions.intArray = new int[mRiver.getNumReaches()];
        for (final Reach reach : state.getReaches()) {
            final int bestAction;

            // Restore if reasonable so that we do not have to include it in the Q calculations
            if (reach.getHabitatsEmpty() > reach.getHabitatsInvaded()) {
                bestAction = Utilities.ACTION_RESTORE;
            } else {
                bestAction = getBestAction(reach);

                if (PRINT_ACTIONS) {
                    if (!mQ.containsKey(getKey(reach))) {
                        System.out.print("_");
                    }
                }
            }

            if (PRINT_ACTIONS) System.out.print(bestAction);

            bestActions.intArray[reach.getIndex()] = bestAction;

            actionReward += mModel.getSingleActionReward(reach, bestAction);

        }

        // Return the given actions if it's within the budget, otherwise the best constrained action
        if (Math.abs(actionReward) < budget) {
            if (PRINT_ACTIONS) System.out.println();
            return bestActions;
        } else {
            final Action bestConstrainedActions = getBestConstrainedAction(state);
            if (PRINT_ACTIONS) {
                System.out.print(" - OVER BUDGET, SUBSTITUTING: ");
                for (final Integer bestConstrainedAction : bestConstrainedActions.intArray) {
                    System.out.print(bestConstrainedAction);
                }
                System.out.println();
            }
            return bestConstrainedActions;
        }
    }

    @Override
    public Action step(final double reward, final Observation observation) {
        return start(observation);
    }

    @Override
    public void end(final double reward) {}

    @Override
    public void cleanup() {}

    @Override
    public void message(final Messages message) {}

    /**
     * Train the Q function based on the current model.
     */
    private void trainQ() {
        final int numReaches = mRiver.getNumReaches();
        final int numHabitats = numReaches * mRiver.getReachSize();

        // Reset Q to remove traces of old models
        mQ.clear();

        int runs = 0;
        int runsUnchanged = 0;
        while (++runsUnchanged < PLAN_LIMIT) {
            final int qSize = mQ.size();

            // Generate a random state
            final Observation observation = new Observation();
            observation.intArray = new int[numHabitats];
            for (int i = 0; i < numHabitats; ++i) {
                observation.intArray[i] = Utilities.RNG.nextInt(2) + 1;
            }
            final RiverState state = new RiverState(mRiver, observation);

            // Cache keys for each reach
            putKeys(state);

            // Generate global actions
            final Action randomActions = getRandomAction(state);

            // Update the local Q(s,a) value for each reach
            for (final Reach reach : state.getReaches()) {
                final int reachIndex = reach.getIndex();
                final ReachKey reachKey = getKey(reach);

                // Update the local Q(s,a) value for each action
                final List<Integer> actions = reach.getValidActions();
                actions.remove(new Integer(Utilities.ACTION_RESTORE));
                for (final Integer action : actions) {
                    final double qValue = getQ(reachKey, action);

                    // Find the expected next state with the global random actions and local action
                    final Action localActions = new Action();
                    localActions.intArray = Arrays.copyOf(randomActions.intArray, numReaches);
                    localActions.intArray[reachIndex] = action;
                    final RiverState expectedNextState = mModel.getExpectedNextState(state,
                            localActions);

                    // Get the reward of the reward of transitioning to the expected state
                    final double reward = mModel.getReachReward(expectedNextState
                            .getReach(reachIndex)) + mModel.getSingleActionReward(reach, action);

                    // Get the Q value of the optimal action in the expected state
                    final Action bestNextActions = mModel.getBestAction(expectedNextState);
                    final double maxNextQ = getQ(getKey(expectedNextState.getReach(reachIndex)),
                            bestNextActions.intArray[reachIndex]);

                    // Update Q
                    putQ(reachKey, action, qValue + LEARNING_RATE
                            * (reward + DISCOUNT * maxNextQ - qValue));
                }
            }

            if (PRINT_ACTIONS) System.out.print(mQ.size() + " ");

            // Keep track of changes
            if (qSize != mQ.size()) {
                runsUnchanged = 0;
            }

            if (runs++ % 50 == 0) {
                if (PRINT_ACTIONS) System.out.println();
            }
        }
        if (PRINT_ACTIONS) System.out.println();
    }

    /**
     * Converts the reaches in the given state to keys and caches them for later use.
     * 
     * @param state
     *            The state to cache the reaches for
     */
    private void putKeys(final RiverState state) {
        for (final Reach reach : state.getReaches()) {
            if (!mKeyCache.containsKey(reach)) {
                mKeyCache.put(reach, new ReachKey(reach));
            }
        }
    }

    /**
     * Retrieves a cached reach key and creates the cache if needed.
     * 
     * @param reach
     *            The reach to get the key for
     * 
     * @return The reach's key
     */
    private ReachKey getKey(final Reach reach) {
        if (!mKeyCache.containsKey(reach)) {
            mKeyCache.put(reach, new ReachKey(reach));
        }
        return mKeyCache.get(reach);
    }

    /**
     * Finds a mapped reach key closest to the given one.
     * 
     * @param reachKey
     *            The reach key to compare to
     * 
     * @return The reach key closest to the given one or null if none was found
     */
    private ReachKey getClosestKey(final ReachKey reachKey) {
        ReachKey bestMatch = null;
        int bestMatchScore = Integer.MAX_VALUE;

        final double upstreamRate = mModel.getUpstreamRate();
        final double downstreamRate = mModel.getDownstreamRate();

        final double parentRate = Math.pow(upstreamRate, 2);
        final double childRate = Math.pow(downstreamRate, 2);
        final double siblingRate = upstreamRate * downstreamRate;

        for (final ReachKey key : mQ.keySet()) {
            int matchScore = (reachKey.mIndex == key.mIndex ? 0 : 11);

            matchScore += Math.abs(reachKey.mCategories[0] - key.mCategories[0]);
            matchScore += Math.abs(reachKey.mCategories[1] - key.mCategories[1]) * parentRate;
            matchScore += Math.abs(reachKey.mCategories[2] - key.mCategories[2]) * siblingRate;
            matchScore += Math.abs(reachKey.mCategories[3] - key.mCategories[3]) * childRate;
            matchScore += Math.abs(reachKey.mCategories[4] - key.mCategories[4]) * childRate;

            if (matchScore < bestMatchScore) {
                bestMatch = key;
                bestMatchScore = matchScore;
            }
        }

        return bestMatch;
    }

    /**
     * Saves a Q(s,a) value.
     * 
     * @param reachKey
     *            The key of reach s
     * @param action
     *            The action a
     * @param value
     *            The value for the s,a pair
     */
    private void putQ(final ReachKey reachKey, final int action, final double value) {
        HashMap<Integer, Double> actionMap = mQ.get(reachKey);
        if (actionMap == null) {
            actionMap = new HashMap<Integer, Double>();
            mQ.put(reachKey, actionMap);
        }

        actionMap.put(action, value);
    }

    /**
     * Checks if Q values are mapped for reach s.
     * 
     * @param reach
     *            The reach s
     * 
     * @return True iff the given reach has Q values mapped to it
     */
    private boolean hasQ(final Reach reach) {
        return mQ.containsKey(getKey(reach));
    }

    /**
     * Retrieves a mapped Q(s,a) value.
     * 
     * @param reachKey
     *            The key for reach s
     * @param action
     *            The action a
     * 
     * @return The mapped Q value or 0 is none was found
     */
    private double getQ(final ReachKey reachKey, final int action) {
        final HashMap<Integer, Double> actionMap = mQ.get(reachKey);
        if (actionMap == null) {
            return 0;
        }
        return (actionMap.containsKey(action) ? actionMap.get(action) : 0);
    }

    /**
     * Retrieves a mapped Q(s,a) value.
     * 
     * @param reachKey
     *            The key for reach s
     * @param action
     *            The action a
     * 
     * @return The mapped Q value or 0 is none was found
     */
    private double getClosestQ(final ReachKey reachKey, final int action) {
        final HashMap<Integer, Double> actionMap = mQ.get(reachKey);

        // If a key is not found, return the Q value of the closest match
        if (actionMap == null) {
            final ReachKey bestMatch = getClosestKey(reachKey);
            return (bestMatch != null ? getQ(bestMatch, action) : 0);
        }
        return (actionMap.containsKey(action) ? actionMap.get(action) : 0);
    }

    /**
     * Retrieves the best action for a reach according to Q(s,a).
     * 
     * @param reachKey
     *            The key of the reach s
     * 
     * @return The best action for the given reach or NOTHING if the reach has no Q values
     */
    private Integer getBestAction(final Reach reach) {
        final ReachKey reachKey = getKey(reach);
        final HashMap<Integer, Double> actionMap;

        // Try to get the action map for the given key, or the closest one if given isn't mapped
        if (mQ.containsKey(reachKey)) {
            actionMap = mQ.get(reachKey);
        } else {
            final ReachKey closestKey = getClosestKey(reachKey);
            if (closestKey == null) {
                return Utilities.ACTION_NOTHING;
            }
            actionMap = mQ.get(closestKey);
        }

        // Check all mapped actions for the one with the best Q value
        Integer bestAction = null;
        Double bestQ = Double.NEGATIVE_INFINITY;
        for (final Integer action : reach.getValidActions()) {
            final Double Q = actionMap.get(action);
            if (Q != null && Q > bestQ) {
                bestAction = action;
                bestQ = Q;
            }
        }

        return (bestAction != null ? bestAction : Utilities.ACTION_NOTHING);
    }

    /**
     * Retrieves random actions for each of the state's reaches.
     * 
     * @param state
     *            The state for get actions for
     * 
     * @return A random action to be performed on the state
     */
    private Action getRandomAction(final RiverState state) {
        final Action randomActions = new Action();
        randomActions.intArray = new int[mRiver.getNumReaches()];

        for (final Reach reach : state.getReaches()) {
            final List<Integer> validActions = reach.getValidActions();
            randomActions.intArray[reach.getIndex()] = validActions.get(Utilities.RNG
                    .nextInt(validActions.size()));
        }

        return randomActions;
    }

    /**
     * Brute-force method to determine the best possible action within the budget.
     * 
     * @param riverState
     *            The current state
     * 
     * @return The best possible action
     */
    public Action getBestConstrainedAction(final RiverState riverState) {
        return getBestConstrainedAction(riverState, 0, new int[mRiver.getNumReaches()]).getLeft();
    }

    /**
     * Search recursive through all possible actions to determine the best action within the budget.
     * 
     * @param state
     *            The current state
     * @param reachPosition
     *            The position which needs to be set next
     * @param action
     *            The so far action list
     * 
     * @return Returns the best action with its rewards value or null if it crosses the budget
     */
    private Pair<Action, Double> getBestConstrainedAction(final RiverState state,
            final int reachPosition, final int[] action) {
        final int numReaches = mRiver.getNumReaches();

        if (reachPosition == numReaches) {
            final double budget = mRiver.getBudget();

            final Action currentAction = new Action();
            currentAction.intArray = action;

            // Find the reward and Q sum of this action
            double actionReward = 0;
            double qSum = 0;
            for (int i = 0; i < numReaches; ++i) {
                final Reach reach = state.getReach(i);
                actionReward += mModel.getSingleActionReward(reach, currentAction.intArray[i]);

                // If the action crosses the budget, it's not valid
                if (Math.abs(actionReward) > budget) {
                    return null;
                }

                qSum += getClosestQ(getKey(reach), currentAction.intArray[i]);
            }

            return new Pair<Action, Double>(currentAction, qSum);
        }

        // Try every action for this reach
        final Reach currentReach = state.getReach(reachPosition);
        final List<Integer> validActions = currentReach.getValidActions();
        validActions.remove(new Integer(Utilities.ACTION_RESTORE));
        Pair<Action, Double> tempAction = null, resultAction = null;
        for (final Integer validAction : validActions) {
            action[reachPosition] = validAction;

            tempAction = getBestConstrainedAction(state, reachPosition + 1, action);

            if (tempAction != null
                    && (resultAction == null || resultAction.getRight() < tempAction.getRight())) {
                final Action bestAction = new Action();
                bestAction.intArray = Arrays.copyOf(tempAction.getLeft().intArray, numReaches);

                resultAction = new Pair<Action, Double>(bestAction, tempAction.getRight());
            }
        }

        return resultAction;
    }

    /**
     * A class to serve as key for a reach to be mapped within a local state space.
     */
    private static class ReachKey {

        /** The initial hash value; must be prime */
        private static final int HASH_SEED = 7;

        /** The hash offset for following numbers; must be prime */
        private static final int HASH_OFFSET = 31;

        /** The index of the reach */
        private final int mIndex;

        /** The reach categories for the reach and its environment */
        private final int[] mCategories = new int[5];

        /**
         * Prepares a key for a reach to be mapped within a local state space.
         * 
         * @param reach
         *            The reach to map
         */
        public ReachKey(final Reach reach) {
            mIndex = reach.getIndex();

            mCategories[0] = getInvasionCategory(reach);

            // Add the parent category
            final Reach parent = reach.getParent();
            if (parent != null) {
                mCategories[1] = getInvasionCategory(parent);

                // Add the sibling category
                final Set<Reach> siblings = parent.getChildren();
                for (final Reach sibling : siblings) {
                    if (sibling != reach) {
                        mCategories[2] = getInvasionCategory(sibling);
                        break;
                    }
                }
            }

            // Add the children's categories
            int pos = 3;
            final Set<Reach> children = reach.getChildren();
            for (final Reach child : children) {
                mCategories[pos++] = getInvasionCategory(child);
            }
        }

        /**
         * Determines which category a reach belongs to.
         * 
         * @param reach
         *            The reach
         * 
         * @return The category for the reach
         */
        private static int getInvasionCategory(final Reach reach) {
            final double habitatsInvaded = reach.getHabitatsInvaded();

            // Category 0 for a likely empty reach
            if (habitatsInvaded < 0.5) {
                return 0;
            }

            if (reach.getNumHabitats() >= habitatsInvaded * 2) {
                // Category 1 for little Tamarisk trees
                return 1;
            } else {
                // Category 2 for many Tamarisk trees
                return 2;
            }
        }

        @Override
        public int hashCode() {
            int intHash = HASH_SEED;
            intHash += HASH_OFFSET * mIndex;
            for (int i = 0; i < mCategories.length; ++i) {
                intHash += HASH_OFFSET * (mCategories[i] + (i + 1) * 10);
            }
            return intHash;
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof ReachKey)) {
                return false;
            }

            final ReachKey reachKey = (ReachKey) other;
            return mIndex == reachKey.mIndex
                    && Objects.deepEquals(mCategories, reachKey.mCategories);
        }
    }

}
