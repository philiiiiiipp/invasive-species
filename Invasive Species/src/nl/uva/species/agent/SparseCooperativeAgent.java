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
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public class SparseCooperativeAgent extends AbstractAgent {

    /** The learning rate alpha as described in (16) by (Kok & Vlassis, 2006) */
    private static final double LEARNING_RATE = 0.2;

    /** The discount factor gamma as described in (16) by (Kok & Vlassis, 2006) */
    private static final double DISCOUNT = 0.9;

    private River mRiver;
    private EnvModel mModel;

    private final Map<ReachKey, HashMap<Integer, Double>> mQ = new HashMap<>();

    public SparseCooperativeAgent() {}

    @Override
    public void init(final River river) {
        mRiver = river;
    }

    public void setModel(final EnvModel model) {
        mModel = model;

        trainQ();
    }

    @Override
    public Action start(final Observation observation) {
        final RiverState state = new RiverState(mRiver, observation);

        System.out.print("ACTION: ");

        final Action bestActions = new Action();
        bestActions.intArray = new int[mRiver.getNumReaches()];
        for (final Reach reach : state.getReaches()) {
            if (reach.getHabitatsEmpty() > 0) {
                bestActions.intArray[reach.getIndex()] = Utilities.ACTION_RESTORE;
            } else {
                bestActions.intArray[reach.getIndex()] = getBestAction(new ReachKey(reach));
            }
            System.out.print(bestActions.intArray[reach.getIndex()]);
        }

        System.out.println();

        return bestActions;
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
    public void message(Messages message) {}

    private void trainQ() {
        final int numReaches = mRiver.getNumReaches();
        final int numHabitats = numReaches * mRiver.getReachSize();
        mQ.clear();

        final int numStates = (int) (Math.pow(3, 5) * numReaches);
        int runs = 0;
        int runsUnchanged = 0;
        while (++runsUnchanged < 50) {
            final int qSize = mQ.size();
            // for (int runs = 0; runs < 500; ++runs) {
            // System.out.print(" " + runs);
            // if (runs % 20 == 0) {
            // System.out.println();
            // }

            final Observation observation = new Observation();
            observation.intArray = new int[numHabitats];
            for (int i = 0; i < numHabitats; ++i) {
                observation.intArray[i] = Utilities.RNG.nextInt(2) + 1;
            }
            final RiverState state = new RiverState(mRiver, observation);

            final Map<Reach, ReachKey> keys = new HashMap<>();
            for (final Reach reach : state.getReaches()) {
                keys.put(reach, new ReachKey(reach));
            }

            final Action randomActions = getRandomAction(state);

            for (final Reach reach : state.getReaches()) {
                final int reachIndex = reach.getIndex();
                final ReachKey reachKey = keys.get(reach);

                final List<Integer> actions = reach.getValidActions();
                for (final Integer action : actions) {
                    final double qValue = getQ(reachKey, action);

                    final Action localActions = new Action();
                    localActions.intArray = Arrays.copyOf(randomActions.intArray, numReaches);
                    localActions.intArray[reachIndex] = action;

                    final RiverState expectedNextState = mModel.getExpectedNextState(state, localActions);
                    // for (int j = 0; j < 10; ++j) {
                    // final RiverState expectedNextState = mModel.getPossibleNextState(state,
                    // localAction);

                    final double reward = mModel.getReachReward(expectedNextState.getReach(reachIndex))
                            + mModel.getSingleActionReward(reach, action);

                    final Action bestNextActions = mModel.getBestAction(expectedNextState);
                    final double maxNextQ = getQ(keys.get(expectedNextState.getReach(reachIndex)),
                            bestNextActions.intArray[reachIndex]);

                    putQ(reachKey, action, qValue + LEARNING_RATE * (reward + DISCOUNT * maxNextQ - qValue));
                    // }
                }
            }

            System.out.print(mQ.size() + " ");

            if (qSize != mQ.size()) {
                runsUnchanged = 0;
            }

            if (runs++ % 50 == 0) {
                System.out.println();
            }
        }
    }

    private void putQ(final ReachKey reachKey, final int action, final double value) {
        HashMap<Integer, Double> actionMap = mQ.get(reachKey);
        if (actionMap == null) {
            actionMap = new HashMap<Integer, Double>();
            mQ.put(reachKey, actionMap);
        }

        actionMap.put(action, value);
    }

    private double getQ(final ReachKey reachKey, final int action) {
        final HashMap<Integer, Double> actionMap = mQ.get(reachKey);
        if (actionMap == null) {
            return 0;
        }
        return (actionMap.containsKey(actionMap) ? actionMap.get(actionMap) : 0);
    }

    private Integer getBestAction(final ReachKey reachKey) {
        final HashMap<Integer, Double> actionMap = mQ.get(reachKey);
        if (actionMap == null) {
            return Utilities.ACTION_NOTHING;
        }

        Integer bestAction = null;
        Double bestQ = Double.NEGATIVE_INFINITY;
        for (final Integer action : actionMap.keySet()) {
            final Double Q = actionMap.get(action);
            if (Q > bestQ) {
                bestAction = action;
                bestQ = Q;
            }
        }

        return (bestAction != null ? bestAction : Utilities.ACTION_NOTHING);
    }

    private Action getRandomAction(final RiverState state) {
        final Action randomActions = new Action();
        randomActions.intArray = new int[mRiver.getNumReaches()];

        for (final Reach reach : state.getReaches()) {
            final List<Integer> validActions = reach.getValidActions();
            randomActions.intArray[reach.getIndex()] = validActions.get(Utilities.RNG.nextInt(validActions.size()));
        }

        return randomActions;
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

            if (reach.getNumHabitats() > habitatsInvaded * 2) {
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
            return mIndex == reachKey.mIndex && Objects.deepEquals(mCategories, reachKey.mCategories);
        }
    }

}
