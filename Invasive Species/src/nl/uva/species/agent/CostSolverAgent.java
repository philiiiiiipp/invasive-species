package nl.uva.species.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import nl.uva.species.model.Reach;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;
import nl.uva.species.utils.Messages;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * Solves the cost parameters and chooses actions that are necessary to do so
 */
public class CostSolverAgent extends AbstractAgent {

    private River mRiver;
    private RiverState mLastState;
    private Action mLastAction;

    /** Indicator whether the cost parameters are found already */
    private boolean mCostParametersFound;

    /** The number of (cost) parameters which we want to estimate */
    private int mNumberOfVariables = 8;

    /**
     * Matrix for left side of the equation The columns are (#invadedHabitatsTotal, #emptyHabitatsTotal,
     * #invadedReaches, #eradicatedReaches, #restoredReaches+#eradicatedAndRestoredReaches, #eradticatedTreesTotal,
     * #restoredTreesTotal, #eradicatedAndRestoredTreesTotal).
     * */
    private RealMatrix mDataMatrix;

    /** Vector for right side of the equation, each entry holds a reward */
    private RealVector mDataVector;

    /** The vector that holds the cost parameters, if found */
    private RealVector mCostParameters;

    /** Indicator at which position we want to fill the matrix currently */
    private int mFillPosition;

    Random mRand;

    int mNumberOfSteps = 1; // 1 for start
    int mNumOfLastInvalidActions = 0;
    int mNumOfSkippedActions = 0;

    // the following boolean parameters are useful to keep track of which state-action-pairs / actions we have already
    // tried
    boolean mNothing = false;
    boolean[] mErad;
    boolean[] mEradRes;

    @Override
    public void init(final River river) {

        mRand = new Random();

        mRiver = river;

        mCostParametersFound = false;
        mFillPosition = 0;

        mErad = new boolean[mRiver.getReachSize()]; // is initalized with false
        mEradRes = new boolean[mRiver.getReachSize()];

        // initialize the matrix and the vector in which the equations will be stored
        mDataMatrix = new Array2DRowRealMatrix(mNumberOfVariables, mNumberOfVariables);
        mDataVector = new ArrayRealVector(mNumberOfVariables);
    }

    @Override
    public Action start(final Observation observation) {

        RiverState currentState = mRiver.getRiverState(observation);

        // try restoring
        Action action = new Action();
        action.intArray = new int[mRiver.getNumReaches()];
        boolean restored = false;
        for (Reach reach : currentState.getReaches()) {
            if (reach.getHabitatsEmpty() > 0 && !restored) {
                action.intArray[reach.getIndex()] = Utilities.ACTION_RESTORE;
                restored = true;
            } else {
                action.intArray[reach.getIndex()] = Utilities.ACTION_NOTHING;
            }
        }

        // in the first step, we want to restore wherever possible
        // the method getPossibleAction() is designed so that it first tries restoring
        if (!addingPossible(currentState, action)) {
            action = getPossibleAction(currentState, 0, new int[currentState.getReaches().size()]);
        }

        // update last state and action
        mLastState = currentState;
        mLastAction = action;

        return action;
    }

    @Override
    public Action step(final double reward, final Observation observation) {

        RiverState currrentState = mRiver.getRiverState(observation);

        mNumberOfSteps++;

        // if the cost parameters have been found already, do nothing
        if (costParametersFound()) {
            return doNothing();
        }

        // if our last action was invalid, we just do nothing
        if (observation.intArray.length != mRiver.getNumReaches() * mRiver.getReachSize()) {
            System.out.println("The last action was invalid or exceeded the budget.");
            return doNothing();
        }

        // if the last action was valid, we reset the number of last invalid actions to zero
        mNumOfLastInvalidActions = 0;

        // add the data (last state, last action, this reward) to the data matrix
        addData(mLastState, mLastAction, reward);

        Action action = getPossibleAction(currrentState, 0, new int[currrentState.getReaches().size()]);
        mLastState = currrentState;
        mLastAction = action;
        return action;

    }

    private Action getPossibleAction(final RiverState riverState, final int reachPosition, final int[] action) {
        // end of recursion
        if (reachPosition == riverState.getReaches().size()) {
            Action current = new Action();
            current.intArray = action;
            // check if the current action would create a linear independent data row and if so, return it
            if (addingPossible(riverState, current)) {
                System.out.println("possible action found");
                for (int i = 0; i < current.intArray.length; ++i) System.out.println(current.intArray[i]);
                return current;
            } else {
                return null;
            }
        }
        // recursively go through all possible actions
        Reach currentReach = riverState.getReach(reachPosition);
        Action tempAction;
        for (Integer a : getValidActions(currentReach)) {
            action[currentReach.getIndex()] = a;
            tempAction = getPossibleAction(riverState, reachPosition + 1, action);
            if (tempAction != null) {
                return tempAction;
            }
        }
        // if we haven't found a possible action that gives a datarow that could be added to the data matrix, we do
        // nothing
        return doNothing();
    }

