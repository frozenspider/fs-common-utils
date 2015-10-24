package org.fs.utils.structure;

import java.util.Comparator;

/**
 * This comparator will specifically tract a {@code null} values as a highest/lowest value possible.
 *
 * @author FS
 * @param <E>
 */
public class NullComparator<E extends Comparable <E>> implements Comparator <E> {

	protected final Comparator <? super E>	cmp;
	protected final boolean					nullIsGreatest;

	/**
	 * Treats {@code null}s as a infinitely big values.
	 *
	 * @param comparator
	 *            a {@link Comparator}, or {@code null} to use natural object ordering
	 * @param nullIsGreatest
	 *            {@code true} if null should be considered greatest possible value, {@code false}
	 *            if least
	 */
	public NullComparator(final Comparator <? super E> comparator, final boolean nullIsGreatest) {
		this.cmp = comparator;
		this.nullIsGreatest = nullIsGreatest;
	}

	@Override
	public int compare(final E o1, final E o2) {
		if (o1 == null) return o2 == null ? 0 : nullIsGreatest ? 1 : -1;
		if (o2 == null) return nullIsGreatest ? -1 : 1;
		if (cmp == null) return o1.compareTo(o2);
		return cmp.compare(o1, o2);
	}
}
