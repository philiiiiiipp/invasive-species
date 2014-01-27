package nl.uva.species.agent;

import nl.uva.species.model.River;
import nl.uva.species.utils.Messages;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public abstract class AbstractAgent implements AgentInterface {

    private boolean mLearningCosts = false;
    private boolean mLearningModel = false;
    private boolean mFollowingHeuristics = false;
    private boolean mPlanning = false;
    private boolean mEvaluating = false;
    
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
        // ----- message handling for our experiment (ourExperiment.py)
        mLearningCosts = false;
        mLearningModel = false;
        mFollowingHeuristics = false;
        mPlanning = false;
        mEvaluating = false;
        if (inMessage.startsWith(Messages.LEARN_COST_PARAM.incomingMessage())) {
            mLearningCosts = true;
            message(Messages.LEARN_COST_PARAM);
            return Messages.LEARN_COST_PARAM.outgoingMessage();
        }
        if (inMessage.startsWith(Messages.LEARN_MODEL.incomingMessage())) {
            mLearningModel = true;
            message(Messages.LEARN_MODEL);
            return Messages.LEARN_MODEL.outgoingMessage();
        }
        if (inMessage.startsWith(Messages.FOLLOW_HEURISTICS.incomingMessage())) {
            mFollowingHeuristics = true;
            message(Messages.FOLLOW_HEURISTICS);
            return Messages.FOLLOW_HEURISTICS.outgoingMessage();
        }
        if (inMessage.startsWith(Messages.PLAN.incomingMessage())) {
            mPlanning = true;
            message(Messages.PLAN);
            return Messages.PLAN.outgoingMessage();
        }
        if (inMessage.startsWith(Messages.EVALUATE.incomingMessage())) {
            mEvaluating = true;
            message(Messages.EVALUATE);
            return Messages.EVALUATE.outgoingMessage();
        }
        // ----- message handling for their experiment
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

    public boolean isFollowingHeuristics() {
        return mFollowingHeuristics;
    }
    
    public boolean isExploring() {
        return mExploring;
    }
    
    public boolean isLearningCosts() {
        return mLearningCosts;
    }
    
    public boolean isLearningModel() {
        return mLearningModel;
    }
    
    public boolean isPlanning() {
        return mPlanning;
    }
    
    public boolean isEvaluating() {
        return mEvaluating;
    }

}
