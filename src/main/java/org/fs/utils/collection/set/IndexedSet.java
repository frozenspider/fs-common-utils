package org.fs.utils.collection.set;

import java.util.Set;

/**
 * A sorted set, whose elements have numerical indices, making it somewhat like the list.
 * <p>
 * This interface, however, does not change the contract of {@link Set#equals(Object) equals()} nor
 * {@link Set#hashCode() hashCode()} methods - they still must obey general contract of a
 * {@link Set}.
 *
 * @author FS
 * @param <E>
 *            the type of elements in this set
 */
public interface IndexedSet<E> extends Set <E> {

	/**
	 * Puts an element in the set (if it is not already there) and returns an element index.
	 *
	 * @param e
	 *            an element to insert
	 * @return an index of an element
	 */
	public int addAndGetPos(E e);

	public E get(int idx);

	public E remove(int index);

	public int indexOf(Object o);
}

