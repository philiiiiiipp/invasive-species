package nl.uva.species.genetic;

import java.util.ArrayList;
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
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

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
		mEvolutions = 5000;
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

		int percentEvolution = mEvolutions / 100;
		for (int i = 0; i < mEvolutions; i++) {
			mGenotype.evolve();
			// Print progress.
			// ---------------
			if (percentEvolution > 0 && i % percentEvolution == 0) {

				IChromosome fittest = mGenotype.getFittestChromosome();
				double fitness = fittest.getFitnessValue();
				System.out.println(i + ": Currently fittest Chromosome has fitness " + fitness);
			}
		}
		// Print summary.
		// --------------
		IChromosome fittest = mGenotype.getFittestChromosome();

		int i = 1;
		for (Gene g : fittest.getGenes()) {
			System.out.println(i + ": " + ((DoubleGene) g).doubleValue());

			++i;
		}

		System.out.println("Fittest Chromosome has fitness " + fittest.getFitnessValue());

		return null;
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

	/**
	 * Starts the example.
	 * 
	 * @param args
	 *            if optional first argument provided, it represents the number of bits to use, but no more than 32
	 * 
	 * @author Neil Rotstan
	 * @author Klaus Meffert
	 * @since 2.0
	 */
	public static void main(final String[] args) {
		River river = new River(
				new TaskSpec(
						"VERSION RL-Glue-3.0 PROBLEMTYPE non-episodic DISCOUNTFACTOR 0.9 OBSERVATIONS INTS (28 1 3) ACTIONS INTS (7 1 4) REWARDS (-10000 -16.1) EXTRA [(0, 7), (1, 4), (2, 6), (3, 6), (4, 0), (5, 4), (6, 0)] BUDGET 100 by Majid Taleghan."));
		GeneticModelCreator g = new GeneticModelCreator(river);

		Observation o;

		o = new Observation();
		o.intArray = new int[] { 1, 2 };
		g.addRiverState(new RiverState(river, o));

		Action a;

		a = new Action();
		a.intArray = new int[] { 1, 2 };

		g.addAction(a);

		int numEvolutions = 50000;
		Configuration gaConf = new DefaultConfiguration();
		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(false);

		Genotype genotype = null;

		try {
			IChromosome sampleChromosome = new Chromosome(gaConf, new DoubleGene(gaConf, 0, 1), 10);
			gaConf.setSampleChromosome(sampleChromosome);
			gaConf.setPopulationSize(200);
			gaConf.setFitnessFunction(new MaxFunction());
			genotype = Genotype.randomInitialGenotype(gaConf);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		int percentEvolution = numEvolutions / 100;
		for (int i = 0; i < numEvolutions; i++) {
			genotype.evolve();
			// Print progress.
			// ---------------
			if (percentEvolution > 0 && i % percentEvolution == 0) {

				IChromosome fittest = genotype.getFittestChromosome();
				double fitness = fittest.getFitnessValue();
				System.out.println(i + ": Currently fittest Chromosome has fitness " + fitness);
			}
		}
		// Print summary.
		// --------------
		IChromosome fittest = genotype.getFittestChromosome();

		// int i = 1;
		// for (Gene g : fittest.getGenes()) {
		// System.out.println(i + ": " + ((DoubleGene) g).doubleValue());
		//
		// ++i;
		// }

		System.out.println("Fittest Chromosome has fitness " + fittest.getFitnessValue());
	}

	private boolean addAction(final Action a) {
		return mActions.add(a);
	}
}
