package nl.uva.species.agent;

import java.util.Arrays;
import java.util.Random;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;
import nl.uva.species.utils.CostSolver;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class CostTestAgent implements AgentInterface {

    private River mRiver;
    private EnvModel mModel;
    private CostSolver mCostSolver;
    
    // the last state and action, which the reward depends on
    private RiverState mLastState;
    private Action mLastActions;
    
    private Random mRand;
    private double mBadActionPenalty;
    
    @Override
    public void agent_init(final String taskSpecification) {
        // System.out.println(taskSpecification);
        TaskSpec theTaskSpec = new TaskSpec(taskSpecification);

        // Assume the message is valid
        System.err.println("NumDiscreteActionDims " + theTaskSpec.getNumDiscreteActionDims());

        System.err.println("Bad_Action_Penalty " + theTaskSpec.getRewardRange().getMin());
        System.err.println("rewardRangeMin " + theTaskSpec.getRewardRange().getMin());
        System.err.println("rewardRangeMax " + theTaskSpec.getRewardRange().getMax());
        System.err.println("habitatSize " + theTaskSpec.getNumDiscreteObsDims()
                / theTaskSpec.getNumDiscreteActionDims());
        System.err.println("DiscountFactor " + theTaskSpec.getDiscountFactor());

        String[] theExtra = theTaskSpec.getExtraString().split("BUDGET");

        System.err.println("edges " + theExtra[0]);

        System.err.println("budget " + Double.parseDouble(theExtra[1].split("by")[0]));

        mRiver = new River(theTaskSpec);
        
        // added this to the init method
        mRand = new Random();
        mModel = new EnvModel(mRiver, false);
        mCostSolver = new CostSolver(mModel);
        
    }

    @Override
    public Action agent_start(final Observation observation) {
                
        // System.out.println("START");
        
        Action defaultAction = new Action();
        defaultAction.intArray = new int[7];
        Arrays.fill(defaultAction.intArray, Utilities.ACTION_ERADICATE_RESTORE);
        int[] theState = new int[7];
        Action returnAction = new Action();
        for (int i = 0; i < 7; ++i) {
            theState[i] = Utilities.ACTION_NOTHING;
            int tam = 0;
            int empty = 0;
            for (int j = 0; j < 4; ++j) {
                if (observation.intArray[i * 4 + j] == Utilities.HABITAT_INVADED) ++tam;
                if (observation.intArray[i * 4 + j] == Utilities.HABITAT_EMPTY) ++empty;
            }
            double rnd = mRand.nextDouble();
            if (tam >= 1 && rnd < 0.4) theState[i] = Utilities.ACTION_ERADICATE_RESTORE;
            else if (tam >= 1 && rnd < 0.7) theState[i] = Utilities.ACTION_ERADICATE;
            else if (empty >= 1) theState[i] = Utilities.ACTION_RESTORE;
        }

        returnAction.intArray = theState;
        
        // update last state and action
        mLastState = new RiverState(mRiver, observation);  
        mLastActions = returnAction;
        
        return returnAction;
    }

    @Override
    public Action agent_step(final double reward, final Observation observation) {
                             
        RiverState state = new RiverState(mRiver, observation);
        
        Action defaultAction = new Action();
        defaultAction.intArray = new int[7];
        Arrays.fill(defaultAction.intArray, Utilities.ACTION_ERADICATE_RESTORE);
        int[] theState = new int[7];
        Action returnAction = new Action();
        for (int i = 0; i < 7; ++i) {
            theState[i] = Utilities.ACTION_NOTHING;
            int tam = 0;
            int empty = 0;
            for (int j = 0; j < 4; ++j) {
                if (observation.intArray[i * 4 + j] == Utilities.HABITAT_INVADED) ++tam;
                if (observation.intArray[i * 4 + j] == Utilities.HABITAT_EMPTY) ++empty;
            }
            double rnd = mRand.nextDouble();
            if (tam >= 1 && rnd < 0.4) theState[i] = Utilities.ACTION_ERADICATE_RESTORE;
            else if (tam >= 1 && rnd < 0.8) theState[i] = Utilities.ACTION_ERADICATE;
            else if (empty >= 1) theState[i] = Utilities.ACTION_RESTORE;
        }

        // if the cost parameters haven't been found yet, add the new data to the solver
        if (!mCostSolver.costParametersFound() && reward != mBadActionPenalty) {
            mCostSolver.addData(mLastState, mLastActions, reward);
         }

        returnAction.intArray = theState;
        
        // update last state and action
        mLastState = state;
        mLastActions = returnAction;

        return returnAction;
    }

    @Override
    public void agent_end(final double reward) {
        System.out.println("Agent_End");
    }

    @Override
    public void agent_cleanup() {
        System.out.println("Agent_Cleanup");

    }

    @Override
    public String agent_message(final String inMessage) {
        System.out.println("Agent_Message: " + inMessage);

        // Message Description
        // 'freeze learning'
        // Action{ Set flag to stop updating policy
        //
        if (inMessage.startsWith("freeze learning")) {
            return "message understood, policy frozen";
        }
        // Message Description
        // unfreeze learning
        // Action{ Set flag to resume updating policy
        //
        if (inMessage.startsWith("unfreeze learning")) {
            return "message understood, policy unfrozen";
        }
        // Message Description
        // freeze exploring
        // Action{ Set flag to stop exploring (greedy actions only)
        //
        if (inMessage.startsWith("freeze exploring")) {
            return "message understood, exploring frozen";
        }
        // Message Description
        // unfreeze exploring
        // Action{ Set flag to resume exploring (e-greedy actions)
        //
        if (inMessage.startsWith("unfreeze exploring")) {
            return "message understood, exploring frozen";
        }
        return "Invasive agent does not understand your message.";
    }

    /**
     * Load our agent with the AgentLoader and automatically connect to the rl_glue server.
     * 
     * @param args
     */
    public static void main(final String[] args) {
        AgentLoader theLoader = new AgentLoader(new CostTestAgent());
        theLoader.run();
    }

}
