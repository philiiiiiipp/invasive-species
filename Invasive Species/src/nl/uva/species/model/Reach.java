package nl.uva.species.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uva.species.utils.Utilities;

/**
 * A reach as defined within a state.
 */
public class Reach {

    /** The reach's index within the experiment's edges */
    private final int mIndex;

    /** The parent reach or null if it's a root reach */
    private final Reach mParent;

    /** The children that flow into this reach */
    private final Set<Reach> mChildren = new HashSet<>();

    /** The habitats within this reach */
    private final int[] mHabitats;

    /** The amount of habitats invaded by Tamarisk plants in this reach */
    private final double mHabitatsInvaded;

    /** The amount of habitats containing natural plants in this reach */
    private final double mHabitatsNative;

    /** The amount of empty habitats in this reach */
    private final double mHabitatsEmpty;

    /** The amount of habitats within the reach */
    private final int mNumHabitats;

    /**
     * Prepares a reach with the specified properties.
     * 
     * @param index
     *            The reach's index within the experiment's edges
     * @param parent
     *            The parent reach or null if it's a root reach
     * @param habitats
     *            The contents of the habitats
     */
    public Reach(final int index, final Reach parent, final int[] habitats) {
        mIndex = index;
        mParent = parent;
        mHabitats = habitats;
        mNumHabitats = habitats.length;

        int habitatsInvaded = 0;
        int habitatsNative = 0;
        int habitatsEmpty = 0;
        for (int i = 0; i < habitats.length; ++i) {
            switch (habitats[i]) {
            case Utilities.HABITAT_INVADED:
                ++habitatsInvaded;
                break;
            case Utilities.HABITAT_NATIVE:
                ++habitatsNative;
                break;
            case Utilities.HABITAT_EMPTY:
                ++habitatsEmpty;
                break;
            }
        }

        mHabitatsInvaded = habitatsInvaded;
        mHabitatsNative = habitatsNative;
        mHabitatsEmpty = habitatsEmpty;

        // Inform the parent that it has a child
        if (mParent != null) {
            mParent.addChild(this);
        }
    }

    /**
     * Prepares a reach with the specified properties.
     * 
     * @param index
     *            The reach's index within the experiment's edges
     * @param parent
     *            The parent reach or null if it's a root reach
     * @param habitatsInvaded
     *            The amount of habitats invaded by Tamarisk plants in this reach
     * @param habitatsNatural
     *            The amount of habitats containing natural plants in this reach
     * @param habitatsEmpty
     *            The amount of empty habitats in this reach
     */
    public Reach(final int index, final Reach parent, final double habitatsInvaded, final double habitatsNative,
            final double habitatsEmpty) {
        mIndex = index;
        mParent = parent;
        mHabitats = null;

        mHabitatsInvaded = habitatsInvaded;
        mHabitatsNative = habitatsNative;
        mHabitatsEmpty = habitatsEmpty;

        mNumHabitats = (int) Math.round(habitatsInvaded + habitatsNative + habitatsEmpty);

        // Inform the parent that it has a child
        if (mParent != null) {
            mParent.addChild(this);
        }
    }

    /**
     * Retrieves the reach's index within the experiment's edges.
     * 
     * @return The reach's index
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * Retrieves the name of this reach
     * 
     * @return The reach's name
     */
    public String getName() {
        return "(        " + mIndex + "        )";
    }

    /**
     * Retrieves the parent reach or null if it's a root reach.
     * 
     * @return The reach's parent
     */
    public Reach getParent() {
        return mParent;
    }

    /**
     * Retrieves the children that flow into this reach.
     * 
     * @return The reach's children
     */
    public Set<Reach> getChildren() {
        return mChildren;
    }

    /**
     * Adds a child that has this reach as a parent to this reach.
     * 
     * @param reach
     *            The reach that has this reach as its parent
     */
    private void addChild(final Reach reach) {
        mChildren.add(reach);
    }

    /**
     * Retrieves the habitats within the reach.
     * 
     * @return The reach's habitats or null if this reach is an expectation
     */
    public int[] getHabitats() {
        return mHabitats;
    }

    /**
     * Retrieves the amount of habitats invaded by Tamarisk plants in this reach.
     * 
     * @return The amount of invaded habitats
     */
    public double getHabitatsInvaded() {
        return mHabitatsInvaded;
    }

    /**
     * Retrieves the amount of habitats containing natural plants in this reach.
     * 
     * @return The amount of natural habitats
     */
    public double getHabitatsNative() {
        return mHabitatsNative;
    }

    /**
     * Retrieves the amount of empty habitats in this reach.
     * 
     * @return The amount of empty habitats
     */
    public double getHabitatsEmpty() {
        return mHabitatsEmpty;
    }

    /**
     * Retrieves the amount of habitats in this reach.
     * 
     * @return The amount of habitats
     */
    public int getNumHabitats() {
        return mNumHabitats;
    }

    /**
     * Retrieves all valid actions on this reach.
     * 
     * @return A list of all valid actions at this reach
     */
    public List<Integer> getValidActions() {
        List<Integer> validActions = new ArrayList<>();
        validActions.add(Utilities.ACTION_NOTHING);

        if (getHabitatsInvaded() != 0) {
            // Can't eradicate a reach without Tamarisk plants
            validActions.add(Utilities.ACTION_ERADICATE);
            validActions.add(Utilities.ACTION_ERADICATE_RESTORE);
        }

        if (getHabitatsEmpty() != 0) {
            // Can't restore a reach without empty habitats
            validActions.add(Utilities.ACTION_RESTORE);
        }

        return validActions;
    }
}
