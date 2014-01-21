package nl.uva.species.utils;

public class Pair<L, R> {

	private final L mLeft;
	private final R mRight;

	public Pair(final L left, final R right) {
		mLeft = left;
		mRight = right;
	}

	public L getLeft() {
		return mLeft;
	}

	public R getRight() {
		return mRight;
	}

}
