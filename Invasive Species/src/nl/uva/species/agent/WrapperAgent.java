package nl.uva.species.agent;

import nl.uva.species.model.River;
import nl.uva.species.utils.Messages;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class WrapperAgent extends AbstractAgent {

    private final GeneticLearner mLearner = new GeneticLearner();
    private final SparseCooperativeAgent mPlanner = new SparseCooperativeAgent();

    @Override
    public void init(River river) {
        mLearner.init(river);
        mPlanner.init(river);
    }

    @Override
    public Action start(final Observation observation) {
        if (isLearning()) {
            return mLearner.start(observation);
        } else {
            return mPlanner.start(observation);
        }
    }

    @Override
    public Action step(final double reward, final Observation observation) {
        if (isLearning()) {
            return mLearner.step(reward, observation);
        } else {
            return mPlanner.step(reward, observation);
        }
    }

    @Override
    public void end(final double reward) {
        mLearner.end(reward);
        mPlanner.end(reward);
    }

    @Override
    public void cleanup() {
        mLearner.cleanup();
        mPlanner.cleanup();
    }

    @Override
    public void message(Messages message) {
        mLearner.message(message);
        mPlanner.message(message);
        
        if (message == Messages.FREEZE_LEARNING) {
            mPlanner.setModel(mLearner.getModel());
        }
    }

    /**
     * Load our agent with the AgentLoader and automatically connect to the rl_glue server.
     * 
     * @param args
     */
    public static void main(final String[] args) {
        AgentLoader theLoader = new AgentLoader(new WrapperAgent());
        theLoader.run();
    }
}
