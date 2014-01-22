package nl.uva.species.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * Represents the state of a river given an observation as base river structure.
 */
public class RiverState {

    /** The river defining the structure */
    private final River mRiver;

    /** The observation used for this state */
    private final Observation mObservation;

    /** The amount of habitats invaded by Tamarisk plants in each reach */
    private final double[] mHabitatsInvaded;

    /** The amount of habitats containing natural plants in each reach */
    private final double[] mHabitatsNative;

    /** The amount of empty habitats in each reach */
    private final double[] mHabitatsEmpty;

    /** The reaches within this state */
    private final Map<Integer, Reach> mReaches = new HashMap<>();

    /** The root reach that all other reaches stream into */
    private final Reach mRootReach;

    /**
     * Prepares a new river state based on the base structure and observation.
     * 
     * @param river
     *            The river to use as base structure
     * @param observation
     *            The observation for this state
     */
    public RiverState(final River river, final Observation observation) {
        mRiver = river;
        mObservation = observation;
        mHabitatsInvaded = null;
        mHabitatsNative = null;
        mHabitatsEmpty = null;

        mRootReach = generateReachAt(river.getRootIndex(), null);
    }

    /**
     * Prepares a new river state based on the base structure and observation.
     * 
     * @param river
     *            The river to use as base structure
     * @param habitatsInvaded
     *            The amount of habitats invaded by Tamarisk plants in this reach
     * @param habitatsNatural
     *            The amount of habitats containing natural plants in this reach
     * @param habitatsEmpty
     *            The amount of empty habitats in this reach
     */
    public RiverState(final River river, final double[] habitatsInvaded, final double[] habitatsNative,
            final double[] habitatsEmpty) {
        mRiver = river;
        mObservation = null;
        mHabitatsInvaded = habitatsInvaded;
        mHabitatsNative = habitatsNative;
        mHabitatsEmpty = habitatsEmpty;

        mRootReach = generateReachAt(river.getRootIndex(), null);
    }

    /**
     * Recursively creates and maps the reaches that belong in the state. Should not be called more
     * than once.
     * 
     * @param index
     *            The index of the reach to generate
     * @param parent
     *            The reach's parent in the river structure
     * 
     * @return The reach that was generated
     */
    private Reach generateReachAt(final int index, final Reach parent) {
        final int reachSize = mRiver.getReachSize();
        final int startPosition = reachSize * index;

        // Create the reach object and add it to the set of reaches
        final Reach reach;
        if (mObservation != null) {
            reach = new Reach(index, parent, Arrays.copyOfRange(mObservation.intArray, startPosition, startPosition
                    + reachSize));
        } else {
            reach = new Reach(index, parent, mHabitatsInvaded[index], mHabitatsNative[index], mHabitatsEmpty[index]);
        }
        mReaches.put(index, reach);

        // Generate the reach's children
        if (mRiver.getStructure().containsKey(index)) {
            for (final int childIndex : mRiver.getStructure().get(index)) {
                generateReachAt(childIndex, reach);
            }
        }

        return reach;
    }

    /**
     * Retrieves the river that this state was based on.
     * 
     * @return The state's river
     */
    public River getRiver() {
        return mRiver;
    }

    /**
     * Retrieves the observation that this state was based on.
     * 
     * @return The state's observation
     */
    public Observation getObservation() {
        return mObservation;
    }

    /**
     * Retrieves all reaches within this state.
     * 
     * @return The state's reaches
     */
    public Collection<Reach> getReaches() {
        return mReaches.values();
    }

    /**
     * Retrieves the root reach that all other reaches flow into
     * 
     * @return The state's root reach
     */
    public Reach getRootReach() {
        return mRootReach;
    }

    /**
     * Retrieves the reach corresponding to the given index.
     * 
     * @param index
     *            The index of the reach
     * 
     * @return The reach with the given index
     */
    public Reach getReach(final int index) {
        return mReaches.get(index);
    }
}
