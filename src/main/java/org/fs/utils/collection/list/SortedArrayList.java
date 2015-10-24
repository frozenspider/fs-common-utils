package org.fs.utils.collection.list;

import static org.fs.utils.ObjectUtils.*;

import java.lang.reflect.Array;
import java.util.*;

import org.fs.utils.collection.SetList;
import org.fs.utils.collection.SortedSetList;
import org.fs.utils.collection.set.IndexedSet;
import org.fs.utils.collection.set.IndexedSortedSet;
import org.fs.utils.collection.set.UnsortedSet;
import org.fs.utils.structure.NullComparator;

/**
 * This extension of {@link ArrayList} is a permanently sorted {@link Set} (it does not allow
 * duplicate values). Positional modification methods, such as {@link #set(int, Object)},
 * {@link #add(int, Object)} and {@link #addAll(int, Collection)} will throw an
 * {@code UnsupportedOperationException}.
 * <p>
 * Because of this, it cannot be targeted by many {@link Collections} methods, which works with
 * {@code List}, such as {@code sort}, {@code swap}, {@code fill}, {@code replaceAll},
 * {@code rotate}, {@code shuffle}, and, perhaps, some others - specifically, those, who relies upon
 * unimplemented methods.
 * <p>
 * How this list handles {@code null} values fully depends on a chosen comparator. By default,
 * {@code null} value will throw a {@code NullPointerException}, because default implementation of
 * {@link Collections#sort(List)} do so. If you wish, you may use a {@link NullComparator} for a
 * explicit {@code null} handling.
 * <p>
 * Objects passed to this list must all be comparable between each other via the provided
 * {@code Comparator}, or via implementing {@code Comparable} if no {@code Comparator} has been
 * provided. If they aren't, list will obviously malfunction. This implementation also supports
 * cases, when {@code a.compareTo(b) == 0} doesn't imply {@code a.equals(b)}. However, too much such
 * values will slowly negate performance gain and will cause it to eventually degrade to
 * {@link UnsortedSet}.
 * <p>
 * Just as {@code ArrayList} is, this class is not thread-safe. For the purposes of multithread
 * modification, explicit synchronization, such as {@link Collections#synchronizedSet(Set)}, is
 * required.
 * <p>
 * <blockquote><b>ATTENTION:</b> This class implements non-standard {@code equals} and
 * {@code hashCode} methods. It is mutually equal with any same {@link Set} or {@link List} and it
 * delivers it's {@code hashCode} from {@link Set} (as such, it volatiles {@link List} contract).
 * Use it with care.</blockquote>
 *
 * @param <E>
 *            type parameter, comparable through implementing {@link Comparable} interface or
 *            through explicitly supplied {@link Comparator}
 * @author FS
 * @see List
 * @see SortedSetList
 * @see IndexedSortedSet
 * @see IndexedSet
 * @see UnsortedSet
 * @see ArrayList
 */
public class SortedArrayList<E> extends ArrayList <E> implements SortedSetList <E> {

	private static final long		serialVersionUID	= 6679141531100257413L;
	private Comparator <? super E>	cmp;

	//
	// Constructors
	//
	/**
	 * Constructs an empty sorted ArrayList with an initial capacity of ten and default comparator.
	 * <p>
	 * Does not allow {@code null}s.
	 *
	 * @see ArrayList#ArrayList()
	 */
	public SortedArrayList() {}

