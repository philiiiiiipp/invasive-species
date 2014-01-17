package nl.uva.species.genetic;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;
import org.jgap.impl.DoubleGene;

public class MaxFunction extends FitnessFunction {
	/** String containing the CVS revision. Read out via reflection! */
	private final static String CVS_REVISION = "$Revision: 1.6 $";

	/**
	 * This example implementation calculates the fitness value of Chromosomes using BooleanAllele implementations. It
	 * simply returns a fitness value equal to the numeric binary value of the bits. In other words, it optimizes the
	 * numeric value of the genes interpreted as bits. It should be noted that, for clarity, this function literally
	 * returns the binary value of the Chromosome's genes interpreted as bits. However, it would be better to return the
	 * value raised to a fixed power to exaggerate the difference between the higher values. For example, the difference
	 * between 254 and 255 is only about .04%, which isn't much incentive for the selector to choose 255 over 254.
	 * However, if you square the values, you then get 64516 and 65025, which is a difference of 0.8% -- twice as much
	 * and, therefore, twice the incentive to select the higher value.
	 * 
	 * @param a_subject
	 *            the Chromosome to be evaluated
	 * @return defect rate of our problem
	 * 
	 * @author Neil Rotstan
	 * @author Klaus Meffert
	 * @since 2.0
	 */
	@Override
	public double evaluate(final IChromosome a_subject) {
		int total = 0;

		for (int i = 0; i < a_subject.size(); i++) {
			DoubleGene value = (DoubleGene) a_subject.getGene(a_subject.size() - (i + 1));
			total += Math.log(value.doubleValue());
		}

		return Math.abs(total);
	}
}
