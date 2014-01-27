package nl.uva.species.genetic;

import nl.uva.species.utils.Utilities;

import org.jgap.BaseGene;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.UnsupportedRepresentationException;

public class SuperGene extends BaseGene implements Gene, java.io.Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 3348371688942532307L;
    private Double mGeneValue = new Double(0.5);

    /**
     * Constructs a new QuarterGene with no maximum number of quarters that can be represented
     * (other than Integer.MAX_VALUE, of course).
     */
    public SuperGene(final Configuration a_conf) throws InvalidConfigurationException {
        super(a_conf);
    }

    /**
     * Provides an implementation-independent means for creating new Gene instances. The new
     * instance that is created and returned should be setup with any implementation-dependent
     * configuration that this Gene instance is setup with (aside from the actual value, of course).
     * For example, if this Gene were setup with bounds on its value, then the Gene instance
     * returned from this method should also be setup with those same bounds. This is important, as
     * the JGAP core will invoke this method on each Gene in the sample Chromosome in order to
     * create each new Gene in the same respective gene position for a new Chromosome.
     * 
     * It should be noted that nothing is guaranteed about the actual value of the returned Gene and
     * it should therefore be considered to be undefined.
     * 
     * @return A new Gene instance of the same type and with the same setup as this concrete Gene.
     */
    @Override
    public SuperGene newGeneInternal() {
        try {
            // We construct the new QuarterGene with the same maximum number
            // of quarters that this Gene was constructed with.
            // -------------------------------------------------------------
            return new SuperGene(getConfiguration());
        } catch (InvalidConfigurationException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    /**
     * Sets the value of this Gene to the new given value. The actual type of the value is
     * implementation-dependent.
     * 
     * @param a_newValue
     *            the new value of this Gene instance.
     */
    @Override
    public void setAllele(final Object a_newValue) {
        mGeneValue = (Double) a_newValue;
    }

    /**
     * Retrieves the value represented by this Gene. The actual type of the value is
     * implementation-dependent.
     * 
     * @return the value of this Gene.
     */
    @Override
    public Object getAllele() {
        return mGeneValue;
    }

    /**
     * Sets the value of this Gene to a random legal value for the implementation. This method
     * exists for the benefit of mutation and other operations that simply desire to randomize the
     * value of a gene.
     * 
     * @param a_numberGenerator
     *            The random number generator that should be used to create any random values. It's
     *            important to use this generator to maintain the user's flexibility to configure
     *            the genetic engine to use the random number generator of their choice.
     */
    @Override
    public void setToRandomValue(final RandomGenerator a_numberGenerator) {
        mGeneValue = randomNumber(101);
    }

    /**
     * Retrieves a string representation of the value of this Gene instance that includes any
     * information required to reconstruct it at a later time, such as its value and internal state.
     * This string will be used to represent this Gene instance in XML persistence. This is an
     * optional method but, if not implemented, XML persistence and possibly other features will not
     * be available. An UnsupportedOperationException should be thrown if no implementation is
     * provided.
     * 
     * @return A string representation of this Gene's current state.
     * @throws UnsupportedOperationException
     *             to indicate that no implementation is provided for this method.
     */
    @Override
    public String getPersistentRepresentation() throws UnsupportedOperationException {
        // We want to represent both the maximum number of quarters that
        // can be represented by this Gene and its actual current value.
        // We'll separate the two with colons.
        // ---------------------------------------------------------------
        return mGeneValue + "";
    }

    /**
     * Sets the value and internal state of this Gene from the string representation returned by a
     * previous invocation of the getPersistentRepresentation() method. This is an optional method
     * but, if not implemented, XML persistence and possibly other features will not be available.
     * An UnsupportedOperationException should be thrown if no implementation is provided.
     * 
     * @param a_representation
     *            the string representation retrieved from a prior call to the
     *            getPersistentRepresentation() method.
     * 
     * @throws UnsupportedOperationException
     *             to indicate that no implementation is provided for this method.
     * @throws UnsupportedRepresentationException
     *             if this Gene implementation does not support the given string representation.
     */
    @Override
    public void setValueFromPersistentRepresentation(final String a_representation)
            throws UnsupportedOperationException, UnsupportedRepresentationException {
        // We're expecting to find the maximum number of quarters that this
        // Gene can represent, followed by a colon, followed by the actual
        // number of quarters currently represented.
        // -----------------------------------------------------------------
        // StringTokenizer tokenizer = new StringTokenizer(a_representation, TOKEN_SEPARATOR);
        // Make sure there are exactly two tokens.
        // ---------------------------------------
        // if (tokenizer.countTokens() != 2) {
        throw new UnsupportedRepresentationException(
                "Unknown representation format: Two tokens expected.");
        // }
        //
        // try {
        // // Parse the two tokens as integers.
        // // ---------------------------------
        // m_maxNumberOfQuarters = Integer.parseInt(tokenizer.nextToken());
        // m_numberOfQuarters = new Integer(tokenizer.nextToken());
        // } catch (NumberFormatException e) {
        // throw new
        // UnsupportedRepresentationException("Unknown representation format: Expecting integer values.");
        // }
    }

    /**
     * Executed by the genetic engine when this Gene instance is no longer needed and should perform
     * any necessary resource cleanup.
     */
    @Override
    public void cleanup() {
        // There's no cleanup necessary for this implementation.
        // -----------------------------------------------------
    }

    /**
     * Compares this Gene with the specified object for order. Returns a negative integer, zero, or
     * a positive integer as this object is less than, equal to, or greater than the specified
     * object. The given object must be a QuarterGene.
     * 
     * @param a_otherQuarterGene
     *            the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal
     *         to, or greater than the specified object.
     * 
     * @throws ClassCastException
     *             if the specified object's type prevents it from being compared to this Object.
     */
    @Override
    public int compareTo(final Object a_otherQuarterGene) {
        // If the other allele is null, we're bigger.
        // ------------------------------------------
        if (a_otherQuarterGene == null) {
            return 1;
        }

        return mGeneValue.compareTo(((SuperGene) a_otherQuarterGene).mGeneValue);
    }

    /**
     * Determines if this QuarterGene is equal to the given QuarterGene.
     * 
     * @return true if this QuarterGene is equal to the given QuarterGene, false otherwise.
     */
    @Override
    public boolean equals(final Object otherQuarterGene) {
        return otherQuarterGene instanceof SuperGene && compareTo(otherQuarterGene) == 0;
    }

    public double doubleValue() {
        return mGeneValue;
    }

    /**
     * Calculates the hash-code for this QuarterGene.
     * 
     * @return the hash-code of this QuarterGene
     */
    @Override
    public int hashCode() {
        return (int) (mGeneValue * 100);
    }

    @Override
    public Object getInternalValue() {
        return mGeneValue;
    }

    private double randomNumber(final int max) {
        return ((double) Utilities.RNG.nextInt(max)) / 100;
    }

    @Override
    public void applyMutation(final int a_index, final double a_percentage) {
        int maxMutation = (int) (101 * Math.abs(a_percentage));

        if (maxMutation == 0) return;

        double mutation = randomNumber(maxMutation);

        if (a_percentage > 0) {
            mGeneValue += mutation;
            if (mGeneValue > 1) mGeneValue = new Double(1);

        } else {
            mGeneValue -= mutation;
            if (mGeneValue < 0) mGeneValue = new Double(0);
        }

        setAllele(mGeneValue);
    }
}