package org.fs.utils.structure.wrap;

import java.io.Serializable;

import org.fs.utils.ObjectUtils;

/**
 * Wrapper for a group of tree objects.
 *
 * @author FS
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class Triplet<A,B,C> implements Serializable {

	private static final long	serialVersionUID	= -8592119128969104431L;
	private A					first;
	private B					second;
	private C					third;

	public Triplet(final A first, final B second, final C third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	@Override
	public int hashCode() { // ABC + A + B**2 + C**3
		final int h1 = ObjectUtils.hashCode(getFirst());
		final int h2 = ObjectUtils.hashCode(getSecond());
		final int h3 = ObjectUtils.hashCode(getThird());
		return h1 + h2 + h3 //
				+ h1 //
				+ h2 * h2 //
				+ h3 * h3 * h3;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) return true;
		if (other instanceof Triplet <?, ?, ?>) {
			final Triplet <?, ?, ?> otherPair = (Triplet <?, ?, ?>)other;
			return ObjectUtils.equals(getFirst(), otherPair.getFirst()) //
					&& ObjectUtils.equals(getSecond(), otherPair.getSecond()) //
					&& ObjectUtils.equals(getThird(), otherPair.getThird());
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + getFirst() + ", " + getSecond() + ", " + getThird() + ")";
	}

	public A getFirst() {
		return first;
	}

	public void setFirst(final A first) {
		this.first = first;
	}

	public B getSecond() {
		return second;
	}

	public void setSecond(final B second) {
		this.second = second;
	}

	public C getThird() {
		return third;
	}

	public void setThird(final C third) {
		this.third = third;
	}
}

