package nl.uva.species.model;

import java.util.Arrays;
import java.util.Set;

import nl.uva.species.utils.Utilities;

import org.apache.commons.math3.linear.RealVector;
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
        final int[] newHabitats = new int[mRiver.getNumReaches() * reachSize];

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
                        if (random < mEradicationRate * (1 - mRestorationRate)) {
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

        final boolean exogenousActivated = isexogenousActivated();

        // Germination of empty habitats
        for (final Reach reach : newState.getReaches()) {
            // Skip full reaches
            if (reach.getHabitatsEmpty() == 0) {
                continue;
            }

            final int index = reach.getIndex();

            final double endoToExoRatio = (1 - mExoToEndoRatio[index]);

            final double exoTamariskWeight = (exogenousActivated ? mExoToEndoRatio[index] * mExoTamarisk[index] : 0);
            final double exoNativeWeight = (exogenousActivated ? mExoToEndoRatio[index] * (1 - mExoTamarisk[index]) : 0);

            final double endoTamarisWeight = endoToExoRatio * mEndoTamarisk;
            final double endoNativeWeight = endoToExoRatio * (1 - mEndoTamarisk);

            // Calculate the reproduction scores for Tamarisk and native trees
            int tamariskScore = reach.getHabitatsInvaded();
            int nativeScore = reach.getHabitatsNative();
            final Reach parent = reach.getParent();
            if (parent != null) {
                // Add the parent's scores
                tamariskScore += parent.getHabitatsInvaded() * Math.pow(mUpstreamRate, 2);
                nativeScore += parent.getHabitatsNative() * Math.pow(mUpstreamRate, 2);

                // Add the sibling's scores
                final Set<Reach> siblings = parent.getChildren();
                for (final Reach sibling : siblings) {
                    if (sibling != reach) {
                        tamariskScore += sibling.getHabitatsInvaded() * mUpstreamRate * mDownstreamRate;
                        nativeScore += sibling.getHabitatsNative() * mUpstreamRate * mDownstreamRate;
                    }
                }
            }
            // Add the children's scores
            final Set<Reach> children = reach.getChildren();
            for (final Reach child : children) {
                tamariskScore += child.getHabitatsInvaded() * Math.pow(mDownstreamRate, 2);
                nativeScore += child.getHabitatsNative() * Math.pow(mDownstreamRate, 2);
            }

            // Determine the chance of each plant and normalise
            final double tamariskChance = exoTamariskWeight + endoTamarisWeight * tamariskScore / (5 * reachSize);
            final double nativeChance = exoNativeWeight + endoNativeWeight * nativeScore / (5 * reachSize);
            final double chanceSum = tamariskChance + nativeChance;

            if (chanceSum > 0) {
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
        }

        // Create a new state based on the new habitats as observation
        final Observation finalObservation = new Observation();
        finalObservation.intArray = newHabitats;
        return new RiverState(state.getRiver(), finalObservation);
    }

    /**
     * Calculates the expected average reward for the state resulting of the actions.
     * 
     * @param state
     *            The initial state
     * @param actions
     *            The actions to be taken
     * 
     * @return The expected reward of the state after performing the actions
     */
    public double getExpectedNextStateReward(final RiverState state, final Action actions) {
        double reward = 0;
        final int reachSize = mRiver.getReachSize();
        final int numReaches = mRiver.getNumReaches();
        final double[] reachesInvaded = new double[numReaches];
        final double[] reachesNative = new double[numReaches];
        final double[] reachesEmpty = new double[numReaches];

        // Perform the action on the different reaches
        for (final Reach reach : state.getReaches()) {
            final int reachInvaded = reach.getHabitatsInvaded();
            final int reachNative = reach.getHabitatsNative();
            final int reachEmpty = reach.getHabitatsEmpty();

            double deathsInvaded = 0;
            double deathsNative = 0;
            double growthsNative = 0;

            // Perform action on each habitat in reach
            switch (actions.intArray[reach.getIndex()]) {
            case Utilities.ACTION_ERADICATE:
                // During eradication, both type of plants may die
                deathsNative = reachNative * mDeathRateNative;
                deathsInvaded = reachInvaded * mEradicationRate;
                break;

            case Utilities.ACTION_RESTORE:
                // During restoration, empty spaces may come to life and plants may die
                growthsNative = reachEmpty * mRestorationRate;
                deathsNative = reachNative * mDeathRateNative;
                deathsInvaded = reachInvaded * mDeathRateTamarisk;
                break;

            case Utilities.ACTION_ERADICATE_RESTORE:
                // During eradication/restoration, invaded habitats may die and/or come to life and
                // native species may die
                deathsNative = reachNative * mDeathRateNative;
                deathsInvaded = reachInvaded * mEradicationRate * (1 - mRestorationRate);
                growthsNative = reachInvaded * mEradicationRate * mRestorationRate;
                break;

            case Utilities.ACTION_NOTHING:
                // When doing nothing, both type of plants may die
                deathsNative = reachNative * mDeathRateNative;
                deathsInvaded = reachInvaded * mDeathRateTamarisk;
            }

            // Update the expected reach contents
            reachesInvaded[reach.getIndex()] = reachInvaded - deathsInvaded;
            reachesNative[reach.getIndex()] = reachNative - deathsNative + growthsNative;
            reachesEmpty[reach.getIndex()] = reachEmpty - growthsNative + deathsInvaded + deathsNative;
        }

        // Germinate empty habitats and calculate reward
        for (final Reach reach : state.getReaches()) {
            final int index = reach.getIndex();

            double reachInvaded = reachesInvaded[index];
            double reachEmpty = reachesEmpty[index];

            // Skip full reaches
            if (reachEmpty == 0) {
                continue;
            }

            final double endoToExoRatio = (1 - mExoToEndoRatio[index]);

            final double exoTamariskWeight = mExoToEndoRatio[index] * mExoTamarisk[index];
            final double exoNativeWeight = mExoToEndoRatio[index] * (1 - mExoTamarisk[index]);

            final double endoTamarisWeight = endoToExoRatio * mEndoTamarisk;
            final double endoNativeWeight = endoToExoRatio * (1 - mEndoTamarisk);

            // Calculate the reproduction scores for Tamarisk and native trees
            double tamariskScore = reachesInvaded[index];
            double nativeScore = reachesNative[index];
            final Reach parent = reach.getParent();
            if (parent != null) {
                final int parentIndex = parent.getIndex();

                // Add the parent's scores
                tamariskScore += reachesInvaded[parentIndex] * Math.pow(mUpstreamRate, 2);
                nativeScore += reachesNative[parentIndex] * Math.pow(mUpstreamRate, 2);

                // Add the sibling's scores
                final Set<Reach> siblings = parent.getChildren();
                for (final Reach sibling : siblings) {
                    if (sibling != reach) {
                        final int siblingIndex = sibling.getIndex();
                        tamariskScore += reachesInvaded[siblingIndex] * mUpstreamRate * mDownstreamRate;
                        nativeScore += reachesNative[siblingIndex] * mUpstreamRate * mDownstreamRate;
                    }
                }
            }
            // Add the children's scores
            final Set<Reach> children = reach.getChildren();
            for (final Reach child : children) {
                final int childIndex = child.getIndex();
                tamariskScore += reachesInvaded[childIndex] * Math.pow(mDownstreamRate, 2);
                nativeScore += reachesNative[childIndex] * Math.pow(mDownstreamRate, 2);
            }

            // Determine the chance of each plant and normalise
            final double tamariskChance = exoTamariskWeight + endoTamarisWeight * tamariskScore / (5 * reachSize);
            final double nativeChance = exoNativeWeight + endoNativeWeight * nativeScore / (5 * reachSize);
            final double chanceSum = tamariskChance + nativeChance;

            if (chanceSum > 0) {
                final double tamariskChanceNorm = tamariskChance / chanceSum;

                // Update the habitats with the new regrown tree
                reachInvaded = reachEmpty * tamariskChanceNorm;
                reachEmpty = 0;
            }

            // Adjust the reward for this reach
            if (reachInvaded > 0) {
                reward -= (mCostInvadedReach + mCostHabitatTamarisk * reachInvaded);
            }
            reward -= mCostHabitatEmpty * reachEmpty;
        }

        return reward;
    }

    /**
     * Compares the resulting state to how the model would predict it and returns a score based on
     * how much they correlate.
     * 
     * @param state
     *            The starting state
     * @param actions
     *            The action to perform
     * @param resultState
     *            The actual resulting state after performing the actions
     * 
     * @return A score representing the correlation between model's expectation and the actual
     *         outcome
     */
    public double evaluateModel(final RiverState state, final Action actions, final RiverState resultState) {
        double reward = 0;
        final int reachSize = mRiver.getReachSize();
        final int numReaches = mRiver.getNumReaches();
        final int numHabitats = numReaches * reachSize;
        final double[] habitatsInvaded = new double[numHabitats];
        final double[] habitatsNative = new double[numHabitats];
        final double[] habitatsEmpty = new double[numHabitats];
        final double[] reachesInvaded = new double[numReaches];
        final double[] reachesNative = new double[numReaches];
        final double[] reachesEmpty = new double[numReaches];

        // Perform the action on the different reaches
        for (final Reach reach : state.getReaches()) {
            final int reachIndex = reach.getIndex();
            final int[] habitats = reach.getHabitats();

            final int action = actions.intArray[reachIndex];

            for (int i = 0; i < habitats.length; ++i) {
                double habitatInvaded = 0;
                double habitatNative = 0;
                double habitatEmpty = 0;

                // Perform action on each habitat in reach
                switch (action) {
                case Utilities.ACTION_ERADICATE:
                    // During eradication, both type of plants may die
                    switch (habitats[i]) {
                    case Utilities.HABITAT_NATIVE:
                        habitatNative = 1 - mDeathRateNative;
                        habitatEmpty = mDeathRateNative;
                        break;
                    case Utilities.HABITAT_INVADED:
                        habitatInvaded = 1 - mEradicationRate;
                        habitatEmpty = mEradicationRate;
                        break;
                    }

                    break;

                case Utilities.ACTION_RESTORE:
                    // During restoration, empty spaces may come to life and plants may die
                    switch (habitats[i]) {
                    case Utilities.HABITAT_EMPTY:
                        habitatNative = mRestorationRate;
                        habitatEmpty = 1 - mRestorationRate;
                        break;
                    case Utilities.HABITAT_NATIVE:
                        habitatNative = 1 - mDeathRateNative;
                        habitatEmpty = mDeathRateNative;
                        break;
                    case Utilities.HABITAT_INVADED:
                        habitatInvaded = 1 - mDeathRateTamarisk;
                        habitatEmpty = mDeathRateTamarisk;
                        break;
                    }
                    break;

                case Utilities.ACTION_ERADICATE_RESTORE:
                    // During eradication, invaded habitats may die and/or come to life and native
                    // species may die
                    switch (habitats[i]) {
                    case Utilities.HABITAT_NATIVE:
                        habitatNative = 1 - mDeathRateNative;
                        habitatEmpty = mDeathRateNative;
                        break;
                    case Utilities.HABITAT_INVADED:
                        habitatEmpty = mEradicationRate * (1 - mRestorationRate);
                        habitatNative = mEradicationRate * mRestorationRate;
                        habitatInvaded = 1 - mEradicationRate;
                        break;
                    }
                    break;

                case Utilities.ACTION_NOTHING:
                    // When doing nothing, both type of plants may die
                    switch (habitats[i]) {
                    case Utilities.HABITAT_NATIVE:
                        habitatNative = 1 - mDeathRateNative;
                        habitatEmpty = mDeathRateNative;
                        break;
                    case Utilities.HABITAT_INVADED:
                        habitatInvaded = 1 - mDeathRateTamarisk;
                        habitatEmpty = mDeathRateTamarisk;
                        break;
                    }
                }

                // Update the habitats and reaches with the new values
                final int habitatIndex = reach.getIndex() * reachSize + i;
                habitatsInvaded[habitatIndex] = habitatInvaded;
                habitatsNative[habitatIndex] = habitatNative;
                habitatsEmpty[habitatIndex] = habitatEmpty;

                reachesInvaded[reachIndex] += habitatInvaded;
                reachesNative[reachIndex] += habitatNative;
                reachesEmpty[reachIndex] += habitatEmpty;
            }
        }

        final boolean exogenousActivated = isexogenousActivated();

        // Germination of empty habitats
        for (final Reach reach : state.getReaches()) {
            final int reachIndex = reach.getIndex();

            // Skip full reaches
            if (reachesEmpty[reachIndex] == 0) {
                continue;
            }

            final int index = reach.getIndex();

            final double endoToExoRatio = (1 - mExoToEndoRatio[index]);

            final double exoTamariskWeight = (exogenousActivated ? mExoToEndoRatio[index] * mExoTamarisk[index] : 0);
            final double exoNativeWeight = (exogenousActivated ? mExoToEndoRatio[index] * (1 - mExoTamarisk[index]) : 0);

            final double endoTamarisWeight = endoToExoRatio * mEndoTamarisk;
            final double endoNativeWeight = endoToExoRatio * (1 - mEndoTamarisk);

            // Calculate the reproduction scores for Tamarisk and native trees
            double tamariskScore = reachesInvaded[reachIndex];
            double nativeScore = reachesNative[reachIndex];
            final Reach parent = reach.getParent();
            if (parent != null) {
                final int parentIndex = parent.getIndex();

                // Add the parent's scores
                tamariskScore += reachesInvaded[parentIndex] * Math.pow(mUpstreamRate, 2);
                nativeScore += reachesNative[parentIndex] * Math.pow(mUpstreamRate, 2);

                // Add the sibling's scores
                final Set<Reach> siblings = parent.getChildren();
                for (final Reach sibling : siblings) {
                    if (sibling != reach) {
                        final int siblingIndex = sibling.getIndex();
                        tamariskScore += reachesInvaded[siblingIndex] * mUpstreamRate * mDownstreamRate;
                        nativeScore += reachesNative[siblingIndex] * mUpstreamRate * mDownstreamRate;
                    }
                }
            }
            // Add the children's scores
            final Set<Reach> children = reach.getChildren();
            for (final Reach child : children) {
                final int childIndex = child.getIndex();
                tamariskScore += reachesInvaded[childIndex] * Math.pow(mDownstreamRate, 2);
                nativeScore += reachesNative[childIndex] * Math.pow(mDownstreamRate, 2);
            }

            // Determine the chance of each plant and normalise
            final double tamariskChance = exoTamariskWeight + endoTamarisWeight * tamariskScore / (5 * reachSize);
            final double nativeChance = exoNativeWeight + endoNativeWeight * nativeScore / (5 * reachSize);
            final double chanceSum = tamariskChance + nativeChance;

            if (chanceSum > 0) {
                final double tamariskChanceNorm = tamariskChance / chanceSum;

                // Update the habitats with the new regrown tree
                final int[] habitats = reach.getHabitats();
                for (int i = 0; i < habitats.length; ++i) {
                    final int habitatIndex = reachIndex * reachSize + i;
                    habitatsInvaded[habitatIndex] += habitatsEmpty[habitatIndex] * tamariskChanceNorm;
                    habitatsNative[habitatIndex] += habitatsEmpty[habitatIndex] * (1 - tamariskChanceNorm);
                    habitatsEmpty[habitatIndex] = 0;
                }
            }
        }

        // Compare the expected habitats to the actual returned ones
        for (final Reach reach : state.getReaches()) {
            final int reachIndex = reach.getIndex();

            final int[] habitats = reach.getHabitats();
            for (int i = 0; i < habitats.length; ++i) {
                final int habitatIndex = reachIndex * reachSize + i;

                switch (habitats[i]) {
                case Utilities.HABITAT_EMPTY:
                    reward += habitatsEmpty[habitatIndex];
                    break;
                case Utilities.HABITAT_NATIVE:
                    reward += habitatsNative[habitatIndex];
                    break;
                case Utilities.HABITAT_INVADED:
                    reward += habitatsInvaded[habitatIndex];
                    break;
                }
            }
        }

        // Return the normalised reward, based on the maximum score (1 per habitat)
        return reward / numHabitats;
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
        // Find the reward of the actions
        double reward = getActionReward(state, actions);

        // Calculate the cost for every reach
        final int numReaches = mRiver.getNumReaches();
        for (int i = 0; i < numReaches; ++i) {
            final Reach reach = state.getReach(i);

            // Subtract the cost generated by the current plants
            final int habitatsInvaded = reach.getHabitatsInvaded();
            if (habitatsInvaded > 0) {
                reward -= (mCostInvadedReach + mCostHabitatTamarisk * habitatsInvaded);
            }
            reward -= mCostHabitatEmpty * reach.getHabitatsEmpty();
        }

        // Can't get less than the penalty
        if (reward < mRiver.getPenalty()) {
            return mRiver.getPenalty();
        }

        return reward;
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
    public double getActionReward(final RiverState state, final Action actions) {
        double reward = 0;
        double actionCost = 0;
        final int numReaches = mRiver.getNumReaches();

        // Calculate the cost for every reach
        for (int i = 0; i < numReaches; ++i) {
            final int action = actions.intArray[i];
            final Reach reach = state.getReach(i);

            // Subtract the cost of the actions performed
            switch (action) {
            case Utilities.ACTION_ERADICATE:
                if (reach.getHabitatsInvaded() == 0) {
                    // Can't eradicate a reach without Tamarisk plants
                    return mRiver.getPenalty();
                }
                reward -= (mCostEradicate + mCostVariableEradicate * reach.getHabitatsInvaded());
                break;

            case Utilities.ACTION_RESTORE:
                if (reach.getHabitatsEmpty() == 0) {
                    // Can't restore a reach without empty habitats
                    return mRiver.getPenalty();
                }
                reward -= (mCostRestorate + mCostVariableRestorate * reach.getHabitatsEmpty());
                break;

            case Utilities.ACTION_ERADICATE_RESTORE:
                if (reach.getHabitatsInvaded() == 0) {
                    // Can't eradicate a reach without Tamarisk plants
                    return mRiver.getPenalty();
                }
                reward -= (mCostRestorate + mCostVariableEradicateRestorate * reach.getHabitatsInvaded());
                break;
            }
        }

        // Penalty for crossing the budget
        if (actionCost > mRiver.getBudget()) {
            return mRiver.getPenalty();
        }

        return reward;
    }

    /**
     * Checks if all reaches are above the activated threshold and thus do not seem to have
     * exogenous germination.
     * 
     * @return True iff all reaches are purely endogenous
     */
    public boolean isexogenousActivated() {
        for (final double ratio : mExoToEndoRatio) {
            if (ratio < EXO_ACTIVATED_THRESHOLD) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the cost parameters
     * 
     * @param costParameters
     *          must be a RealVector with the following entries:
     *          (costHabitatTamarisk, costHabitatEmpty, costInvadedReach,
     *           costEradicate, costRestorate, 
     *           costVariableEradicate, costVariableRestore, costVariableEradicateRestore)
     */
    public void setCostParameters(RealVector costParameters) {
        mCostHabitatTamarisk = costParameters.getEntry(0);
        mCostHabitatEmpty = costParameters.getEntry(1);
        mCostInvadedReach = costParameters.getEntry(2);
        mCostEradicate = costParameters.getEntry(3);
        mCostRestorate = costParameters.getEntry(4);
        mCostVariableEradicate = costParameters.getEntry(5);
        mCostVariableRestorate = costParameters.getEntry(6);
        mCostVariableEradicateRestorate = costParameters.getEntry(7);
    }
    
}