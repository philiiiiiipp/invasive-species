package nl.uva.species.genetic;

import java.util.List;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;
import org.jgap.impl.DoubleGene;
import org.rlcommunity.rlglue.codec.types.Action;

public class EvaluateModel extends FitnessFunction {

	/**
	 * Generated default serial UID
	 */
	private static final long serialVersionUID = 3905984536032544547L;

	/** The list of all previous river states */
	private final List<RiverState> mRiverState;

	/** The list of all previous actions */
	private final List<Action> mActions;

	/** The current river */
	private final River mRiver;

	public EvaluateModel(final List<RiverState> riverStates, final List<Action> actions, final River river) {
		mRiverState = riverStates;
		mActions = actions;
		mRiver = river;
	}

	@Override
	protected double evaluate(final IChromosome a_subject) {
		EnvModel model = new EnvModel(mRiver, (DoubleGene[]) a_subject.getGenes());
		double result = 0;

		for (int i = 0; i < mRiverState.size() - 1; ++i) {
			result += model.evaluateModel(mRiverState.get(i), mActions.get(i), mRiverState.get(i + 1));
		}

		return result / mActions.size();
	}
}