    /**
     * Retrieves all valid actions on this reach.
     * 
     * @return A list of all valid actions at this reach
     */
    public List<Integer> getValidActions(final Reach reach) {
        List<Integer> validActions = new ArrayList<>();
        if (reach.getHabitatsEmpty() != 0) {
            // Can't restore a reach without empty habitats
            validActions.add(Utilities.ACTION_RESTORE);
        }
        if (reach.getHabitatsInvaded() != 0) {
            // Can't eradicate a reach without Tamarisk plants
            validActions.add(Utilities.ACTION_ERADICATE);
            validActions.add(Utilities.ACTION_ERADICATE_RESTORE);
        }

        validActions.add(Utilities.ACTION_NOTHING);
        return validActions;
    }

    private Action doNothing() {
        final Action action = new Action();
        action.intArray = new int[mRiver.getNumReaches()];
        Arrays.fill(action.intArray, Utilities.ACTION_NOTHING);
        return action;
    }

    private boolean addingPossible(final RiverState state, final Action action) {
        // we will only add data if the matrix is not full yet
        if (mFillPosition == mNumberOfVariables) {
            return false;
        }
        // get the data we need for calculations
        RealVector newDataRow = fillNewDataRow(state, action);
        // if the vector has zero norm, we can't use it
        if (newDataRow.getNorm() == 0) {
            return false;
        }
        // we can always fill in data if the matrix is empty
        if (mFillPosition == 0) {
            return true;
        } else {
            // check if new data row is linear dependent on the already filled up rows of the matrix
            RealMatrix subMatrix = mDataMatrix.getSubMatrix(0, mFillPosition, 0, mNumberOfVariables - 1);
            subMatrix.setRowVector(mFillPosition, newDataRow);
            SingularValueDecomposition decomp = new SingularValueDecomposition(subMatrix);
            if (decomp.getRank() == mFillPosition + 1) { // not linear dependent, we can add it
                return true;
            } else { // linear dependent --> useless
                return false;
            }
        }
    }

    /**
     * Checks if the new data is usable for the calculations and if so, adds them. If enough data is collected, this
     * solves the linear system
     * 
     * @param state
     * @param actions
     * @param reward
     * 
     * @return success Indicated if data was successfully added to the data matrix
     */
    public void addData(final RiverState state, final Action actions, final double reward) {
        // we will only add data if the matrix is not full yet
        if (mFillPosition == mNumberOfVariables) {
            return;
        }
        // get the data we need for calculations
        RealVector newDataRow = fillNewDataRow(state, actions);
        // if the vector has zero norm, we can't use it
        if (newDataRow.getNorm() == 0) {
            return;
        }
        boolean insert = true;
        if (mFillPosition != 0) {
            // check if new data row is linear dependent on the already filled up rows of the matrix
            RealMatrix subMatrix = mDataMatrix.getSubMatrix(0, mFillPosition, 0, mNumberOfVariables - 1);
            subMatrix.setRowVector(mFillPosition, newDataRow);
            SingularValueDecomposition decomp = new SingularValueDecomposition(subMatrix);
            if (decomp.getRank() < mFillPosition + 1) {
                insert = false;
            }
        }
        // if the new data isn't linear dependent on the old data, we can add it to the matrix and the vector
        if (insert) {
            System.out.print("Fillposition: ");
            System.out.println(mFillPosition);
            mDataMatrix.setRowVector(mFillPosition, newDataRow);
            mDataVector.setEntry(mFillPosition, -reward); // add the negative reward, which are the costs for the
                                                          // state/action pair
            mFillPosition++;
            System.out.print("Number of steps until here: ");
            System.out.println(mNumberOfSteps);
            // print out details about this round (observation/action/dataRow) System.out.println(" ");
            System.out.print("Observation: ");
            for (int i = 0; i < state.getObservation().intArray.length; ++i) {
                System.out.print(state.getObservation().intArray[i]);
                System.out.print(" ");
            }
            System.out.println(" ");
            System.out.print("Action: ");
            for (int i = 0; i < actions.intArray.length; ++i) {
                System.out.print(actions.intArray[i]);
                System.out.print(" ");
            }
            System.out.println(" ");
            System.out.print("Reward: ");
            System.out.print(reward);
            System.out.println(" ");
            // System.out.print("New data row: "); for (int i = 0; i < newDataRow.getDimension(); ++i) {
            // System.out.print(newDataRow.getEntry(i)); System.out.print(" "); }
        }

        // if we have gained enough data, then we can solve the system of linear equations
        if (mFillPosition == mNumberOfVariables) {
            solveCostParameters();
        }

        return;
    }

