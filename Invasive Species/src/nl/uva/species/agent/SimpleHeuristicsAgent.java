package nl.uva.species.agent;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.Reach;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;
import nl.uva.species.utils.Messages;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class SimpleHeuristicsAgent extends AbstractAgent {

    private River mRiver;

    private EnvModel mModel;

    @Override
    public void init(final River river) {
        mRiver = river;
        mModel = new EnvModel(river);
    }

    @Override
    public Action start(final Observation observation) {
        final RiverState state = new RiverState(mRiver, observation);
        double budgetLeft = mRiver.getBudget();

        final Action action = new Action();
        action.intArray = new int[7];

        for (final Reach reach : state.getReaches()) {
            final int index = reach.getIndex();
            final double cost = Math.abs(mModel.getSingleActionReward(reach,
                    Utilities.ACTION_ERADICATE_RESTORE));
            if (cost <= budgetLeft) {
                action.intArray[index] = Utilities.ACTION_ERADICATE_RESTORE;
                budgetLeft -= cost;
            } else {
                action.intArray[index] = Utilities.ACTION_NOTHING;
            }
        }
        return action;
    }

    @Override
    public Action step(final double reward, final Observation observation) {
        return start(observation);
    }

    @Override
    public void end(final double reward) {}

    @Override
    public void cleanup() {}

    @Override
    public void message(final Messages message) {}

    /**
     * Load our agent with the AgentLoader and automatically connect to the rl_glue server.
     * 
     * @param args
     */
    public static void main(final String[] args) {
        AgentLoader theLoader = new AgentLoader(new SimpleHeuristicsAgent());
        theLoader.run();
    }

}