	/**
	 * Constructs an empty sorted ArrayList with the specified initial capacity and default
	 * comparator.
	 * <p>
	 * Does not allow {@code null}s.
	 *
	 * @param initialCapacity
	 *            the initial capacity of the list
	 * @exception IllegalArgumentException
	 *                if the specified initial capacity is negative
	 * @see ArrayList#ArrayList(int)
	 */
	public SortedArrayList(final int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs a sorted ArrayList and uses specified comparator for sorting.
	 *
	 * @param cmp
	 *            a {@link Comparator}
	 * @see #SortedArrayList()
	 */
	public SortedArrayList(final Comparator <? super E> cmp) {
		this.cmp = cmp;
	}

	/**
	 * Constructs an empty sorted ArrayList with specified initial capacity.
	 *
	 * @param initialCapacity
	 *            the initial capacity of the list
	 * @param cmp
	 *            a {@link Comparator}
	 * @see #SortedArrayList(int)
	 */
	public SortedArrayList(final int initialCapacity, final Comparator <? super E> cmp) {
		this(initialCapacity);
		this.cmp = cmp;
	}

	/**
	 * Constructs a sorted ArrayList with default comparator, containing the elements of the
	 * specified collection. If the collection itself is the SortedSet with <code>null</code>
	 * comparator, then no sorting is performed.
	 * <p>
	 * Does not allow {@code null}s.
	 *
	 * @param c
	 *            the collection whose elements are to be placed into this list
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public SortedArrayList(final Collection <? extends E> c) {
		super(c.size());
		if (c instanceof SortedSet && ((SortedSet <?>) c).comparator() == null) {
			this.addAllUnsorted(c);
		} else {
			this.addAll(c);
		}
	}

	/**
	 * Constructs an empty sorted ArrayList and specified initial capacity. If the collection itself
	 * is the SortedArrayList with equal comparator, then no sorting is performed.
	 * <p>
	 * Does not allow {@code null}s.
	 *
	 * @param c
	 *            the collection whose elements are to be placed into this list
	 * @param cmp
	 *            a {@link Comparator}
	 * @see #SortedArrayList(int)
	 */
	public SortedArrayList(final Collection <? extends E> c, final Comparator <? super E> cmp) {
		super(c.size());
		this.cmp = cmp;
		if (c instanceof SortedArrayList) {
			final Comparator <?> ccmp = ((SortedArrayList <?>) c).comparator();
			if (eq(cmp, ccmp)) {
				this.addAllUnsorted(c);
			} else {
				this.addAll(c);
			}
		} else {
			this.addAll(c);
		}
	}

	//
	// Methods
	//
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(final Object o) {
		try {
			return Collections.binarySearch(this, (E) o, cmp) >= 0;
		} catch(final ClassCastException ex) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public int indexOf(final Object o) {
		try {
			int idx;
			idx = Collections.binarySearch(this, (E) o, cmp);
			if (idx < 0) return -1;
			return idx;
		} catch(final ClassCastException ex) {
			return -1;
		}
	}

	/** Totally equals to {@link #indexOf(Object)}, as it may not contain duplicates */
	@Override
	public int lastIndexOf(final Object o) {
		return this.indexOf(o);
	}

	/** This implementation will throw {@link UnsupportedOperationException} */
	@Override
	public E set(final int index, final E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(final E element) {
		final int idx = this.addIfNotInSet(element);
		return idx < 0;
	}

	/**
	 * Pust an element in the set (if it is not already there) and returns an element index.
	 *
	 * @param element
	 * @return an index of an element if it was found (and thus no action were performed), or
	 *         {@code (-(insertion point) - 1)}, if an element wasn't found and was inserted at
	 *         {@code insertion point}.
	 */
	@SuppressWarnings("unchecked")
	public int addIfNotInSet(final E element) {
		if (element == null && cmp == null) throw new NullPointerException();
		int idx = Collections.binarySearch(this, element, cmp);
		if (idx >= 0) {
			final int sz = size();
			for (int i = idx; i < sz; ++i) {
				final E found = get(i);
				if (eq(found, element)) return i;
				if ((cmp == null ? ((Comparable <E>) found).compareTo(element) : cmp.compare(found,
						element)) != 0) {
					idx = -i - 1;
					break;
				}
			}
			if (idx >= 0) {
				idx = -sz - 1;
			}
		}
		super.add(-idx - 1, element); // idx = -insertion point - 1;
		return idx;
	}

	/** Same, as {@link #addIfNotInSet(Object)}, but will always return an actual index of element */
	@Override
	public int addAndGetPos(final E element) {
		final int idx = addIfNotInSet(element);
		return idx >= 0 ? idx : -idx - 1;
	}

	/** This implementation will throw {@link UnsupportedOperationException} */
	@Override
	public void add(final int index, final E element) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(final Object o) {
		try {
			final int idx = Collections.binarySearch(this, (E) o, cmp);
			if (idx < 0) return false;
			super.remove(idx);
			return true;
		} catch(final ClassCastException ex) {
			return false;
		}
	}

	@Override
	public boolean addAll(final Collection <? extends E> c) {
		final Iterator <? extends E> iter = c.iterator();
		if (!iter.hasNext()) return false;
		while (iter.hasNext()) {
			this.add(iter.next());
		}
		return true;
	}

	/** This implementation will throw {@link UnsupportedOperationException} */
	@Override
	public boolean addAll(final int index, final Collection <? extends E> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method will return sublist of this list. Sublist is considered an ordinaty List (not a
	 * Set), with a single exception: <b>sublist and it's iterators do not support {@code add()} and
	 * {@code set()} operation</b>
	 */
	@Override
	public SetList <E> subList(final int fromIndex, final int toIndex) {
		return new SortedArraySubList(fromIndex, toIndex);
	}

	@Override
	public SortedArrayList <E> clone() {
		return (SortedArrayList <E>) super.clone();
	}

	/**
	 * Indicates, if this object equals to another one. Can be equal to both {@link List} and
	 * {@link Set}, thus <b>violating the equals() contract by making it non-transitive</b>. Note,
	 * that {@code equals} is still symmetric, i.e. {@code a.equals(b)} means {@code b.equals(a)}
	 * for both {@code List} and {@code Set}.
	 */
	@Override
	public boolean equals(final Object o) {
		if (o == null) return false;
		if (o instanceof List) return super.equals(o);
		else if (o instanceof Set) {
			if (o == this) return true;
			final Set <?> cast = (Set <?>) o;
			if (size() != cast.size()) return false;
			final Iterator <?> iter = iterator();
			while (iter.hasNext()) {
				if (!cast.contains(iter.next())) return false;
			}
			return true;
		} else
			return false;
	}

	/**
	 * <b>Violates the {@link List#hashCode()} contract</b>, implementing {@link Set#hashCode()}
	 * instead.
	 */
	@Override
	public int hashCode() {
		int hashCode = 0;
		for (final E item : this) {
			hashCode += item == null ? 0 : item.hashCode();
		}
		return hashCode;
	}

	/**
	 * Truncates the array, leaving only range between {@code fromIndex} (inclusive) and
	 * {@code toIndex} (exclusive)
	 *
	 * @param fromIndex
	 *            index of first element to be removed
	 * @param toIndex
	 *            index after last element to be removed
	 */
	public void truncateToRange(final int fromIndex, final int toIndex) {
		final int size = size();
		if (fromIndex < 0 || toIndex > size || toIndex < fromIndex)
			throw new ArrayIndexOutOfBoundsException();
		// List back-end must be truncated before list front-end to prevent index shifting
		super.removeRange(toIndex, size);
		super.removeRange(0, fromIndex);
	}

	/**
	 * Removes from this list all of the elements whose index is between {@code fromIndex},
	 * inclusive, and {@code toIndex}, exclusive. Shifts any succeeding elements to the left
	 * (reduces their index). This call shortens the list by {@code (toIndex - fromIndex)} elements.
	 * (If {@code toIndex==fromIndex}, this operation has no effect.)
	 *
	 * @param fromIndex
	 *            index of first element to be removed
	 * @param toIndex
	 *            index after last element to be removed
	 * @see ArrayList#removeRange(int, int)
	 */
	public void truncateRange(final int fromIndex, final int toIndex) {
		if (fromIndex < 0 || toIndex > size() || toIndex < fromIndex)
			throw new ArrayIndexOutOfBoundsException();
		if (fromIndex != toIndex) {
			super.removeRange(fromIndex, toIndex);
		}
	}

	/**
	 * Changes the comparator being used and reorders elements in the list according to a new
	 * comparator. This operation is not much cheaper, then a new array allocation.<br/>
	 * <br/>
	 * <u>NOTE:</u> This method is not synchronized and is not thread-safe. If this method is
	 * invoked during list iteration or such, list's behavior is undefined.
	 *
	 * @param cmp
	 *            a {@link Comparator} or <code>null</code> for natural element order
	 */
	public void setComparator(final Comparator <? super E> cmp) {
		this.cmp = cmp;
		repair();
	}

	@Override
	public Comparator <? super E> comparator() {
		return cmp;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public IndexedSortedSet <E> subSet(final E fromElement, final E toElement) {
		if (cmp != null) {
			if (cmp.compare(fromElement, toElement) > 0) throw new IllegalArgumentException("fromElement > toElement");
		} else {
			if (((Comparable) fromElement).compareTo(toElement) > 0) throw new IllegalArgumentException("fromElement > toElement");
		}
		return new SortedArraySubSet(fromElement, toElement);
	}

	@Override
	public IndexedSortedSet <E> headSet(final E toElement) {
		return new SortedArraySubSet(null, toElement);
	}

	@Override
	public IndexedSortedSet <E> tailSet(final E fromElement) {
		return new SortedArraySubSet(fromElement, null);
	}

	@Override
	public E first() {
		if (isEmpty()) throw new NoSuchElementException();
		return get(0);
	}

	@Override
	public E last() {
		if (isEmpty()) throw new NoSuchElementException();
		return get(size() - 1);
	}

	/**
	 * Adds all of the given elements to the list. <br/>
	 * <br/>
	 * <b><u>ATTENTION:</u></b> Improper usage of this method may cause the whole list to
	 * malfunction. If this happens, use {@link #verify()} and {@link #repair()} methods.
	 *
	 * @param c
	 * @see #verify()
	 * @see #repair()
	 */
	protected void addAllUnsorted(final Collection <? extends E> c) {
		super.addAll(c);
	}

	/**
	 * Used to check, is list still sorted (valid) or not. List may become invalid, if
	 * {@link #addAllUnsorted(Collection)} was incorrectly called.
	 * <p>
	 * Will not work, if the type parameter {@code E} does not implements {@code Comparable <E>} and
	 * no comparator is specified.
	 *
	 * @return true, if list is valid, false otherwise
	 * @see #repair()
	 * @see #addAllUnsorted(Collection)
	 */
	protected boolean verify() {
		final List <E> shallowcopy = new ArrayList <E>(this);
		Collections.sort(shallowcopy, cmp);
		return shallowcopy.equals(this);
	}

	/**
	 * Re-sorts the list. This operation is not normally needed, but may be required after illegal
	 * {@link #addAllUnsorted(Collection)} call.
	 * <p>
	 * Will not work, if the type parameter {@code E} does not implements {@code Comparable <E>} and
	 * no comparator is specified.
	 *
	 * @see #verify()
	 * @see #addAllUnsorted(Collection)
	 */
	protected void repair() {
		final ArrayList <E> shallowcopy = new ArrayList <E>(this);
		Collections.sort(shallowcopy, cmp);
		this.clear();
		this.ensureCapacity(shallowcopy.size());
		this.addAllUnsorted(shallowcopy);
	}

	/**
	 * Returns a list iterator of the elements in this list (in proper sequence), starting at the
	 * specified position in this list. The specified index indicates the first element that would
	 * be returned by an initial call to {@code next}. An initial call to {@code previous()} would
	 * return the element with the specified index minus one.<br/>
	 * <p>
	 * Internal implementation of {@link ListIterator} does not allow
	 * {@link ListIterator#set(Object)} call.
	 *
	 * @see AbstractList#listIterator(int)
	 */
	@Override
	public ListIterator <E> listIterator(final int index) {
		final ListIterator <E> iter = super.listIterator(index);
		return new ReadAndRemoveListItr(iter);
	}

	public class SortedArraySubSet implements IndexedSortedSet <E>, RandomAccess {

		private final E	minLimit;
		private final E	maxLimit;

		public SortedArraySubSet(final E minLimit, final E maxLimit) {
			this.minLimit = minLimit;
			this.maxLimit = maxLimit;
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		public boolean isOutOfBound(final Object o) {
			try {
				if (minLimit != null) {
					if (cmp != null) {
						if (cmp.compare(minLimit, (E) o) > 0) return true;
					} else {
						if (((Comparable) minLimit).compareTo(o) > 0) return true;
					}
				}
				if (maxLimit != null) {
					if (cmp != null) {
						if (cmp.compare(minLimit, (E) o) > 0) return true;
					} else {
						if (((Comparable) maxLimit).compareTo(o) <= 0) return true;
					}
				}
			} catch(final ClassCastException ex) {
				return true;
			}
			return false;
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		public boolean isOutOfBoundExclusive(final Object o) {
			try {
				if (minLimit != null) {
					if (cmp != null) {
						if (cmp.compare(minLimit, (E) o) > 0) return true;
					} else {
						if (((Comparable) minLimit).compareTo(o) > 0) return true;
					}
				}
				if (maxLimit != null) {
					if (cmp != null) {
						if (cmp.compare(minLimit, (E) o) >= 0) return true;
					} else {
						if (((Comparable) maxLimit).compareTo(o) < 0) return true;
					}
				}
			} catch(final ClassCastException ex) {
				return true;
			}
			return false;
		}

		public int getTrueFirstIdx() {
			if (minLimit == null) return 0;
			final int idx = Collections.binarySearch(SortedArrayList.this, minLimit, cmp);
			return idx < 0 ? -1 - idx : idx;
		}

		public int getTrueAfterLastIdx() {
			if (maxLimit == null) return SortedArrayList.this.size();
			final int idx = Collections.binarySearch(SortedArrayList.this, maxLimit, cmp);
			return idx < 0 ? -1 - idx : idx;
		}

		@Override
		public boolean contains(final Object o) {
			if (isOutOfBound(o)) return false;
			return SortedArrayList.this.contains(o);
		}

		@Override
		public int indexOf(final Object o) {
			if (isOutOfBound(o)) return -1;
			return SortedArrayList.this.indexOf(o) - getTrueFirstIdx();
		}

		@Override
		public boolean add(final E element) {
			if (isOutOfBound(element)) throw new IllegalArgumentException();
			return SortedArrayList.this.add(element);
		}

		@Override
		public int addAndGetPos(final E element) {
			if (isOutOfBound(element)) throw new IllegalArgumentException();
			return SortedArrayList.this.addAndGetPos(element) - getTrueFirstIdx();
		}

		@Override
		public boolean remove(final Object o) {
			if (isOutOfBound(o)) return false;
			return SortedArrayList.this.remove(o);
		}

		@Override
		public boolean addAll(final Collection <? extends E> c) {
			boolean atLeastOne = false;
			for (final E e : c) {
				atLeastOne = add(e) || atLeastOne;
			}
			return atLeastOne;
		}

		@Override
		public int size() {
			final int startIdx = getTrueFirstIdx();
			final int endIdx = getTrueAfterLastIdx();
			return endIdx - startIdx;
		}

		@Override
		public Object clone() {
			return SortedArrayList.this.subSet(minLimit, maxLimit);
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public Iterator <E> iterator() {
			final int startIdx = getTrueFirstIdx();
			final int endIdx = getTrueAfterLastIdx();
			if (startIdx == endIdx) return Collections.EMPTY_LIST.iterator();
			return SortedArrayList.this.subList(startIdx, endIdx).iterator();
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			for (final Object o : c)
				if (isOutOfBound(o)) return false;
			return SortedArrayList.this.containsAll(c);
		}

		@Override
		public Comparator <? super E> comparator() {
			return SortedArrayList.this.comparator();
		}

		/**
		 * Returns a view of the portion of the source set whose elements range from
		 * {@code fromElement}, inclusive, to {@code toElement}, exclusive. Resulting subset bounds
		 * cannot be wider, than this subset's bounds.
		 *
		 * @throws IllegalArgumentException
		 *             if key is out of bounds
		 */
		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public SortedSet <E> subSet(final E fromElement, final E toElement) {
			if (isOutOfBound(fromElement))
				throw new IllegalArgumentException("fromElement is out of bounds");
			if (isOutOfBoundExclusive(toElement))
				throw new IllegalArgumentException("toElement is out of bounds");
			if (cmp != null) {
				if (cmp.compare(fromElement, toElement) > 0) throw new IllegalArgumentException("fromElement > toElement");
			} else {
				if (((Comparable) fromElement).compareTo(toElement) > 0) throw new IllegalArgumentException("fromElement > toElement");
			}
			return SortedArrayList.this.subSet(fromElement, toElement);
		}

		/**
		 * Returns a view of the portion of the source set whose elements range from this subset's
		 * lower bound, inclusive, to {@code toElement}, exclusive. Resulting subset bounds cannot
		 * be wider, than this subset's bounds.
		 *
		 * @throws IllegalArgumentException
		 *             if key is out of bounds
		 */
		@Override
		public SortedSet <E> headSet(final E toElement) {
			if (isOutOfBoundExclusive(toElement))
				throw new IllegalArgumentException("toElement is out of bounds");
			return SortedArrayList.this.subSet(minLimit, toElement);
		}

		/**
		 * Returns a view of the portion of the source set whose elements range from
		 * {@code fromElement}, inclusive, to this subset's upper bound, exclusive. Resulting subset
		 * bounds cannot be wider, than this subset's bounds.
		 *
		 * @throws IllegalArgumentException
		 *             if key is out of bounds
		 */
		@Override
		public SortedSet <E> tailSet(final E fromElement) {
			if (isOutOfBound(fromElement))
				throw new IllegalArgumentException("fromElement is out of bounds");
			return SortedArrayList.this.subSet(fromElement, maxLimit);
		}

		@Override
		public E first() {
			if (isEmpty()) throw new NoSuchElementException();
			return get(0);
		}

		@Override
		public E last() {
			if (isEmpty()) throw new NoSuchElementException();
			return get(size() - 1);
		}

		@Override
		public Object[] toArray() {
			final int startIdx = getTrueFirstIdx();
			final int endIdx = getTrueAfterLastIdx();
			if (startIdx == endIdx) return new Object[0];
			return SortedArrayList.this.subList(getTrueFirstIdx(), getTrueAfterLastIdx()).toArray();
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			final int startIdx = getTrueFirstIdx();
			final int endIdx = getTrueAfterLastIdx();
			if (startIdx == endIdx) return (T[]) Array.newInstance(a.getClass(), 0);
			return SortedArrayList.this.subList(getTrueFirstIdx(), getTrueAfterLastIdx()).toArray(a);
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			final int startIdx = getTrueFirstIdx();
			final int endIdx = getTrueAfterLastIdx();
			if (startIdx == endIdx) return false;
			SortedArrayList.this.removeRange(startIdx, endIdx);
			return true;
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			final Iterator <E> iter = this.iterator();
			final boolean changed = false;
			while (iter.hasNext()) {
				if (!c.contains(iter.next())) {
					iter.remove();
				}
			}
			return changed;
		}

		@Override
		public E get(final int index) {
			if (index < 0 || index >= size()) throw new ArrayIndexOutOfBoundsException();
			return SortedArrayList.this.get(getTrueFirstIdx() + index);
		}

		@Override
		public E remove(final int index) {
			if (index < 0 || index >= size()) throw new ArrayIndexOutOfBoundsException();
			return SortedArrayList.this.remove(getTrueFirstIdx() + index);
		}

		@Override
		public void clear() {
			final int startIdx = getTrueFirstIdx();
			final int endIdx = getTrueAfterLastIdx();
			if (startIdx != endIdx) {
				SortedArrayList.this.removeRange(startIdx, endIdx);
			}
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			if (o instanceof List) {
				final int startIdx = getTrueFirstIdx();
				final int endIdx = getTrueAfterLastIdx();
				return SortedArrayList.this.subList(startIdx, endIdx).equals(o);
			} else if (o instanceof Set) {
				final Set <?> cast = (Set <?>) o;
				if (size() != cast.size()) return false;
				final Iterator <?> iter = iterator();
				while (iter.hasNext()) {
					if (!cast.contains(iter.next())) return false;
				}
				return true;
			} else
				return false;
		}

		@Override
		public int hashCode() {
			final int startIdx = getTrueFirstIdx();
			final int endIdx = getTrueAfterLastIdx();
			if (startIdx == endIdx) return 0;
			int hashCode = 0;
			final Iterator <E> i = iterator();
			while (i.hasNext()) {
				final E obj = i.next();
				if (obj != null) {
					hashCode += obj.hashCode();
				}
			}
			return hashCode;
		}

		@Override
		public String toString() {
			final int startIdx = getTrueFirstIdx();
			final int endIdx = getTrueAfterLastIdx();
			if (startIdx == endIdx) return Collections.EMPTY_LIST.toString();
			return SortedArrayList.this.subList(startIdx, endIdx).toString();
		}
	}

	public class SortedArraySubList implements SetList <E>, RandomAccess {

		private final List <E>	agent;
		private final int		start;
		private final int		end;

		public SortedArraySubList(final int start, final int end) {
			this.start = start;
			this.agent = SortedArrayList.super.subList(start, end);
			this.end = end;
		}

		@Override
		public int size() {
			return agent.size();
		}

		@Override
		public boolean isEmpty() {
			return agent.isEmpty();
		}

		@Override
		public boolean contains(final Object o) {
			return indexOf(o) >= 0;
		}

		@Override
		public int indexOf(final Object o) {
			final int idx = SortedArrayList.this.indexOf(o);
			if (idx < 0 || idx < start || idx >= end) return -1;
			return idx - start;
		}

		@Override
		public int lastIndexOf(final Object o) {
			return indexOf(o);
		}

		@Override
		public Iterator <E> iterator() {
			return agent.iterator();
		}

		@Override
		public Object[] toArray() {
			return agent.toArray();
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			return agent.toArray(a);
		}

		@Override
		public boolean add(final E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			return agent.remove(o);
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			return agent.containsAll(c);
		}

		@Override
		public boolean addAll(final Collection <? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection <? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			return agent.removeAll(c);
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			return agent.retainAll(c);
		}

		@Override
		public void clear() {
			agent.clear();
		}

		@Override
		public E get(final int index) {
			return agent.get(index);
		}

		@Override
		public E set(final int index, final E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int addAndGetPos(final E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E remove(final int index) {
			return agent.remove(index);
		}

		@Override
		public ListIterator <E> listIterator() {
			return new ReadAndRemoveListItr(agent.listIterator());
		}

		@Override
		public ListIterator <E> listIterator(final int index) {
			return new ReadAndRemoveListItr(agent.listIterator(index));
		}

		@Override
		public SetList <E> subList(final int fromIndex, final int toIndex) {
			if (fromIndex < 0 || toIndex > agent.size())
				throw new ArrayIndexOutOfBoundsException();
			return SortedArrayList.this.subList(start + fromIndex, start + toIndex);
		}

		@Override
		public boolean equals(final Object o) {
			return agent.equals(o);
		}

		@Override
		public int hashCode() {
			return agent.hashCode();
		}

		@Override
		public String toString() {
			return agent.toString();
		}
	}

	public class ReadAndRemoveListItr implements ListIterator <E> {

		ListIterator <E>	agent;

		public ReadAndRemoveListItr(final ListIterator <E> agent) {
			this.agent = agent;
		}

		@Override
		public boolean hasNext() {
			return agent.hasNext();
		}

		@Override
		public E next() {
			return agent.next();
		}

		@Override
		public boolean hasPrevious() {
			return agent.hasPrevious();
		}

		@Override
		public E previous() {
			return agent.previous();
		}

		@Override
		public int nextIndex() {
			return agent.nextIndex();
		}

		@Override
		public int previousIndex() {
			return agent.previousIndex();
		}

		@Override
		public void remove() {
			agent.remove();
		}

		/** This method will throw the {@link UnsupportedOperationException}. Please, don't call it. */
		@Override
		public void set(final E e) {
			throw new UnsupportedOperationException(
					"set(E) is not supported because of the list nature");
		}

		@Override
		public void add(final E e) {
			agent.add(e);
		}
	}
}
