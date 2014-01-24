package nl.uva.species.agent;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.JFrame;

import nl.uva.species.genetic.GeneticModelCreator;
import nl.uva.species.model.EnvModel;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;
import nl.uva.species.ui.GraphInterface;
import nl.uva.species.utils.Messages;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class GeneticLearner extends AbstractLearnerAgent {

	/** The amount of nothing action taken during learning */
	private final int AMOUNT_NOTHING_ACTION = 100;

	/** The amount of eradicate action taken during learning */
	private final int AMOUNT_ERADICATE_ACTION = 100;

	/** The amount of eradicate and restore action taken during learning */
	private final int AMOUNT_ERADICATE_RESTORE_ACTION = 100;

	/** The supported phases of the environment */
	private enum Phase {
		LEARNING,
		PLANNING
	};

	/** The current phase */
	private Phase mCurrentPhase = Phase.LEARNING;

	/** The river where our model bases on */
	private River mRiver;

	/** The generic algorithm */
	private GeneticModelCreator mModelGenerator;

	/** The last calculated most probable model */
	private EnvModel mCurrentModel;

	/** Episode counter */
	private int mEpisodeCount = 0;

	/** Step counter inside each episode */
	private int mStepCount = 0;

	/** Graph update counter */
	private int mGraphVisibilityCounter = 0;

	/** The graph interface */
	private GraphInterface mGraphInterface = new GraphInterface();

	@Override
	public void init(final River river) {
		mRiver = river;

//		mGraphInterface.init();
//		JFrame frame = new JFrame();
//		frame.getContentPane().add(mGraphInterface);
//		frame.setTitle("The invasive species domain");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setVisible(true);

		mCurrentModel = new EnvModel(mRiver, false);

//		try {
			// Set the err output to /dev/null, most probable works only under linux/mac (Guess who wrote that). This is
			// there to prevent the graph from spmaming the whole console.
//			System.setErr(null);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}

		if (mModelGenerator == null)
			mModelGenerator = new GeneticModelCreator(mRiver);
	}

	@Override
	public Action start(final Observation observation) {
		mStepCount = 0;
		++mEpisodeCount;

		if (mCurrentPhase == Phase.LEARNING) {
			mModelGenerator.finishEpisode();
			System.out.print(" " + mEpisodeCount);
		} else {
			System.out.print(" " + mEpisodeCount);
		}

		RiverState currentState = new RiverState(mRiver, observation);
		Action action = null;

		if (mCurrentPhase == Phase.LEARNING) {
			action = getHeuristicNextAction(new RiverState(mRiver, observation), mStepCount, AMOUNT_NOTHING_ACTION,
					AMOUNT_ERADICATE_ACTION, AMOUNT_ERADICATE_RESTORE_ACTION);

			mModelGenerator.addRiverState(new RiverState(mRiver, observation));
			mModelGenerator.addAction(action);
		} else {
			action = mCurrentModel.getBestAction(currentState);
		}

		mStepCount++;

		return action;
	}

	@Override
	public Action step(final double reward, final Observation observation) {
		RiverState currentState = new RiverState(mRiver, observation);
		Action action = null;

		if (mCurrentPhase == Phase.LEARNING) {
			action = getHeuristicNextAction(new RiverState(mRiver, observation), mStepCount, AMOUNT_NOTHING_ACTION,
					AMOUNT_ERADICATE_ACTION, AMOUNT_ERADICATE_RESTORE_ACTION);

			mModelGenerator.addRiverState(new RiverState(mRiver, observation));
			mModelGenerator.addAction(action);
		} else {
			action = mCurrentModel.getBestAction(currentState);
		}

		if (mCurrentPhase == Phase.PLANNING && mGraphVisibilityCounter % 100 < 10) {

//			mGraphInterface.update(currentState);
//			mGraphInterface.showActions(action.intArray);

			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mGraphVisibilityCounter++;

		mStepCount++;
		return action;
	}

	@Override
	public void end(final double reward) {

	}

	@Override
	public void cleanup() {
	}

	@Override
	public void message(final Messages message) {

		if (message.equals(Messages.FREEZE_LEARNING)) {
			mCurrentPhase = Phase.PLANNING;
			mCurrentModel = mModelGenerator.getBestModel(mRiver);
			mEpisodeCount = 0;
			System.out.print("Planning step: ");
		}

		if (message.equals(Messages.UNFREEZE_LEARNING)) {
			mCurrentPhase = Phase.LEARNING;
			mEpisodeCount = 0;
			System.out.print("Learning step: ");
			mModelGenerator.reinitialise(mRiver);
		}
	}
	
	public EnvModel getModel() {
	    return mCurrentModel;
	}

	/**
	 * Load our agent with the AgentLoader and automatically connect to the rl_glue server.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		AgentLoader theLoader = new AgentLoader(new GeneticLearner());
		theLoader.run();
	}
}
