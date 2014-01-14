package nl.uva.species.model;

import java.util.Arrays;
import java.util.Set;

import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public class EnvModel {

    private final static double EXO_ACTIVATED_THRESHOLD = 0.98;

    private River mRiver;

    private double mCostInvadedReach = 10;
    private double mCostHabitatTamarisk = 0.1;
    private double mCostHabitatEmpty = 0.05;
    private double mCostEradicate = 0.5;
    private double mCostRestorate = 0.9;
    private double mCostVariableEradicate = 0.4;
    private double mCostVariableRestorate = 0.4;
    private double mCostVariableEradicateRestorate = 0.8;

    private double mDefaultExoToEndoRatio = 0.8;
    private double[] mExoToEndoRatio;
    private double mDefaultExoTamarisk = 0.7;
    private double[] mExoTamarisk;
    private double mEndoTamarisk = 0.6;
    private double mUpstreamRate = 0.1;
    private double mDownstreamRate = 0.5;
    private double mEradicationRate = 0.85;
    private double mRestorationRate = 0.65;
    private double mDeathRateTamarisk = 0.2;
    private double mDeathRateNative = 0.2;

    public EnvModel(final River river) {
        mRiver = river;

        mExoToEndoRatio = new double[river.getNumReaches()];
        Arrays.fill(mExoToEndoRatio, mDefaultExoToEndoRatio);

        mExoTamarisk = new double[river.getNumReaches()];
        Arrays.fill(mExoTamarisk, mDefaultExoTamarisk);
    }

    public RiverState getPossibleNextState(final RiverState state, final Action actions) {
        final int reachSize = mRiver.getReachSize();
        final int[] newHabitats = new int[state.getReaches().size() * reachSize];

        for (final Reach reach : state.getReaches()) {
            final int[] habitats = reach.getHabitats();

            final int action = actions.intArray[reach.getIndex()];

            for (int i = 0; i < habitats.length; ++i) {
                final double random = Utilities.RNG.nextDouble();

                // Perform action on each habitat in reach
                switch (action) {
                case Utilities.Erad:

                    switch (habitats[i]) {
                    case Utilities.Nat:
                        if (random < mDeathRateNative) {
                            habitats[i] = Utilities.Emp;
                        }
                        break;
                    case Utilities.Tam:
                        if (random < mEradicationRate) {
                            habitats[i] = Utilities.Emp;
                        }
                        break;
                    }

                    break;

                case Utilities.Res:
                    switch (habitats[i]) {
                    case Utilities.Emp:
                        if (random < mRestorationRate) {
                            habitats[i] = Utilities.Nat;
                        }
                        break;
                    case Utilities.Nat:
                        if (random < mDeathRateNative) {
                            habitats[i] = Utilities.Emp;
                        }
                        break;
                    case Utilities.Tam:
                        if (random < mDeathRateTamarisk) {
                            habitats[i] = Utilities.Emp;
                        }
                        break;
                    }

                    break;

                case Utilities.EradRes:
                    switch (habitats[i]) {
                    case Utilities.Nat:
                        if (random < mDeathRateNative) {
                            habitats[i] = Utilities.Emp;
                        }
                        break;
                    case Utilities.Tam:
                        if (random < mEradicationRate + (1 - mRestorationRate)) {
                            habitats[i] = Utilities.Emp;
                        } else if (random < mEradicationRate) {
                            habitats[i] = Utilities.Nat;
                        }
                        break;
                    }
                    break;

                case Utilities.Not:
                    switch (habitats[i]) {
                    case Utilities.Nat:
                        if (random < mDeathRateNative) {
                            habitats[i] = Utilities.Emp;
                        }
                        break;
                    case Utilities.Tam:
                        if (random < mDeathRateTamarisk) {
                            habitats[i] = Utilities.Emp;
                        }
                        break;
                    }
                }

                newHabitats[reach.getIndex() * reachSize + i] = habitats[i];
            }
        }

        final Observation newObservation = new Observation();
        newObservation.intArray = newHabitats;
        final RiverState newState = new RiverState(state.getRiver(), newObservation);

        // Germination of empty habitats
        for (final Reach reach : newState.getReaches()) {
            if (reach.getHabitatsEmpty() == 0) {
                continue;
            }

            final int index = reach.getIndex();

            final double endoToExoRatio = (1 - mExoToEndoRatio[index]);

            final double exoTamariskWeight = mExoToEndoRatio[index] * mExoTamarisk[index];
            final double exoNativeWeight = mExoToEndoRatio[index] * (1 - mExoTamarisk[index]);

            final double endoTamarisWeight = endoToExoRatio * mEndoTamarisk;
            final double endoNativeWeight = endoToExoRatio * (1 - mEndoTamarisk);

            int tamariskScore = reach.getHabitatsInvaded();
            int nativeScore = reach.getHabitatsNative();
            final Reach parent = reach.getParent();
            if (parent != null) {
                tamariskScore += parent.getHabitatsInvaded() * Math.pow(mUpstreamRate, 2);
                nativeScore += parent.getHabitatsInvaded() * Math.pow(mUpstreamRate, 2);

                final Set<Reach> siblings = parent.getChildren();
                for (final Reach sibling : siblings) {
                    if (sibling != reach) {
                        tamariskScore += sibling.getHabitatsInvaded() * mUpstreamRate * mDownstreamRate;
                        nativeScore += sibling.getHabitatsInvaded() * mUpstreamRate * mDownstreamRate;
                    }
                }
            }
            final Set<Reach> children = reach.getChildren();
            for (final Reach child : children) {
                tamariskScore += child.getHabitatsInvaded() * Math.pow(mDownstreamRate, 2);
                nativeScore += child.getHabitatsInvaded() * Math.pow(mDownstreamRate, 2);
            }

            final double tamariskChance = exoTamariskWeight + endoTamarisWeight * tamariskScore / (5 * reachSize);
            final double nativeChance = exoNativeWeight + endoNativeWeight * nativeScore / (5 * reachSize);
            final double chanceSum = tamariskChance + nativeChance;
            final double tamariskChanceNorm = tamariskChance / chanceSum;
            final double nativeChanceNorm = nativeChance / chanceSum;

            final int[] habitats = reach.getHabitats();
            for (int i = 0; i < habitats.length; ++i) {
                if (habitats[i] == Utilities.Emp) {
                    habitats[i] = (Utilities.RNG.nextDouble() < tamariskChanceNorm ? Utilities.Tam : Utilities.Nat);
                    newHabitats[index * reachSize + i] = habitats[i];
                }
            }
        }

        final Observation finalObservation = new Observation();
        finalObservation.intArray = newHabitats;
        return new RiverState(state.getRiver(), finalObservation);
    }
}