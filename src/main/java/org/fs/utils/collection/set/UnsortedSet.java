package org.fs.utils.collection.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.fs.utils.ObjectUtils;
import org.fs.utils.collection.SetList;

/**
 * This implementation of {@link IndexedSet} is based on {@link ArrayList}. In fact, this class just
 * allows {@code ArrayList} to be used as an {@code IndexedSet}.
 * <p>
 * Adding an existing element will have no effect.
 * <p>
 * This class should be used through {@code IndexedSet} or {@code Set} interface.
 * <p>
 * This class obeys the {@link Set#equals(Object) equals()} and {@link Set#hashCode() hashCode()}
 * contracts of a {@link Set} (i.e. it is not equal to {@link List}).
 *
 * @author FS
 * @param <E>
 */
public class UnsortedSet<E> extends ArrayList <E> implements SetList <E> {

	public static <K>UnsortedSet <K> make(final K... obj) {
		final UnsortedSet <K> result = new UnsortedSet <K>(Arrays.asList(obj));
		return result;
	}

	public static UnsortedSet <?> makeUntyped(final Object... obj) {
		final UnsortedSet <?> result = new UnsortedSet <Object>(Arrays.asList(obj));
		return result;
	}

	private static final long	serialVersionUID	= 5721861112062373719L;

	public UnsortedSet() {}

	public UnsortedSet(final Collection <? extends E> c) {
		super(c.size());
		addAll(c);
	}

	public UnsortedSet(final int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public E set(final int index, final E element) {
		final int idx = indexOf(element);
		if (idx != index && idx >= 0) throw new IllegalArgumentException("Cannot set an element to be duplicate");
		return super.set(index, element);
	}

	@Override
	public void add(final int index, final E element) {
		if (contains(element)) return;
		super.add(index, element);
	}

	/** Will change nothing if element already in set */
	@Override
	public boolean add(final E e) {
		if (contains(e)) return false;
		return super.add(e);
	}

	@Override
	public int addAndGetPos(final E object) {
		final int idx = indexOf(object);
		if (idx != -1) return idx;
		super.add(object);
		return size() - 1;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Set)) return false;
		final Set <?> cast = (Set <?>)o;
		if (size() != cast.size()) return false;
		for (final E element : this) {
			if (!cast.contains(element)) return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (final E element : this) {
			hash += ObjectUtils.hashCode(element);
		}
		return hash;
	}

	/** Reconstructs this set, removing all duplicate elements. */
	public void repair() {
		final ArrayList <E> copy = new ArrayList <E>(this);
		this.clear();
		this.addAll(copy);
	}

	@Override
	public boolean addAll(final Collection <? extends E> c) {
		// ArrayList implementation uses System.arraycopy()
		boolean change = false;
		for (final E element : c) {
			change = this.add(element) || change;
		}
		return change;
	}
}

