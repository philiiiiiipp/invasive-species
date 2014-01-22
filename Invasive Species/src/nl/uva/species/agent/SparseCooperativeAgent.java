package nl.uva.species.agent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

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

public class SparseCooperativeAgent implements AgentInterface {

    /** The learning rate alpha as described in (16) by (Kok & Vlassis, 2006) */
    private static final double LEARNING_RATE = 0.2;
    
    /** The discount factor gamma as described in (16) by (Kok & Vlassis, 2006) */
    private static final double DISCOUNT = 0.2;

    private boolean mPolicyFrozen;
    private boolean mExploringFrozen;

    private River mRiver;
    private EnvModel mModel;

    private final Map<ReachKey, HashMap<Integer, Double>> mQ = new HashMap<>();

    public SparseCooperativeAgent() {}

    public SparseCooperativeAgent(final EnvModel model) {
        mModel = model;
    }

    @Override
    public void agent_init(final String taskSpecification) {
        if (mModel == null) {
            mRiver = new River(new TaskSpec(taskSpecification));
            mModel = new EnvModel(mRiver, false);
        }
    }

    @Override
    public Action agent_start(final Observation observation) {

        Action defaultAction = new Action();
        defaultAction.intArray = new int[7];
        Arrays.fill(defaultAction.intArray, Utilities.ACTION_ERADICATE_RESTORE);

        EnvModel model = new EnvModel(mRiver, false);
        RiverState state = new RiverState(mRiver, observation);

        return null;
    }

    @Override
    public Action agent_step(final double reward, final Observation observation) {
        return agent_start(observation);
    }

    @Override
    public void agent_end(final double reward) {
        System.out.println("Agent_End");

    }

    @Override
    public void agent_cleanup() {
        System.out.println("Agent_Cleanup");

    }

    @Override
    public String agent_message(final String inMessage) {
        System.out.println("Agent_Message: " + inMessage);

        if (inMessage.startsWith("freeze learning")) {
            mPolicyFrozen = true;
            return "message understood, policy frozen";
        }

        if (inMessage.startsWith("unfreeze learning")) {
            mPolicyFrozen = false;
            return "message understood, policy unfrozen";
        }

        if (inMessage.startsWith("freeze exploring")) {
            mExploringFrozen = true;
            return "message understood, exploring frozen";
        }

        if (inMessage.startsWith("unfreeze exploring")) {
            mExploringFrozen = false;
            return "message understood, exploring frozen";
        }

        return "Invasive agent does not understand your message.";
    }

    private Action getAction(final RiverState state) {
        
        final Map<Reach, ReachKey> keys = new HashMap<>();
        for (final Reach reach : state.getReaches()) {
            keys.put(reach, new ReachKey(reach));
        }

        for (final Reach reach : state.getReaches()) {
            final ReachKey reachKey = keys.get(reach);
            
            final List<Integer> actions = reach.getValidActions();
            for (final Integer action : actions) {
                final double Q = getQ(reachKey, action);
                putQ(reachKey, action, Q + LEARNING_RATE * (getReward() + DISCOUNT * getNextQ() - Q));
            }
        }

        return null;
    }

    private double getQ(final ReachKey reachKey, final int action) {
        final HashMap<Integer, Double> actionMap = mQ.get(reachKey);
        if (actionMap == null) {
            return 0;
        }
        return actionMap.get(actionMap);
    }

    private void putQ(final ReachKey reachKey, final int action, final double value) {
        HashMap<Integer, Double> actionMap = mQ.get(reachKey);
        if (actionMap == null) {
            actionMap = new HashMap<Integer, Double>();
            mQ.put(reachKey, actionMap);
        }

        actionMap.put(action, value);
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

            if (reach.getNumHabitats() > reach.getNumHabitats() * 2) {
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

            return mIndex == reachKey.mIndex && mCategories.equals(reachKey.mCategories);
        }
    }

    /**
     * Load our agent with the AgentLoader and automatically connect to the rl_glue server.
     * 
     * @param args
     */
    public static void main(final String[] args) {
        AgentLoader theLoader = new AgentLoader(new SparseCooperativeAgent());
        theLoader.run();
    }

}
