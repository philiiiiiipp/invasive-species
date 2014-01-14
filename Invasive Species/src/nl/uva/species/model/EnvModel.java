package nl.uva.species.model;

import java.util.Arrays;
import java.util.Set;

import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public class EnvModel {

    /** The threshold for when we believe exogenous germination is activated */
    private final static double EXO_ACTIVATED_THRESHOLD = 0.98;

    /** The river this model is specific to */
    private River mRiver;

    /** The cost per invaded reach */
    private double mCostInvadedReach = 10;

    /** The cost per habitat containing a Tamarisk */
    private double mCostHabitatTamarisk = 0.1;

    /** The cost per empty habitat */
    private double mCostHabitatEmpty = 0.05;

    /** The consistent cost of one eradication */
    private double mCostEradicate = 0.5;

    /** The consistent cost of one restoration, and for a eradicating and restoring */
    private double mCostRestorate = 0.9;

    /** The variable cost for each Tamarisk plant attempted to eradicate */
    private double mCostVariableEradicate = 0.4;

    /** The variable cost for each native plant attempted to eradicate */
    private double mCostVariableRestorate = 0.4;

    /** The variable cost for each Tamarisk plant attempted to eradicate and restore */
    private double mCostVariableEradicateRestorate = 0.8;

    /** Default value for each reach that there is exogenous germination */
    private double mDefaultExoToEndoRatio = 0.8;

    /** The chance for each reach that there is exogenous germination as opposed to endogenous */
    private double[] mExoToEndoRatio;

    /** Default value for each reach that a Tamarisk plant grows from exogenous germination */
    private double mDefaultExoTamarisk = 0.7;

    /** The chance for each reach that a Tamarisk plant grows from exogenous germination */
    private double[] mExoTamarisk;

    /** The chance that a native plant grows from endogenous germination */
    private double mEndoTamarisk = 0.6;

    /** The factor affecting the chance that trees spread to children */
    private double mUpstreamRate = 0.1;

    /** The factor affecting the chance that trees spread to parents */
    private double mDownstreamRate = 0.5;

    /** The chance that eradication succeeds */
    private double mEradicationRate = 0.85;

    /** The chance that restoration succeeds */
    private double mRestorationRate = 0.65;

    /** The chance that a Tamarisk plant randomly dies */
    private double mDeathRateTamarisk = 0.2;

    /** The chance that a native plant randomly dies */
    private double mDeathRateNative = 0.2;

    /**
     * Prepares a model for the given river.
     * 
     * @param river
     *            The river to base the model on
     */
    public EnvModel(final River river) {
        mRiver = river;

        // Set default values for vectors
        mExoToEndoRatio = new double[river.getNumReaches()];
        Arrays.fill(mExoToEndoRatio, mDefaultExoToEndoRatio);

        mExoTamarisk = new double[river.getNumReaches()];
        Arrays.fill(mExoTamarisk, mDefaultExoTamarisk);
    }

    /**
     * Retrieves a randomly generated possible next state based on the model for the given state,
     * performing the specified actions.
     * 
     * @param state
     *            The starting state
     * @param actions
     *            The action to perform
     * 
     * @return A random next possible state
     */
    public RiverState getPossibleNextState(final RiverState state, final Action actions) {
        final int reachSize = mRiver.getReachSize();
        final int[] newHabitats = new int[state.getReaches().size() * reachSize];

        // Perform the action on the different reaches
        for (final Reach reach : state.getReaches()) {
            final int[] habitats = reach.getHabitats();

            final int action = actions.intArray[reach.getIndex()];

            for (int i = 0; i < habitats.length; ++i) {
                final double random = Utilities.RNG.nextDouble();

                // Perform action on each habitat in reach
                switch (action) {
                case Utilities.ACTION_ERADICATE:
                    // During eradication, both type of plants may die
                    switch (habitats[i]) {
                    case Utilities.HABITAT_NATIVE:
                        if (random < mDeathRateNative) {
                            habitats[i] = Utilities.HABITAT_EMPTY;
                        }
                        break;
                    case Utilities.HABITAT_INVADED:
                        if (random < mEradicationRate) {
                            habitats[i] = Utilities.HABITAT_EMPTY;
                        }
                        break;
                    }

                    break;

                case Utilities.ACTION_RESTORE:
                    // During restoration, empty spaces may come to life and plants may die
                    switch (habitats[i]) {
                    case Utilities.HABITAT_EMPTY:
                        if (random < mRestorationRate) {
                            habitats[i] = Utilities.HABITAT_NATIVE;
                        }
                        break;
                    case Utilities.HABITAT_NATIVE:
                        if (random < mDeathRateNative) {
                            habitats[i] = Utilities.HABITAT_EMPTY;
                        }
                        break;
                    case Utilities.HABITAT_INVADED:
                        if (random < mDeathRateTamarisk) {
                            habitats[i] = Utilities.HABITAT_EMPTY;
                        }
                        break;
                    }
                    break;

                case Utilities.ACTION_ERADICATE_RESTORE:
                    // During eradication, invaded habitats may die and/or come to life and native
                    // species may die
                    switch (habitats[i]) {
                    case Utilities.HABITAT_NATIVE:
                        if (random < mDeathRateNative) {
                            habitats[i] = Utilities.HABITAT_EMPTY;
                        }
                        break;
                    case Utilities.HABITAT_INVADED:
                        if (random < mEradicationRate + (1 - mRestorationRate)) {
                            habitats[i] = Utilities.HABITAT_EMPTY;
                        } else if (random < mEradicationRate) {
                            habitats[i] = Utilities.HABITAT_NATIVE;
                        }
                        break;
                    }
                    break;

                case Utilities.ACTION_NOTHING:
                    // When doing nothing, both type of plants may die
                    switch (habitats[i]) {
                    case Utilities.HABITAT_NATIVE:
                        if (random < mDeathRateNative) {
                            habitats[i] = Utilities.HABITAT_EMPTY;
                        }
                        break;
                    case Utilities.HABITAT_INVADED:
                        if (random < mDeathRateTamarisk) {
                            habitats[i] = Utilities.HABITAT_EMPTY;
                        }
                        break;
                    }
                }

                // Update the habitats with the new reach values
                newHabitats[reach.getIndex() * reachSize + i] = habitats[i];
            }
        }

        // Create a new state based on the new habitats as observation
        final Observation newObservation = new Observation();
        newObservation.intArray = newHabitats;
        final RiverState newState = new RiverState(state.getRiver(), newObservation);

        // Germination of empty habitats
        for (final Reach reach : newState.getReaches()) {
            // Skip full reaches
            if (reach.getHabitatsEmpty() == 0) {
                continue;
            }

            final int index = reach.getIndex();

            final double endoToExoRatio = (1 - mExoToEndoRatio[index]);

            final double exoTamariskWeight = mExoToEndoRatio[index] * mExoTamarisk[index];
            final double exoNativeWeight = mExoToEndoRatio[index] * (1 - mExoTamarisk[index]);

            final double endoTamarisWeight = endoToExoRatio * mEndoTamarisk;
            final double endoNativeWeight = endoToExoRatio * (1 - mEndoTamarisk);

            // Calculate the reproduction scores for Tamarisk and native trees
            int tamariskScore = reach.getHabitatsInvaded();
            int nativeScore = reach.getHabitatsNative();
            final Reach parent = reach.getParent();
            if (parent != null) {
                // Add the parent's scores
                tamariskScore += parent.getHabitatsInvaded() * Math.pow(mUpstreamRate, 2);
                nativeScore += parent.getHabitatsInvaded() * Math.pow(mUpstreamRate, 2);

                // Add the sibling's scores
                final Set<Reach> siblings = parent.getChildren();
                for (final Reach sibling : siblings) {
                    if (sibling != reach) {
                        tamariskScore += sibling.getHabitatsInvaded() * mUpstreamRate * mDownstreamRate;
                        nativeScore += sibling.getHabitatsInvaded() * mUpstreamRate * mDownstreamRate;
                    }
                }
            }
            // Add the children's scores
            final Set<Reach> children = reach.getChildren();
            for (final Reach child : children) {
                tamariskScore += child.getHabitatsInvaded() * Math.pow(mDownstreamRate, 2);
                nativeScore += child.getHabitatsInvaded() * Math.pow(mDownstreamRate, 2);
            }

            // Determine the chance of each plant and normalise
            final double tamariskChance = exoTamariskWeight + endoTamarisWeight * tamariskScore / (5 * reachSize);
            final double nativeChance = exoNativeWeight + endoNativeWeight * nativeScore / (5 * reachSize);
            final double chanceSum = tamariskChance + nativeChance;
            final double tamariskChanceNorm = tamariskChance / chanceSum;

            // Update the habitats with the new regrown tree
            final int[] habitats = reach.getHabitats();
            for (int i = 0; i < habitats.length; ++i) {
                if (habitats[i] == Utilities.HABITAT_EMPTY) {
                    habitats[i] = (Utilities.RNG.nextDouble() < tamariskChanceNorm ? Utilities.HABITAT_INVADED
                            : Utilities.HABITAT_NATIVE);
                    newHabitats[index * reachSize + i] = habitats[i];
                }
            }
        }

        // Create a new state based on the new habitats as observation
        final Observation finalObservation = new Observation();
        finalObservation.intArray = newHabitats;
        return new RiverState(state.getRiver(), finalObservation);
    }

    /**
     * Retrieve the reward for the transition from a state to the next with the given actions.
     * 
     * @param state
     *            The initial state
     * @param actions
     *            The actions performed on the initial state
     * 
     * @return The reward of the transition
     */
    public double getReward(final RiverState state, final Action actions) {
        double reward = 0;
        final int numReaches = mRiver.getNumReaches();

        // Calculate the cost for every reach
        for (int i = 0; i < numReaches; ++i) {
            final int action = actions.intArray[i];
            final Reach reach = state.getReach(i);

            // Subtract the cost generated by the current plants
            final int habitatsInvaded = reach.getHabitatsInvaded();
            if (habitatsInvaded > 0) {
                reward -= (mCostInvadedReach + mCostHabitatTamarisk * habitatsInvaded);
            }
            reward -= mCostHabitatEmpty * reach.getHabitatsEmpty();

            // Subtract the cost of the actions performed
            switch (action) {
            case Utilities.ACTION_ERADICATE:
                reward -= (mCostEradicate + mCostVariableEradicate * reach.getHabitatsInvaded());
                break;

            case Utilities.ACTION_RESTORE:
                reward -= (mCostRestorate + mCostVariableRestorate * reach.getHabitatsEmpty());
                break;

            case Utilities.ACTION_ERADICATE_RESTORE:
                reward -= (mCostRestorate + mCostVariableEradicateRestorate * reach.getHabitatsInvaded());
                break;
            }
        }

        return reward;
    }

    /**
     * Checks if all reaches are above the activated threshold and thus do not seem to have
     * exogenous germination.
     * 
     * @return True iff all reaches are purely endogenous
     */
    public boolean isExegenousActivated() {
        for (final double ratio : mExoToEndoRatio) {
            if (ratio < EXO_ACTIVATED_THRESHOLD) {
                return true;
            }
        }
        return false;
    }

}