    /**
     * Solves the linear equation given with the data matrix and data vector, sets the cost parameters in the model and
     * sets cost parameters to true
     * 
     * Should only be called once!
     */
    private void solveCostParameters() {
        DecompositionSolver solver = new LUDecomposition(mDataMatrix).getSolver();
        mCostParameters = solver.solve(mDataVector);
        mCostParametersFound = true;

        /*
         * System.out.println(mDataMatrix); System.out.println(mDataVector); System.out.println(mCostParameters);
         */

        System.out.println(mCostParameters.getEntry(0));
        System.out.println(mCostParameters.getEntry(1));
        System.out.println(mCostParameters.getEntry(2));
        System.out.println(mCostParameters.getEntry(3));
        System.out.println(mCostParameters.getEntry(4));
        System.out.println(mCostParameters.getEntry(5));
        System.out.println(mCostParameters.getEntry(6));
        System.out.println(mCostParameters.getEntry(7));

    }

    /**
     * Takes a state and an action and fills a row vector with (#invadedHabitatsTotal, #emptyHabitatsTotal,
     * #invadedReaches, #eradicatedReaches, #restoredReaches+#eradicatedAndRestoredReaches, #eradticatedTreesTotal,
     * #restoredTreesTotal, #eradicatedAndRestoredTreesTotal).
     * 
     * @param state
     * @param actions
     * @return
     */
    private RealVector fillNewDataRow(final RiverState state, final Action actions) {

        RealVector newDataRow = new ArrayRealVector(mNumberOfVariables);

        // state-dependent
        int numberOfInvadedHabitats = 0;
        int numberOfEmptyHabitats = 0;
        int numberOfInvadedReaches = 0;
        // action-dependent
        int numberOfEradicatedReaches = 0;
        int numberOfRestoredReaches = 0;
        int numberOfEradicatedAndRestoredReaches = 0; // will later count as restoration only
        int numberOfEradicatedTrees = 0;
        int numberOfRestoredTrees = 0;
        int numberOfEradicatedAndRestoredTrees = 0;

        for (final Reach reach : state.getReaches()) {
            // state-dependent
            numberOfInvadedHabitats += reach.getHabitatsInvaded();
            numberOfEmptyHabitats += reach.getHabitatsEmpty();
            if (reach.getHabitatsInvaded() > 0) numberOfInvadedReaches++;
            // action-dependent
            final int action = actions.intArray[reach.getIndex()];
            switch (action) {
                case Utilities.ACTION_ERADICATE:
                    numberOfEradicatedReaches++;
                    numberOfEradicatedTrees += reach.getHabitatsInvaded();
                    break;
                case Utilities.ACTION_RESTORE:
                    numberOfRestoredReaches++;
                    numberOfRestoredTrees += reach.getHabitatsEmpty();
                    break;
                case Utilities.ACTION_ERADICATE_RESTORE:
                    numberOfEradicatedAndRestoredReaches++;
                    numberOfEradicatedAndRestoredTrees += reach.getHabitatsInvaded();
                    break;
            }
        }

        // state-dependent
        newDataRow.setEntry(0, (double) numberOfInvadedHabitats);
        newDataRow.setEntry(1, (double) numberOfEmptyHabitats);
        newDataRow.setEntry(2, (double) numberOfInvadedReaches);
        // action-dependent
        newDataRow.setEntry(3, (double) numberOfEradicatedReaches);
        newDataRow.setEntry(4, (double) numberOfRestoredReaches + numberOfEradicatedAndRestoredReaches);
        newDataRow.setEntry(5, (double) numberOfEradicatedTrees);
        newDataRow.setEntry(6, (double) numberOfRestoredTrees);
        newDataRow.setEntry(7, (double) numberOfEradicatedAndRestoredTrees);

        return newDataRow;
    }

    /**
     * Returns whether the cost parameters were already found
     * 
     * @return
     */
    public boolean costParametersFound() {
        return mCostParametersFound;
    }

    /**
     * Returns the cost parameter vector
     * 
     * @return the cost parameter as a RealVector if defined, else null
     */
    public RealVector getCostParameters() {
        return mCostParameters;
    }

    @Override
    public void end(final double reward) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    @Override
    public void message(Messages message) {
        // TODO Auto-generated method stub

    }

}
