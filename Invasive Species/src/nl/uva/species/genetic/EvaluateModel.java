package nl.uva.species.genetic;

import java.util.List;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;
import org.rlcommunity.rlglue.codec.types.Action;

public class EvaluateModel extends FitnessFunction {

	/**
	 * Generated default serial UID
	 */
	private static final long serialVersionUID = 3905984536032544547L;

	/** The list of all previous river states */
	private final List<List<RiverState>> mRiverState;

	/** The list of all previous actions */
	private final List<List<Action>> mActions;

	/** The current river */
	private final River mRiver;

	public EvaluateModel(final List<List<RiverState>> riverStates, final List<List<Action>> actions, final River river) {
		mRiverState = riverStates;
		mActions = actions;
		mRiver = river;
	}

	@Override
	protected double evaluate(final IChromosome a_subject) {

		SuperGene[] genes = new SuperGene[a_subject.getGenes().length];
		for (int i = 0; i < a_subject.getGenes().length; ++i) {
			genes[i] = (SuperGene) a_subject.getGene(i);
		}

		EnvModel model = new EnvModel(mRiver, genes);
		double result = 0;
		int steps = 0;
		for (int i = 0; i < mRiverState.size(); ++i) {
			for (int j = 0; j < mRiverState.get(i).size() - 1; ++j) {
				steps++;
				result += model.evaluateModel(mRiverState.get(i).get(j), mActions.get(i).get(j), mRiverState.get(i)
						.get(j + 1));
			}
		}
		return result / steps;
	}
}
