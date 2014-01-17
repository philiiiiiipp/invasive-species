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
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.rlcommunity.rlglue.codec.types.Action;

public class GeneticModelCreator {

	/** The past river states */
	private final List<RiverState> mStates = new ArrayList<>();

	private final List<Action> mActions = new ArrayList<>();

	private final int mEvolutions;

	/** The standard population size */
	public final int STANDARD_POP_SIZE = 200;

	private final Genotype mGenotype;

	/**
	 * Create a genetic model creator with standard values and the given river
	 * 
	 * @param river
	 *            The river as the basis of the model
	 */
	public GeneticModelCreator(final River river) {
		mEvolutions = 100;
		int geneNumber = EnvModel.Parameter.values().length + river.getNumReaches() * 2;
		mGenotype = initialiseGenotype(STANDARD_POP_SIZE, river, geneNumber);
	}

	/**
	 * Initialise a genotype with the given parameters
	 * 
	 * @param populationSize
	 *            The size of the population
	 * @return The generated genotype
	 */
	private Genotype initialiseGenotype(final int populationSize, final River river, final int geneNumber) {
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

		for (int i = 0; i < mEvolutions; i++) {
			mGenotype.evolve();
		}
		// Print summary.
		// --------------
		IChromosome fittest = mGenotype.getFittestChromosome();

		int i = 1;
		DecimalFormat df = new DecimalFormat("#.####");
		for (Gene g : fittest.getGenes()) {
			System.out.println(i + ": " + df.format(((DoubleGene) g).doubleValue()));

			++i;
		}

		System.out.println("Fittest Chromosome has fitness " + fittest.getFitnessValue());
		return new EnvModel(river, Arrays.copyOf(fittest.getGenes(), fittest.getGenes().length, DoubleGene[].class));
	}

	/**
	 * Add the river state to the river state observation list
	 * 
	 * @param riverState
	 * @return true if the action was successful
	 */
	public boolean addRiverState(final RiverState riverState) {
		return mStates.add(riverState);
	}

	public boolean addAction(final Action a) {
		return mActions.add(a);
	}
}
