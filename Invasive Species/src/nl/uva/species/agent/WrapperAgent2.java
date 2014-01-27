package nl.uva.species.agent;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.River;
import nl.uva.species.utils.Messages;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

/**
 * This wrapper can be used with ourExperiment.py
 *
 */
public class WrapperAgent2 extends AbstractAgent {

    private River mRiver;

    /** The cost solver which estimates the cost parameters, before the learner or the planner start */
    private CostSolverAgent mCostSolver = new CostSolverAgent();

    /** The genetic learner that learns the model */
    private final GeneticLearner mLearner = new GeneticLearner();
    
    /** The Planner that plans on a model given by the genetic learner */
    private final SparseCooperativeAgent mPlanner = new SparseCooperativeAgent();

    @Override
    public void init(final River river) {
        mRiver = river;
        mCostSolver.init(mRiver);
        mLearner.init(mRiver);
        mPlanner.init(mRiver);
    }

    @Override
    public Action start(final Observation observation) {
        if (isLearningCosts()) {
            return mCostSolver.start(observation);
        } else if (isLearning()) {
            return mLearner.start(observation);
        } else { // if isEvaluating()
            return mPlanner.start(observation);
        }
    }

    @Override
    public Action step(final double reward, final Observation observation) {
        if (isLearningCosts()) {
            return mCostSolver.step(reward, observation);
        } else if (isLearning()) {
            return mLearner.step(reward, observation);
        } else { // if isEvaluating()
            return mPlanner.step(reward, observation);
        }
    }

    @Override
    public void end(final double reward) {
        mCostSolver.end(reward);
        mLearner.end(reward);
        mPlanner.end(reward);
    }

    @Override
    public void cleanup() {
        mCostSolver.cleanup();
        mLearner.cleanup();
        mPlanner.cleanup();
    }

    @Override
    public void message(Messages message) {
        mCostSolver.message(message);
        mLearner.message(message);
        mPlanner.message(message);
        // if the message is PLAN, then we give the (learned) model to the planner
        if (message == Messages.PLAN) {
            EnvModel model = mLearner.getModel();
            if (mCostSolver.costParametersFound()) {
                model.setCostParameters(mCostSolver.getCostParameters());
            }
            mPlanner.setModel(mLearner.getModel());
        }
    }

    /**
     * Load our agent with the AgentLoader and automatically connect to the rl_glue server.
     * 
     * @param args
     */
    public static void main(final String[] args) {
        AgentLoader theLoader = new AgentLoader(new WrapperAgent2());
        theLoader.run();
    }
}
