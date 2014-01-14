package nl.uva.species.ui;

import org.jgrapht.graph.DefaultEdge;

public class RelationshipEdge<V> extends DefaultEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3109015200915779162L;

	private final V mParent;

	private final V mChild;

	private String label;

	public RelationshipEdge(final V v1, final V v2, final String label) {
		this.mParent = v1;
		this.mChild = v2;
		this.label = label;
	}

	public V getParent() {
		return mParent;
	}

	public V getChild() {
		return mChild;
	}

	/**
	 * Set the new label
	 * 
	 * @param label
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}