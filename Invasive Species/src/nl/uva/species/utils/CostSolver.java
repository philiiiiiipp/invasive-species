package nl.uva.species.utils;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.Reach;
import nl.uva.species.model.RiverState;
import org.rlcommunity.rlglue.codec.types.Action;

/**
 * Solves the cost parameters
 * 
 * Should be used like this:
 * if (!costParametersFound()) {
 *      addData(RiverState state, Action actions, reward)
 * }
 */
public class CostSolver {
    
    /** The model for which the cost parameters should be set for */
    private EnvModel mModel;
    
    /** Indicator whether the cost parameters are found already */
    private boolean mCostParametersFound;
    
    /** The number of (cost) parameters which we want to estimate */
    private int mNumberOfVariables;
    
    /** Matrix for left side of the equation 
     * The columns are 
     * (#invadedHabitatsTotal, #emptyHabitatsTotal, #invadedReaches, 
     *  #eradicatedReaches, #restoredReaches+#eradicatedAndRestoredReaches, 
     *  #eradticatedTreesTotal, #restoredTreesTotal, #eradicatedAndRestoredTreesTotal).
     * */
    private RealMatrix mDataMatrix;
    
    /** Vector for right side of the equation, each entry holds a reward */
    private RealVector mDataVector;
    
    /** The vector that holds the cost parameters, if found*/
    private RealVector mCostParameters;
    
    /** Indicator at which position we want to fill the matrix currently */
    private int mFillPosition;
    
    public CostSolver(final EnvModel model) {
        //
        mModel = model;
        mCostParametersFound = false;
        mFillPosition = 0;
        mNumberOfVariables = 8;
        
        // create the matrix and the vector in which the equations will be stored
        mDataMatrix = new Array2DRowRealMatrix(mNumberOfVariables, mNumberOfVariables);
        mDataVector = new ArrayRealVector(mNumberOfVariables);
    }
 
    /**
     * Checks if the new data is usable for the calculations and if so, adds them.
     * If enough data is collected, this solves the linear system
     * 
     * @param state
     * @param actions
     * @param reward
     */
    public void addData(final RiverState state, final Action actions, final double reward) {
        
        // we will only add data if the matrix is not full yet
        if (mFillPosition > mNumberOfVariables) {
            return;
        }
        
        // get the data we need for calculations
        RealVector newDataRow = fillNewDataRow(state, actions);
        
        // go through the rows which are already filled and check if the new data is linearly dependent to one of them
        boolean insert = true;
        // copy and unitize new vector
        RealVector unitizedNewDataRow = newDataRow.copy();
        unitizedNewDataRow.unitize();
        for (int i = 0; i < mFillPosition; ++i) {
            // copy and unitize current matrix row
            RealVector unitizedDataMatrixRow = mDataMatrix.getRowVector(i);
            unitizedDataMatrixRow.unitize();
            // if they are equal, the vectors are linear dependent and we don't want to add the new data
            if (unitizedNewDataRow.equals(unitizedDataMatrixRow)) {
                insert = false;
                break;
            }
        }
        
        // if the new data isn't linear dependent on the old data, we can add it to the matrix and the vector
        if (insert) {
            mDataMatrix.setRowVector(mFillPosition, newDataRow);
            mDataVector.setEntry(mFillPosition, reward);
            mFillPosition++;
        }
        
        // if we have gained enough data, then we can solve the system of linear equations
        if (mFillPosition > mNumberOfVariables) {
            solveCostParameters();
        }
    }
    
    /**
     * Solves the linear equation given with the data matrix and data vector,
     * sets the cost parameters in the model
     * and sets cost parameters to true
     * 
     * Should only be called once!
     */
    private void solveCostParameters() {
        DecompositionSolver solver = new LUDecomposition(mDataMatrix).getSolver();
        mCostParameters = solver.solve(mDataVector);
        mModel.setCostParameters(mCostParameters);
        mCostParametersFound = true;
        
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
     * Takes a state and an action and fills a row vector with
     * (#invadedHabitatsTotal, #emptyHabitatsTotal, #invadedReaches, 
     *  #eradicatedReaches, #restoredReaches+#eradicatedAndRestoredReaches, 
     *  #eradticatedTreesTotal, #restoredTreesTotal, #eradicatedAndRestoredTreesTotal).
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
        newDataRow.setEntry(0, numberOfInvadedHabitats);
        newDataRow.setEntry(1, numberOfEmptyHabitats);
        newDataRow.setEntry(2, numberOfInvadedReaches);
        // action-dependent
        newDataRow.setEntry(3, numberOfEradicatedReaches);
        newDataRow.setEntry(4, numberOfRestoredReaches+numberOfEradicatedAndRestoredReaches);
        newDataRow.setEntry(5, numberOfEradicatedTrees);
        newDataRow.setEntry(6, numberOfRestoredTrees);
        newDataRow.setEntry(7, numberOfEradicatedAndRestoredTrees);
        
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
  
    
}
