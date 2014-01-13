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

    /** The mapping from parent to child reaches using indices */
    private final HashMap<Integer, Set<Integer>> mStructure = new HashMap<>();

    /** The index that specifies the root reach */
    private final int mRootIndex;

    /**
     * Prepares a new river based on the task specification.
     * 
     * @param taskSpec
     *            The task specification for the experiment
     */
    public River(final TaskSpec taskSpec) {
        final String extraString = taskSpec.getExtraString();
        mReachSize = taskSpec.getNumDiscreteObsDims() / taskSpec.getNumDiscreteActionDims();

        // Parse the edges to determine the river's structure
        int rootIndex = -1;
        final String edgeString = extraString.substring(0, extraString.indexOf(" BUDGET"));
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
            if (right > rootIndex) {
                rootIndex = right;
            }
        }

        // Set the root reach index as the left hand value of the highest right hand mapping
        mRootIndex = mStructure.get(rootIndex).iterator().next();
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

}
