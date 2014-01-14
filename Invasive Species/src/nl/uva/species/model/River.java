package nl.uva.species.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * Represents main structure of the river, but not its contents as those depend on the observations
 * within a certain state.
 */
public class River {

    /** The amount of habitats per reach */
    private final int mReachSize;

    /** The budget for actions per time step */
    private final double mBudget;

    /** The penalty of performing a bad action */
    private final double mPenalty;

    /** The mapping from parent to child reaches using indices */
    private final HashMap<Integer, Set<Integer>> mStructure = new HashMap<>();

    /** The index that specifies the root reach */
    private final int mRootIndex;

    /** The root node */
    private final int mRootNode;

    /**
     * Prepares a new river based on the task specification.
     * 
     * @param taskSpec
     *            The task specification for the experiment
     */
    public River(final TaskSpec taskSpec) {
        final String extraString = taskSpec.getExtraString();
        mReachSize = taskSpec.getNumDiscreteObsDims() / taskSpec.getNumDiscreteActionDims();

        final int budgetIndex = extraString.indexOf(" BUDGET ");

        // Parse the edges to determine the river's structure
        int rootNode = -1;
        final String edgeString = extraString.substring(0, budgetIndex);
        final Matcher matcher = Pattern.compile("\\((\\d+), ?(\\d+)\\)").matcher(edgeString);
        while (matcher.find()) {
            final int left = Integer.parseInt(matcher.group(1));
            final int right = Integer.parseInt(matcher.group(2));

            // Map the reaches from the right hand parent to the left hand child
            if (!mStructure.containsKey(right)) {
                mStructure.put(right, new HashSet<Integer>());
            }
            mStructure.get(right).add(left);

            // Keep track of the higher index as this is the root
            if (right > rootNode) {
                rootNode = right;
            }
        }

        mBudget = Double.parseDouble(extraString.substring(budgetIndex, extraString.indexOf(" by ")));

        mPenalty = taskSpec.getRewardRange().getMin();

        mRootNode = rootNode;

        // Set the root reach index as the left hand value of the highest right hand mapping
        mRootIndex = mStructure.get(rootNode).iterator().next();
    }

    /**
     * Retrieves the river with the observation put into the base structure.
     * 
     * @param observation
     *            The observation that determines the state
     * 
     * @return The state expressed in the given structure
     */
    public RiverState getRiverState(final Observation observation) {
        return new RiverState(this, observation);
    }

    /**
     * Retrieves the base structure of the river, defined as a mapping from parent reach indices to
     * its children.
     * 
     * @return The river structure as an index mapping
     */
    public HashMap<Integer, Set<Integer>> getStructure() {
        return mStructure;
    }

    /**
     * Retrieves the budget for actions per time step.
     * 
     * @return The budget
     */
    public double getBudget() {
        return mBudget;
    }

    /**
     * Retrieves the penalty of performing a bad action.
     * 
     * @return The penalty
     */
    public double getPenalty() {
        return mPenalty;
    }

    /**
     * Retrieves the amount of habitats per reach.
     * 
     * @return A reach's size
     */
    public int getReachSize() {
        return mReachSize;
    }

    /**
     * Retrieves the index of the reach that counts as root.
     * 
     * @return The root index
     */
    public int getRootIndex() {
        return mRootIndex;
    }

    /**
     * Get the amount of reaches within the river.
     * 
     * @return The amount of reaches
     */
    public int getNumReaches() {
        return mRootNode;
    }

    /**
     * Retrieves the index of the root node, the node the water flows trough wards
     * 
     * @return The root node index
     */
    public int getRootNode() {
        return mRootNode;
    }
}
