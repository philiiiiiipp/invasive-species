package nl.uva.species.genetic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.uva.species.model.EnvModel;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.rlcommunity.rlglue.codec.types.Action;

public class GeneticModelCreator {

	/** The past river states */
	private final List<List<RiverState>> mStates = new ArrayList<>();

	private final List<List<Action>> mActions = new ArrayList<>();

	private final int mEvolutions;

	/** The standard population size */
	public final int STANDARD_POP_SIZE = 100;

	public final int STANDARD_EVOLUTIONS = 5;

	private Genotype mGenotype;

	private static String[] GEN_NAMES = new String[] { "ENDO_TAMARISK", "UPSTREAM_RATE", "DOWNSTREAM_RATE",
			"ERADICATION_RATE", "RESTORATION_RATE", "DEATH_RATE_TAMARISK", "DEATH_RATE_NATIVE" };

	/**
	 * Create a genetic model creator with standard values and the given river
	 * 
	 * @param river
	 *            The river as the basis of the model
	 */
	public GeneticModelCreator(final River river) {
		mEvolutions = STANDARD_EVOLUTIONS;
		int geneNumber = EnvModel.Parameter.values().length + river.getNumReaches() * 2;
		mGenotype = initialiseGenotype(STANDARD_POP_SIZE, river, geneNumber);
	}

	public void reinitialise(final River river) {
		initialiseGenotype(STANDARD_POP_SIZE, river, EnvModel.Parameter.values().length + river.getNumReaches() * 2);
	}

	/**
	 * Initialize a genotype with the given parameters
	 * 
	 * @param populationSize
	 *            The size of the population
	 * @return The generated genotype
	 */
	private Genotype initialiseGenotype(final int populationSize, final River river, final int geneNumber) {
		Configuration.reset();
		Configuration gaConf = new DefaultConfiguration();
		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(false);

		Genotype genotype = null;
		try {
			IChromosome sampleChromosome = new Chromosome(gaConf, new DoubleGene(gaConf, 0, 1), geneNumber);

			gaConf.setAlwaysCaculateFitness(true);
			gaConf.setSampleChromosome(sampleChromosome);
			gaConf.setPopulationSize(populationSize);

			gaConf.setFitnessFunction(new EvaluateModel(mStates, mActions, river));
			genotype = Genotype.randomInitialGenotype(gaConf);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		return genotype;
	}

	public EnvModel getBestModel(final River river) {
		int stateCounter = 0;
		for (List<RiverState> riverList : mStates) {
			stateCounter += riverList.size();
		}

		if (stateCounter < 3) {
			// we have no data return our prior
			return new EnvModel(river, false);
		}

		for (int i = 0; i < mEvolutions; i++) {
			mGenotype.evolve();
			System.out.println(i + ": " + mGenotype.getFittestChromosome().getFitnessValue());
		}

		// Print summary
		IChromosome fittest = mGenotype.getFittestChromosome();

		System.out.println("== Fittest Cromosome ==");

		DecimalFormat df = new DecimalFormat("#.####");
		for (int i = 0; i < GEN_NAMES.length; ++i) {
			System.out.println(GEN_NAMES[i] + " \t " + df.format(((DoubleGene) fittest.getGenes()[i]).doubleValue()));
		}

		System.out.println("==ExoToEndoRatio==");
		for (int i = GEN_NAMES.length; i < GEN_NAMES.length + river.getNumReaches(); ++i) {
			System.out.println((i - GEN_NAMES.length) + ": "
					+ df.format(((DoubleGene) fittest.getGenes()[i]).doubleValue()));
		}

		System.out.println("==ExoTamarisk==");
		for (int i = GEN_NAMES.length + river.getNumReaches(); i < GEN_NAMES.length + 2 * river.getNumReaches(); ++i) {
			System.out.println((i - GEN_NAMES.length + river.getNumReaches()) + ": "
					+ df.format(((DoubleGene) fittest.getGenes()[i]).doubleValue()));
		}

		System.out.println("Fittest Chromosome has fitness " + fittest.getFitnessValue());
		return new EnvModel(river, Arrays.copyOf(fittest.getGenes(), fittest.getGenes().length, DoubleGene[].class));
	}

	/**
	 * Finish the current episode, creating space for new states and actions
	 */
	public void finishEpisode() {
		mStates.add(new ArrayList<RiverState>());
		mActions.add(new ArrayList<Action>());
	}

	/**
	 * Add the river state to the river state observation list
	 * 
	 * @param riverState
	 * @return true if the action was successful
	 */
	public boolean addRiverState(final RiverState riverState) {
		return mStates.get(mStates.size() - 1).add(riverState);
	}

	/**
	 * Add the action to the action list
	 * 
	 * @param a
	 *            The action to add
	 * @return true if the action was successful
	 */
	public boolean addAction(final Action a) {
		return mActions.get(mStates.size() - 1).add(a);
	}
}
