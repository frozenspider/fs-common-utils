package org.fs.utils.collection;

import java.util.List;
import java.util.SortedSet;

import org.fs.utils.collection.set.IndexedSet;
import org.fs.utils.collection.set.IndexedSortedSet;

/**
 * Generalization of {@link SetList} and {@link IndexedSortedSet}. As such, in implements
 * {@link List}, {@link SortedSet} and {@link IndexedSet},
 *
 * @author FS
 * @param <E>
 *            the type of elements in this collection
 */
public interface SortedSetList<E> extends SetList <E>, IndexedSortedSet <E> {

	@Override
	public IndexedSortedSet <E> subSet(final E fromElement, final E toElement);

	@Override
	public IndexedSortedSet <E> headSet(final E toElement);

	@Override
	public IndexedSortedSet <E> tailSet(final E fromElement);
}

