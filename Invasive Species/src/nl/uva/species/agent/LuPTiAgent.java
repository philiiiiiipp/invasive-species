package nl.uva.species.agent;

import java.util.Arrays;

import nl.uva.species.model.River;
import nl.uva.species.utils.Utilities;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class LuPTiAgent implements AgentInterface {

	private boolean policyFrozen;
	private boolean exploringFrozen;
	private River river;

	@Override
	public void agent_init(final String taskSpecification) {
		System.out.println(taskSpecification);
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

		river = new River(theTaskSpec);
	}

	int print = 0;

	@Override
	public Action agent_start(final Observation observation) {

		if (print > 1)
			return null;

		print++;

		System.out.println("================ START =================");
		Action defaultAction = new Action();
		defaultAction.intArray = new int[7];
		Arrays.fill(defaultAction.intArray, Utilities.ACTION_ERADICATE_RESTORE);

		// GraphInterface applet = new GraphInterface(state);
		// applet.init();
		//
		// JFrame frame = new JFrame();
		// frame.getContentPane().add(applet);
		// frame.setTitle("The invasive species domain");
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.pack();
		// frame.setVisible(true);

		// boolean loop = false;
		// while (loop) {
		// state = model.getPossibleNextState(state, defaultAction);
		// applet.update(state);
		//
		// try {
		// Thread.sleep(300);
		// } catch (InterruptedException ex) {
		// // TODO Auto-generated catch block
		// ex.printStackTrace();
		// }
		// }

		// System.out.println("Agent_Start: " + observation.intArray.length);

		int[] theState = new int[7];
		Action returnAction = new Action();

		for (int i = 0; i < 7; ++i) {

			theState[i] = Utilities.ACTION_NOTHING;

		}

		returnAction.intArray = theState;

		System.out.println("o = new Observation();");
		System.out.print("o.intArray = new int[] {");
		for (int i = 0; i < observation.intArray.length; ++i) {
			if (i < observation.intArray.length - 1) {
				System.out.print(observation.intArray[i] + ",");
			} else {
				System.out.println(observation.intArray[i] + "};");
			}
		}
		System.out.println("g.addRiverState(new RiverState(river, o));");

		return returnAction;
	}

	@Override
	public Action agent_step(final double reward, final Observation observation) {

		Action defaultAction = new Action();
		defaultAction.intArray = new int[7];
		Arrays.fill(defaultAction.intArray, Utilities.ACTION_ERADICATE_RESTORE);

		// System.out.println("Agent_Start: " + observation.intArray.length);

		int[] theState = new int[7];
		Action returnAction = new Action();

		for (int i = 0; i < 7; ++i) {
			theState[i] = Utilities.ACTION_NOTHING;
		}

		returnAction.intArray = theState;

		System.out.println("o = new Observation();");
		System.out.print("o.intArray = new int[] {");
		for (int i = 0; i < observation.intArray.length; ++i) {
			if (i < observation.intArray.length - 1) {
				System.out.print(observation.intArray[i] + ",");
			} else {
				System.out.println(observation.intArray[i] + "};");
			}
		}
		System.out.println("g.addRiverState(new RiverState(river, o));");

		System.out.println("g.getBestModel(river);");

		System.out.println("a = new Action();");
		System.out.print("a.intArray = new int[] {");
		for (int i = 0; i < theState.length; ++i) {
			if (i < theState.length - 1) {
				System.out.print(theState[i] + ",");
			} else {
				System.out.println(theState[i] + "};");
			}
		}
		System.out.println("g.addAction(a);");

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
			this.policyFrozen = true;
			return "message understood, policy frozen";
		}
		// Message Description
		// unfreeze learning
		// Action{ Set flag to resume updating policy
		//
		if (inMessage.startsWith("unfreeze learning")) {
			this.policyFrozen = false;
			return "message understood, policy unfrozen";
		}
		// Message Description
		// freeze exploring
		// Action{ Set flag to stop exploring (greedy actions only)
		//
		if (inMessage.startsWith("freeze exploring")) {
			this.exploringFrozen = true;
			return "message understood, exploring frozen";
		}
		// Message Description
		// unfreeze exploring
		// Action{ Set flag to resume exploring (e-greedy actions)
		//
		if (inMessage.startsWith("unfreeze exploring")) {
			this.exploringFrozen = false;
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
		AgentLoader theLoader = new AgentLoader(new LuPTiAgent());
		theLoader.run();
	}

}
