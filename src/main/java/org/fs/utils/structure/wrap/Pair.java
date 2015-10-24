package org.fs.utils.structure.wrap;

import java.io.Serializable;

import org.fs.utils.ObjectUtils;

/**
 * Wrapper for an object pair.
 *
 * @author FS
 * @param <A>
 * @param <B>
 */
public class Pair<A,B> implements Serializable, java.util.Map.Entry <A, B> {

	public static <A,B>Pair <A, B> make(final A first, final B second) {
		return new Pair <A, B>(first, second);
	}

	private static final long	serialVersionUID	= 3806348091434961432L;
	private A					first;
	private B					second;

	public Pair(final A first, final B second) {
		super();
		this.first = first;
		this.second = second;
	}

	public Pair(final java.util.Map.Entry <A, B> entry) {
		super();
		this.first = entry.getKey();
		this.second = entry.getValue();
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

	@Override
	public A getKey() {
		return first;
	}

	@Override
	public B getValue() {
		return second;
	}

	@Override
	public B setValue(final B value) {
		final B old = second;
		setSecond(value);
		return old;
	}

	@Override
	public int hashCode() { // AB + A + B**2
		final int h1 = ObjectUtils.hashCode(getFirst());
		final int h2 = ObjectUtils.hashCode(getSecond());
		return h1 + (h1 + h2) * h2;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) return true;
		if (!(other instanceof Pair <?, ?>)) return false;
		final Pair <?, ?> cast = (Pair <?, ?>)other;
		return ObjectUtils.equals(getFirst(), cast.getFirst()) && ObjectUtils.equals(getSecond(), cast.getSecond());
	}

	@Override
	public String toString() {
		return "(" + getFirst() + ", " + getSecond() + ")";
	}
}

