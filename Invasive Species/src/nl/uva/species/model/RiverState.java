package nl.uva.species.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * Represents the state of a river given an observation as base river structure.
 */
public class RiverState {

    /** The river defining the structure */
    private final River mRiver;

    /** The observation used for this state */
    private final Observation mObservation;

    /** The reaches within this state */
    private final Set<Reach> mReaches = new HashSet<>();

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
        final Reach reach = new Reach(index, parent, Arrays.copyOfRange(mObservation.intArray, startPosition,
                startPosition + reachSize));
        mReaches.add(reach);

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
    public Set<Reach> getReaches() {
        return mReaches;
    }

    /**
     * Retrieves the root reach that all other reaches flow into
     * 
     * @return The state's root reach
     */
    public Reach getRootReach() {
        return mRootReach;
    }
}
