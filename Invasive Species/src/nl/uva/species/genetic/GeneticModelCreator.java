package nl.uva.species.genetic;

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
import org.rlcommunity.rlglue.codec.types.Action;

public class GeneticModelCreator {

    /** The past river states */
    private final List<List<RiverState>> mStates = new ArrayList<>();

    private final List<List<Action>> mActions = new ArrayList<>();

    private final int mEvolutions;

    /** The standard population size */
    public final int STANDARD_POP_SIZE = 20;

    /** Amount of evolutions */
    public final int STANDARD_EVOLUTIONS = 20;

    /** The currently calculated genotype */
    private Genotype mGenotype;

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

    /**
     * Reinitialise the genepool
     * 
     * @param river
     */
    public void reinitialise(final River river) {
        mGenotype = initialiseGenotype(STANDARD_POP_SIZE, river, EnvModel.Parameter.values().length
                + river.getNumReaches() * 2);
    }

    /**
     * Initialize a genotype with the given parameters
     * 
     * @param populationSize
     *            The size of the population
     * @return The generated genotype
     */
    private Genotype initialiseGenotype(final int populationSize, final River river,
            final int geneNumber) {

        if (mGenotype != null) {
            // We already calculated once, take the best chromosomes to the next round

            List<IChromosome> chromosomeList = mGenotype.getPopulation().getChromosomes();
            for (int i = 1; i < chromosomeList.size(); ++i) {
                Gene[] geneList = chromosomeList.get(i).getGenes();
                for (Gene gene : geneList) {
                    gene.setToRandomValue(null);
                }
            }

            return mGenotype;
        }

        Configuration gaConf = new DefaultConfiguration();
        gaConf.setPreservFittestIndividual(true);
        gaConf.setKeepPopulationSizeConstant(false);

        Genotype genotype = null;
        try {
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

    /**
     * Calculate the best fitting model
     * 
     * @param river
     *            The currently used river
     * @return The best fitting model
     */
    public EnvModel getBestModel(final River river) {
        return getBestModel(river, null);
    }

    /**
     * Calculate the best fitting model
     * 
     * @param river
     *            The currently used river
     * @param trueModel
     *            The true model if available, only for printout comparison
     * @return The best fitting model
     */
    public EnvModel getBestModel(final River river, final EnvModel trueModel) {
        int stateCounter = 0;
        for (List<RiverState> riverList : mStates) {
            stateCounter += riverList.size();
        }

        if (stateCounter < 3) {
            // we have no data return our prior

            // double[] superGenes = new double[EnvModel.Parameter.values().length
            // + river.getNumReaches() * 2];
            // for (int i = 0; i < superGenes.length; ++i) {
            // superGenes[i] = 0;
            // }

            return new EnvModel(river, false);
        }

        IChromosome fittest = null;
        double start = System.currentTimeMillis(), total = System.currentTimeMillis();
        EnvModel lastModel = null;
        int equalityCounter = 0;
        for (int evolution = 0; evolution < mEvolutions; evolution++) {
            mGenotype.evolve();

            // Print summary
            fittest = mGenotype.getFittestChromosome();

            SuperGene[] genes = new SuperGene[fittest.getGenes().length];
            for (int i = 0; i < fittest.getGenes().length; ++i) {
                genes[i] = (SuperGene) fittest.getGene(i);
            }

            EnvModel model = new EnvModel(river, genes);

            if (lastModel != null && model.compareTo(lastModel) == 0) {
                // models equal
                equalityCounter++;
                lastModel = model;

                if (equalityCounter == 2) {
                    // Multiple times the same model, stop calculating
                    System.out.println("Genetic model stops due to: No improvement");
                    break;
                }
            } else {
                equalityCounter = 0;
                lastModel = model;
            }

            if (trueModel != null) {
                trueModel.printComparison(model);
            } else {
                model.prettyPrint();
            }
            System.out.println("Best fitness after: " + evolution + " steps "
                    + mGenotype.getFittestChromosome().getFitnessValue());

            System.out.println("Took: " + (System.currentTimeMillis() - start) / 1000 / 60
                    + " Minutes");
            start = System.currentTimeMillis();
        }

        System.out.println("Total: " + (System.currentTimeMillis() - total) / 1000 / 60
                + " Minutes");

        return new EnvModel(river, Arrays.copyOf(fittest.getGenes(), fittest.getGenes().length,
                SuperGene[].class));
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
