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
import org.rlcommunity.rlglue.codec.types.Action;

public class GeneticModelCreator {

	/** The past river states */
	private final List<List<RiverState>> mStates = new ArrayList<>();

	private final List<List<Action>> mActions = new ArrayList<>();

	private final int mEvolutions;

	/** The standard population size */
	public final int STANDARD_POP_SIZE = 50;

	public final int STANDARD_EVOLUTIONS = 3;

	private Genotype mGenotype;

	private static String[] GEN_NAMES = new String[] { "ENDO_TAMARISK   ", "UPSTREAM_RATE   ", "DOWNSTREAM_RATE",
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
			// IChromosome sampleChromosome = new Chromosome(gaConf, new DoubleGene(gaConf, 0, 1), geneNumber);
			IChromosome sampleChromosome = new Chromosome(gaConf, new SuperGene(gaConf), geneNumber);

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

		IChromosome fittest = null;
		double start = System.currentTimeMillis(), total = System.currentTimeMillis();
		for (int evolution = 0; evolution < mEvolutions; evolution++) {
			mGenotype.evolve();

			// Print summary
			fittest = mGenotype.getFittestChromosome();

			DecimalFormat df = new DecimalFormat("#.####");
			for (int i = 0; i < GEN_NAMES.length; ++i) {
				System.out
						.println(GEN_NAMES[i] + " \t " + df.format(((SuperGene) fittest.getGenes()[i]).doubleValue()));
			}

			System.out.println("==ExoToEndoRatio==");
			for (int i = GEN_NAMES.length; i < GEN_NAMES.length + river.getNumReaches(); ++i) {
				System.out.println((i - GEN_NAMES.length) + ": "
						+ df.format(((SuperGene) fittest.getGenes()[i]).doubleValue()));
			}

			System.out.println("==ExoTamarisk==");
			for (int i = GEN_NAMES.length + river.getNumReaches(); i < GEN_NAMES.length + 2 * river.getNumReaches(); ++i) {
				System.out.println((i - (GEN_NAMES.length + river.getNumReaches())) + ": "
						+ df.format(((SuperGene) fittest.getGenes()[i]).doubleValue()));
			}

			System.out.println("Best fitness after: " + evolution + " steps "
					+ mGenotype.getFittestChromosome().getFitnessValue());

			System.out.println("Took: " + (System.currentTimeMillis() - start) / 1000 / 60 + " Minutes");
			start = System.currentTimeMillis();
		}

		System.out.println("Total: " + (System.currentTimeMillis() - total) / 1000 / 60 + " Minutes");

		return new EnvModel(river, Arrays.copyOf(fittest.getGenes(), fittest.getGenes().length, SuperGene[].class));
	}

	public EnvModel getBestModel(final River river, final EnvModel trueModel) {
		int stateCounter = 0;
		for (List<RiverState> riverList : mStates) {
			stateCounter += riverList.size();
		}

		if (stateCounter < 3) {
			// we have no data return our prior
			return new EnvModel(river, false);
		}

		IChromosome fittest = null;
		double start = System.currentTimeMillis(), total = System.currentTimeMillis();
		for (int evolution = 0; evolution < mEvolutions; evolution++) {
			mGenotype.evolve();

			// Print summary
			fittest = mGenotype.getFittestChromosome();

			SuperGene[] genes = new SuperGene[fittest.getGenes().length];
			for (int i = 0; i < fittest.getGenes().length; ++i) {
				genes[i] = (SuperGene) fittest.getGene(i);
			}

			EnvModel model = new EnvModel(river, genes);
			trueModel.printComparison(model);

			System.out.println("Best fitness after: " + evolution + " steps "
					+ mGenotype.getFittestChromosome().getFitnessValue());

			System.out.println("Took: " + (System.currentTimeMillis() - start) / 1000 / 60 + " Minutes");
			start = System.currentTimeMillis();
		}

		System.out.println("Total: " + (System.currentTimeMillis() - total) / 1000 / 60 + " Minutes");

		return new EnvModel(river, Arrays.copyOf(fittest.getGenes(), fittest.getGenes().length, SuperGene[].class));
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
