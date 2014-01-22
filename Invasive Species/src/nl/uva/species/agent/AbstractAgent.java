package nl.uva.species.agent;

import nl.uva.species.model.River;
import nl.uva.species.utils.Messages;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public abstract class AbstractAgent implements AgentInterface {

    private boolean mLearning = true;
    private boolean mExploring = true;

    @Override
    public void agent_init(final String taskSpecification) {
        this.init(new River(new TaskSpec(taskSpecification)));
    }

    public abstract void init(final River river);

    @Override
    public Action agent_start(final Observation observation) {
        return start(observation);
    }

    public abstract Action start(Observation observation);

    @Override
    public Action agent_step(final double reward, final Observation observation) {
        return step(reward, observation);
    }

    public abstract Action step(final double reward, final Observation observation);

    @Override
    public void agent_end(final double reward) {
        end(reward);
    }

    public abstract void end(final double reward);

    @Override
    public void agent_cleanup() {
        cleanup();
    }

    public abstract void cleanup();

    @Override
    public String agent_message(final String inMessage) {
        System.out.println();
        System.out.println(inMessage);
        // Message Description
        // 'freeze learning'
        // Action{ Set flag to stop updating policy
        //
        if (inMessage.startsWith(Messages.FREEZE_LEARNING.incomingMessage())) {
            mLearning = false;
            message(Messages.FREEZE_LEARNING);
            return Messages.FREEZE_LEARNING.outgoingMessage();
        }
        // Message Description
        // unfreeze learning
        // Action{ Set flag to resume updating policy
        //
        if (inMessage.startsWith(Messages.UNFREEZE_LEARNING.incomingMessage())) {
            mLearning = true;
            message(Messages.UNFREEZE_LEARNING);
            return Messages.UNFREEZE_LEARNING.outgoingMessage();
        }
        // Message Description
        // freeze exploring
        // Action{ Set flag to stop exploring (greedy actions only)
        //
        if (inMessage.startsWith(Messages.FREEZE_EXPLORING.incomingMessage())) {
            mExploring = false;
            message(Messages.FREEZE_EXPLORING);
            return Messages.FREEZE_EXPLORING.outgoingMessage();
        }
        // Message Description
        // unfreeze exploring
        // Action{ Set flag to resume exploring (e-greedy actions)
        //
        if (inMessage.startsWith(Messages.UNFREEZE_EXPLORING.incomingMessage())) {
            mExploring = false;
            message(Messages.UNFREEZE_EXPLORING);
            return Messages.UNFREEZE_EXPLORING.outgoingMessage();
        }
        return Messages.ERROR.outgoingMessage();
    }

    public abstract void message(Messages message);

    public boolean isLearning() {
        return mLearning;
    }

    public boolean isExploring() {
        return mExploring;
    }

}
