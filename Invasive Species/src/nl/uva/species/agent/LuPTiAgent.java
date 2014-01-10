package nl.uva.species.agent;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class LuPTiAgent implements AgentInterface {

	@Override
	public void agent_init(final String taskSpecification) {
		TaskSpec theTaskSpec = new TaskSpec(taskSpecification);

		// Assume the message is valid
		System.err.println("NumDiscreteActionDims "
				+ theTaskSpec.getNumDiscreteActionDims());

		System.err.println("Bad_Action_Penalty "
				+ theTaskSpec.getRewardRange().getMin());
		System.err.println("rewardRangeMin "
				+ theTaskSpec.getRewardRange().getMin());
		System.err.println("rewardRangeMax "
				+ theTaskSpec.getRewardRange().getMax());
		System.err.println("habitatSize " + theTaskSpec.getNumDiscreteObsDims()
				/ theTaskSpec.getNumDiscreteActionDims());
		System.err.println("DiscountFactor " + theTaskSpec.getDiscountFactor());

		String[] theExtra = theTaskSpec.getExtraString().split("BUDGET");

		System.err.println("edges " + theExtra[0]);

		System.err.println("budget "
				+ Double.parseDouble(theExtra[1].split("by")[0]));

	}

	@Override
	public Action agent_start(final Observation observation) {
		System.out.println("Agent_Start");

		int[] theState = observation.intArray;
		Action returnAction = new Action();

		for (int i = 0; i < theState.length; ++i) {
			theState[i] = 0;
		}

		returnAction.intArray = theState;

		return returnAction;
	}

	@Override
	public Action agent_step(final double reward, final Observation observation) {
		System.out.println("Agent_Step");
		return null;
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
	public String agent_message(final String message) {
		System.out.println("Agent_Message: ");
		System.out.println(message);
		return null;
	}

	/**
	 * Load our agent with the AgentLoader and automatically connect to the
	 * rl_glue server.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		AgentLoader theLoader = new AgentLoader(new LuPTiAgent());
		theLoader.run();
	}

}
