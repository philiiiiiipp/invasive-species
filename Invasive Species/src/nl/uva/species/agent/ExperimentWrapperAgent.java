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
public class ExperimentWrapperAgent extends AbstractAgent {

    private River mRiver;

    /** The cost solver which estimates the cost parameters, before the learner or the planner start */
    private CostSolverAgent mCostSolver = new CostSolverAgent();

    /** The genetic learner that learns the model */
    private final GeneticLearner mLearner = new GeneticLearner();

    /** The Planner that plans on a model given by the genetic learner */
    private final SparseCooperativeAgent mPlanner = new SparseCooperativeAgent();

    /** The really clever heuristic agent */
    private final SimpleHeuristicsAgent mHeuristicAgent = new SimpleHeuristicsAgent();

    @Override
    public void init(final River river) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        mRiver = river;
        mCostSolver.init(mRiver);
        mLearner.init(mRiver);
        mPlanner.init(mRiver);
        mHeuristicAgent.init(mRiver);
    }

    @Override
    public Action start(final Observation observation) {
        if (isLearningCosts()) {
            return mCostSolver.start(observation);
        } else if (isLearningModel()) {
            return mLearner.start(observation);
        } else if (isFollowingHeuristics()) {
            return mHeuristicAgent.start(observation);
        } else { // if isEvaluating()

            return mPlanner.start(observation);
        }
    }

    @Override
    public Action step(final double reward, final Observation observation) {
        if (isLearningCosts()) {
            return mCostSolver.step(reward, observation);
        } else if (isLearningModel()) {
            return mLearner.step(reward, observation);
        } else if (isFollowingHeuristics()) {
            return mHeuristicAgent.step(reward, observation);
        } else { // if isEvaluating()
            return mPlanner.step(reward, observation);
        }
    }

    @Override
    public void end(final double reward) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        mCostSolver.end(reward);
        mLearner.end(reward);
        mPlanner.end(reward);
        mHeuristicAgent.end(reward);
    }

    @Override
    public void cleanup() {
        mCostSolver.cleanup();
        mLearner.cleanup();
        mPlanner.cleanup();
        mHeuristicAgent.cleanup();
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
        AgentLoader theLoader = new AgentLoader(new ExperimentWrapperAgent());
        theLoader.run();
    }
}
