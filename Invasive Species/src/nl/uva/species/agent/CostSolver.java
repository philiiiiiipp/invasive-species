package nl.uva.species.agent;

import java.io.Serializable;

import nl.uva.species.model.Reach;
import nl.uva.species.model.RiverState;
import nl.uva.species.utils.Utilities;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.rlcommunity.rlglue.codec.types.Action;

public class CostSolver {

    /** Indicator whether the cost parameters are found already */
    private boolean mCostParametersFound = false;
    
    /** The cost per invaded reach */
    private double mCostInvadedReach;

    /** The cost per habitat containing a Tamarisk */
    private double mCostHabitatTamarisk;

    /** The cost per empty habitat */
    private double mCostHabitatEmpty;

    /** The consistent cost of one eradication */
    private double mCostEradicate;

    /** The consistent cost of one restoration, and for a eradicating and restoring */
    private double mCostRestorate;

    /** The variable cost for each Tamarisk plant attempted to eradicate */
    private double mCostVariableEradicate ;

    /** The variable cost for each native plant attempted to eradicate */
    private double mCostVariableRestorate;

    /** The variable cost for each Tamarisk plant attempted to eradicate and restore */
    private double mCostVariableEradicateRestorate;
    
    public CostSolver() {
        new Array2DRowRealMatrix();
    }
 
    
    
}
