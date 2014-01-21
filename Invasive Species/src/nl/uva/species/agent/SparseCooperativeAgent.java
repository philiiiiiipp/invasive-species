package nl.uva.species.agent;

import java.util.Arrays;

import javax.swing.JFrame;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;
import nl.uva.species.ui.GraphInterface;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class SparseCooperativeAgent implements AgentInterface {

    private boolean mPolicyFrozen;
    private boolean mExploringFrozen;
    
    private River mRiver;
    private EnvModel mModel;
    
    public SparseCooperativeAgent() {
    }
    
    public SparseCooperativeAgent(final EnvModel model) {
        mModel = model;
    }

    @Override
    public void agent_init(final String taskSpecification) {
        System.out.println(taskSpecification);
        if (mModel == null) {
            mRiver = new River(new TaskSpec(taskSpecification));
            mModel = new EnvModel(mRiver, false);
        }
    }

    @Override
    public Action agent_start(final Observation observation) {

        Action defaultAction = new Action();
        defaultAction.intArray = new int[7];
        Arrays.fill(defaultAction.intArray, Utilities.ACTION_ERADICATE_RESTORE);

        EnvModel model = new EnvModel(mRiver, false);
        RiverState state = new RiverState(mRiver, observation);

        return null;
    }

    @Override
    public Action agent_step(final double reward, final Observation observation) {
        return agent_start(observation);
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

        if (inMessage.startsWith("freeze learning")) {
            mPolicyFrozen = true;
            return "message understood, policy frozen";
        }

        if (inMessage.startsWith("unfreeze learning")) {
            mPolicyFrozen = false;
            return "message understood, policy unfrozen";
        }

        if (inMessage.startsWith("freeze exploring")) {
            mExploringFrozen = true;
            return "message understood, exploring frozen";
        }

        if (inMessage.startsWith("unfreeze exploring")) {
            mExploringFrozen = false;
            return "message understood, exploring frozen";
        }

        return "Invasive agent does not understand your message.";
    }

    
    private Action getAction(final Observation observation) {
        
        
        
        
        return null;
    }
    
    
    /**
     * Load our agent with the AgentLoader and automatically connect to the rl_glue server.
     * 
     * @param args
     */
    public static void main(final String[] args) {
        AgentLoader theLoader = new AgentLoader(new SparseCooperativeAgent());
        theLoader.run();
    }

}
