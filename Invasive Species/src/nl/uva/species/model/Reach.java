package nl.uva.species.model;

import java.util.HashSet;
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
    private final int mHabitatsInvaded;

    /** The amount of habitats containing natural plants in this reach */
    private final int mHabitatsNatural;

    /** The amount of empty habitats in this reach */
    private final int mHabitatsEmpty;

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
    public Reach(final int index, final Reach parent, final int[] habitats) {
        mIndex = index;
        mParent = parent;
        mHabitats = habitats;

        int habitatsInvaded = 0;
        int habitatsNatural = 0;
        int habitatsEmpty = 0;
        for (int i = 0; i < habitats.length; ++i) {
            switch (habitats[i]) {
            case Utilities.Tam:
                ++habitatsInvaded;
                break;
            case Utilities.Nat:
                ++habitatsNatural;
                break;
            case Utilities.Emp:
                ++habitatsEmpty;
                break;
            }
        }

        mHabitatsInvaded = habitatsInvaded;
        mHabitatsNatural = habitatsNatural;
        mHabitatsEmpty = habitatsEmpty;

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

    public int[] getHabitats() {
        return mHabitats;
    }

    /**
     * Retrieves the amount of habitats invaded by Tamarisk plants in this reach.
     * 
     * @return The amount of invaded habitats
     */
    public int getHabitatsInvaded() {
        return mHabitatsInvaded;
    }

    /**
     * Retrieves the amount of habitats containing natural plants in this reach.
     * 
     * @return The amount of natural habitats
     */
    public int getHabitatsNative() {
        return mHabitatsNatural;
    }

    /**
     * Retrieves the amount of empty habitats in this reach.
     * 
     * @return The amount of empty habitats
     */
    public int getHabitatsEmpty() {
        return mHabitatsEmpty;
    }

}
