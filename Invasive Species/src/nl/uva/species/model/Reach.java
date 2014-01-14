package nl.uva.species.model;

import java.util.HashSet;
import java.util.Set;

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
	public Reach(final int index, final Reach parent, final int habitatsInvaded, final int habitatsNatural,
			final int habitatsEmpty) {
		mIndex = index;
		mParent = parent;
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
	public int getHabitatsNatural() {
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
