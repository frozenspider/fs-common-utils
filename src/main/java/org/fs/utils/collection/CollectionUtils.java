package org.fs.utils.collection;

import static org.fs.utils.ObjectUtils.*;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import org.fs.utils.ObjectUtils;
import org.fs.utils.collection.iter.AbstractIterator;
import org.fs.utils.collection.iter.KeyIterator;
import org.fs.utils.collection.list.SortedArrayList;
import org.fs.utils.collection.map.IndexedMap;
import org.fs.utils.collection.map.IndexedSortedMap;
import org.fs.utils.collection.set.IndexedSet;
import org.fs.utils.collection.set.IndexedSortedSet;
import org.fs.utils.structure.wrap.Pair;

public class CollectionUtils {
	
	/**
	 * Puts an element in the collection (if it is not already there) and returns the actual element
	 * contained in the collection.
	 * <p>
	 * If element was added, will return it's argument, otherwise will return equal element from the
	 * collection.
	 * 
	 * @param collection
	 *            where to search/add element
	 * @param element
	 *            target element
	 * @return an element contained in the collection
	 * @throws NullPointerException
	 *             if any argument is {@code null}
	 */
	public <E>E addAndGet(final Collection <E> collection, final E element) {
		ObjectUtils.requireNonNull(collection);
		ObjectUtils.requireNonNull(element);
		if (collection.contains(element)) {
			for (final E e : collection) {
				if (element.equals(e)) return e;
			}
		}
		collection.add(element);
		return element;
	}
	
	public static <T>IndexedSortedSet <T> asSortedSet(final T... values) {
		final IndexedSortedSet <T> result = new SortedArrayList <T>();
		result.addAll(Arrays.asList(values));
		return unmodifiableSortedSet(result);
	}
	
	/**
	 * Gets the element from map and returns it.
	 * <p>
	 * If it was {@code null}, return {@code defaultValue} instead.
	 * 
	 * @param map
	 *            where to get from
	 * @param key
	 *            map key
	 * @param defaultValue
	 *            a value to return if actual values was {@code null}
	 * @return element or {@code defaultValue}
	 * @throws NullPointerException
	 *             if map was {@code null}
	 */
	public static <K,V>V getUnboundByKey(final Map <K, V> map, final K key, final V defaultValue) {
		ObjectUtils.requireNonNull(map, "map");
		return valueOr(map.get(key), defaultValue);
	}
	
	/**
	 * Allows you to request element at any index without checking map size.
	 * <p>
	 * if idx >= size, it returns {@code null}.
	 * 
	 * @param map
	 *            where to get from
	 * @param idx
	 *            index
	 * @return element or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             if idx < 0
	 * @throws NullPointerException
	 *             if map was {@code null}
	 */
	public static <K,V>V getUnbound(final IndexedMap <K, V> map, final int idx) {
		return getUnbound(map, idx, null);
	}
	
	/**
	 * Allows you to request element at any index without checking map size.
	 * <p>
	 * if idx >= size, it returns {@code defaultValue}.
	 * 
	 * @param map
	 *            where to get from
	 * @param idx
	 *            index
	 * @param defaultValue
	 *            a value to return if index is too large
	 * @return element or {@code defaultValue}
	 * @throws IndexOutOfBoundsException
	 *             if idx < 0
	 * @throws NullPointerException
	 *             if map was {@code null}
	 */
	public static <K,V>V getUnbound(final IndexedMap <K, V> map, final int idx, final V defaultValue) {
		ObjectUtils.requireNonNull(map, "map");
		return idx < map.size() ? map.get(idx) : defaultValue;
	}
	
	/**
	 * Allows you to request element at any index without checking collection size.
	 * <p>
	 * if idx >= size, it simply returns {@code null}.
	 * 
	 * @param list
	 *            where to get from
	 * @param idx
	 *            index
	 * @return element or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             if idx < 0
	 * @throws NullPointerException
	 *             if list was {@code null}
	 */
	public static <E>E getUnbound(final List <E> list, final int idx) {
		return getUnbound(list, idx, null);
	}
	
	/**
	 * Allows you to request element at any index without checking collection size.
	 * <p>
	 * if idx >= size, it returns {@code defaultValue}.
	 * 
	 * @param list
	 *            where to get from
	 * @param idx
	 *            index
	 * @param defaultValue
	 *            a value to return if index is too large
	 * @return element or {@code defaultValue}
	 * @throws IndexOutOfBoundsException
	 *             if idx < 0
	 * @throws NullPointerException
	 *             if list was {@code null}
	 */
	public static <E>E getUnbound(final List <E> list, final int idx, final E defaultValue) {
		ObjectUtils.requireNonNull(list);
		return idx < list.size() ? list.get(idx) : defaultValue;
	}
	
	/**
	 * Allows you to request element at any index without checking collection size.
	 * <p>
	 * if idx >= size, it simply returns {@code null}.
	 * 
	 * @param set
	 *            where to get from
	 * @param idx
	 *            index
	 * @return element or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             if idx < 0
	 * @throws NullPointerException
	 *             if set was {@code null}
	 */
	public static <E>E getUnbound(final IndexedSet <E> set, final int idx) {
		return getUnbound(set, idx, null);
	}
	
	/**
	 * Allows you to request element at any index without checking collection size.
	 * <p>
	 * if idx >= size, it returns {@code defaultValue}.
	 * 
	 * @param set
	 *            where to get from
	 * @param idx
	 *            index
	 * @param defaultValue
	 *            a value to return if index is too large
	 * @return element or {@code defaultValue}
	 * @throws IndexOutOfBoundsException
	 *             if idx < 0
	 * @throws NullPointerException
	 *             if set was {@code null}
	 */
	public static <E>E getUnbound(final IndexedSet <E> set, final int idx, final E defaultValue) {
		ObjectUtils.requireNonNull(set);
		return idx < set.size() ? set.get(idx) : defaultValue;
	}
	
	/**
	 * Changes the value in list at the given position.
	 * <p>
	 * If idx >= size, list size is increased up to idx.
	 * 
	 * @param list
	 *            what to change
	 * @param idx
	 *            where to set
	 * @param newValue
	 *            what to set
	 * @return old element (if any), or {@code null}
	 */
	public static <E>E setUnbound(final List <E> list, final int idx, final E newValue) {
		ObjectUtils.requireNonNull(list);
		final int size = list.size();
		if (idx == size) {
			list.add(newValue);
			return null;
		} else if (idx < size) return list.set(idx, newValue);
		else {
			list.addAll(Collections.<E> nCopies(idx - size, null));
			list.add(newValue);
			return null;
		}
	}
	
	/**
	 * Removes all duplicate elements (e1 and e2, where
	 * {@code e1 == null ? e2 == null : e1.equals(e2)}. Removal is done using the iterator.
	 * <p>
	 * This implementation requires {@code hashCode()} of a collection elements.
	 * 
	 * @param c
	 */
	public static void removeDuplicate(final Collection <?> c) {
		final Iterator <?> iter = c.iterator();
		final Set <Object> set = new HashSet <Object>();
		while (iter.hasNext()) {
			final Object next = iter.next();
			if (!set.add(next)) {
				iter.remove();
			}
		}
	}
	
	/**
	 * Returns an index of element in the indexed set, with search starting at {@code from}
	 * (inclusive).
	 * 
	 * @param set
	 * @param o
	 * @param from
	 * @return index or -1 if not found.
	 */
	public static int indexOfFrom(final IndexedSet <?> set, final Object o, final int from) {
		final int size = set.size();
		if (from >= size) return -1;
		for (int i = from; i < size; ++i) {
			if (eq(o, set.get(i))) return i;
		}
		return -1;
	}
	
	/**
	 * Returns an index of element in the list, with search starting at {@code from} (inclusive).
	 * 
	 * @param list
	 * @param o
	 * @param from
	 * @return index or -1 if not found.
	 */
	public static int indexOfFrom(final List <?> list, final Object o, final int from) {
		final int size = list.size();
		if (list instanceof RandomAccess) {
			// Access directly
			if (from >= size) return -1;
			for (int i = from; i < size; ++i) {
				final Object element = list.get(i);
				if (eq(o, element)) return i;
			}
		} else {
			// Iterate
			final Iterator <?> iter = list.iterator();
			for (int i = 0; i < from; ++i) {
				iter.next();
			}
			for (int i = from; i < size; ++i) {
				final Object element = iter.next();
				if (eq(o, element)) return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns a last index of element in the list, with backward search starting at {@code from}
	 * (inclusive).
	 * 
	 * @param set
	 * @param o
	 * @param from
	 * @return index or -1 if not found.
	 */
	public static int lastIndexOfFrom(final IndexedSet <?> set, final Object o, final int from) {
		if (from < 0) return -1;
		for (int i = from; i >= 0; --i) {
			if (eq(o, set.get(i))) return i;
		}
		return -1;
	}
	
	/**
	 * Returns a last index of element in the list, with backward search starting at {@code from}
	 * (inclusive).
	 * 
	 * @param list
	 * @param o
	 * @param from
	 * @return index or -1 if not found.
	 */
	public static int lastIndexOfFrom(final List <?> list, final Object o, final int from) {
		if (from < 0) return -1;
		for (int i = from; i >= 0; --i) {
			if (eq(o, list.get(i))) return i;
		}
		return -1;
	}
	
	public static <K,V>K getKey(final Map <K, V> map, final V value) {
		if (!map.containsValue(value)) return null;
		for (final Entry <K, V> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) return entry.getKey();
		}
		return null;
	}
	
	public static <K>K getKey(
			final Map <K, String> map,
			final String value,
			final boolean ignoreCase) {
		for (final Entry <K, String> entry : map.entrySet()) {
			if (ignoreCase && value.equalsIgnoreCase(entry.getValue()) || !ignoreCase
					&& value.equals(entry.getValue())) return entry.getKey();
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static void swap(final IndexedMap map, final int p1, final int p2) {
		final Object key1 = map.getKeyAt(p1);
		final Object key2 = map.getKeyAt(p2);
		swap(map, key1, key2);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void swap(final Map map, final Object key1, final Object key2) {
		map.put(key2, map.put(key1, map.get(key2)));
	}
	
	public static boolean equalsOrdered(final IndexedSet <?> set1, final IndexedSet <?> set2) {
		ObjectUtils.requireNonNull(set1, "First argument is null");
		ObjectUtils.requireNonNull(set2, "Second argument is null");
		if (set1 == set2) return true;
		if (set1.size() != set2.size()) return false;
		final Iterator <?> iter1 = set1.iterator();
		final Iterator <?> iter2 = set2.iterator();
		while (iter1.hasNext()) {
			if (!iter2.hasNext()) return false;
			final Object o1 = iter1.next();
			final Object o2 = iter2.next();
			if (!eq(o1, o2)) return false;
		}
		if (iter2.hasNext()) return false;
		return true;
	}
	
	public static <T extends Comparable <T>>boolean equalsByContent(
			final Collection <T> c1,
			final Collection <T> c2) {
		if (c1.size() != c2.size()) return false;
		final List <T> c1List = new ArrayList <T>(c1);
		final List <T> c2List = new ArrayList <T>(c2);
		for (final T t : c1List) {
			final boolean removeResult = c2List.remove(t);
			if (!removeResult) return false;
		}
		assert c2List.isEmpty();
		return true;
	}
	
	public static <V,K>Pair <V, K> inversePair(final Pair <K, V> pair) {
		return Pair.make(pair.getSecond(), pair.getFirst());
	}
	
	/**
	 * Selects all entries by indices.
	 * 
	 * @param list
	 *            original collection
	 * @param indices
	 *            indices to select
	 * @return new list
	 */
	public static <T>List <T> getAll(List <T> list, Collection <Integer> indices) {
		List <T> result = new ArrayList <T>(indices.size());
		for (Integer idx : indices) {
			result.add(list.get(idx));
		}
		return result;
		
	}
	
	@SuppressWarnings("rawtypes")
	private static final IndexedSortedMap	EMPTY_MAP			= new EmptyIndexedSortedMap();
	@SuppressWarnings("rawtypes")
	private static final IndexedSortedSet	EMPTY_SORTED_SET	= new EmptySortedSet();
	@SuppressWarnings("rawtypes")
	private static final KeyIterator		EMPTY_ITERATOR		= new EmptyIterator();
	
	/**
	 * Returns the empty immutable serializable IndexedSortedMap singleton.
	 * 
	 * @return empty immutable serializable map
	 * @see Collections#emptyMap()
	 */
	@SuppressWarnings("unchecked")
	public static <K,V>IndexedSortedMap <K, V> emptyMap() {
		return EMPTY_MAP;
	}
	
	/**
	 * Returns the empty immutable serializable IndexedSortedSet singleton.
	 * 
	 * @return empty immutable serializable map
	 * @see Collections#emptySet()
	 */
	@SuppressWarnings("unchecked")
	public static <T>IndexedSortedSet <T> emptySet() {
		return EMPTY_SORTED_SET;
	}
	
	/**
	 * Returns the empty iterator singleton.
	 * 
	 * @return empty iterator
	 */
	@SuppressWarnings("unchecked")
	public static <K,T>KeyIterator <K, T> emptyIterator() {
		return EMPTY_ITERATOR;
	}
	
	@SuppressWarnings("unchecked")
	public static <T>IndexedSet <T> unmodifiableSet(final IndexedSet <? extends T> set) {
		if (set instanceof UnmodifiableIndexedSet) return (IndexedSet <T>) set;
		return new UnmodifiableIndexedSet <T>(set);
	}
	
	public static <T>IndexedSortedSet <T> unmodifiableSortedSet(final IndexedSortedSet <T> set) {
		if (set instanceof UnmodifiableIndexedSet) return set;
		return new UnmodifiableIndexedSortedSet <T>(set);
	}
	
	private static class EmptyIterator implements KeyIterator <Object, Object> {
		
		@Override
		public boolean hasNext() {
			return false;
		}
		
		@Override
		public Object next() {
			throw new NoSuchElementException();
		}
		
		@Override
		public void remove() {
			throw new IllegalStateException();
		}
		
		@Override
		public Object getKey() {
			throw new IllegalStateException();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static class EmptyIndexedSortedMap implements IndexedSortedMap <Object, Object>,
			Serializable {
		
		private static final long	serialVersionUID	= -480384306218428877L;
		
		@Override
		public Object getKeyAt(final int index) {
			throw new IndexOutOfBoundsException();
		}
		
		@Override
		public Object getValueAt(final int index) {
			throw new IndexOutOfBoundsException();
		}
		
		@Override
		public Object setKeyAt(final int index, final Object newKey) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Object setValueAt(final int index, final Object newValue) {
			throw new IndexOutOfBoundsException();
		}
		
		@Override
		public int indexOfKey(final Object key) {
			return -1;
		}
		
		@Override
		public int indexOfValue(final Object value) {
			return -1;
		}
		
		@Override
		public java.util.Map.Entry <Object, Object> removeAt(final int index) {
			throw new IndexOutOfBoundsException();
		}
		
		@Override
		public int size() {
			return 0;
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public boolean containsKey(final Object key) {
			return false;
		}
		
		@Override
		public boolean containsValue(final Object value) {
			return false;
		}
		
		@Override
		public Object get(final Object key) {
			return null;
		}
		
		@Override
		public IndexedSet <Object> keySet() {
			return EMPTY_SORTED_SET;
		}
		
		@Override
		public Collection <Object> values() {
			return Collections.<Object> emptySet();
		}
		
		@Override
		public Set <Map.Entry <Object, Object>> entrySet() {
			return Collections.emptySet();
		}
		
		@Override
		public Object put(final Object key, final Object value) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Object remove(final Object key) {
			return null;
		}
		
		@Override
		public void putAll(final Map <? extends Object, ? extends Object> m) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void clear() {}
		
		@Override
		public Comparator <? super Object> comparator() {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public SortedMap <Object, Object> subMap(final Object fromKey, final Object toKey) {
			if (!(fromKey instanceof Comparable <?>))
				throw new ClassCastException("fromKey not comparable");
			if (!(toKey instanceof Comparable <?>))
				throw new ClassCastException("toKey not comparable");
			if (((Comparable <Object>) fromKey).compareTo(toKey) > 0)
				throw new IllegalArgumentException("fromKey > toKey");
			return this;
		}
		
		@Override
		public SortedMap <Object, Object> headMap(final Object toKey) {
			if (!(toKey instanceof Comparable <?>)) throw new ClassCastException("not comparable");
			return this;
		}
		
		@Override
		public SortedMap <Object, Object> tailMap(final Object fromKey) {
			if (!(fromKey instanceof Comparable <?>))
				throw new ClassCastException("not comparable");
			return this;
		}
		
		@Override
		public Object firstKey() {
			throw new NoSuchElementException();
		}
		
		@Override
		public Object lastKey() {
			throw new NoSuchElementException();
		}
		
		@Override
		public boolean equals(final Object o) {
			return o instanceof Map && ((Map) o).size() == 0;
		}
		
		@Override
		public int hashCode() {
			return 0;
		}
		
		@Override
		public String toString() {
			return "{}";
		}
	}
	
	private static class EmptySortedSet extends AbstractSet <Object> implements
			IndexedSortedSet <Object>, Serializable {
		
		private static final long	serialVersionUID	= 9016858173784506776L;
		
		@Override
		public int size() {
			return 0;
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public boolean contains(final Object o) {
			return false;
		}
		
		@Override
		public Iterator <Object> iterator() {
			return EMPTY_ITERATOR;
		}
		
		@Override
		public Object[] toArray() {
			return new Object[0];
		}
		
		@Override
		public <T>T[] toArray(final T[] a) {
			return (T[]) Arrays.copyOf(new Object[0], 0, a.getClass());
		}
		
		@Override
		public boolean add(final Object e) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean remove(final Object o) {
			return false;
		}
		
		@Override
		public boolean containsAll(final Collection <?> c) {
			return c.size() == 0;
		}
		
		@Override
		public boolean addAll(final Collection <? extends Object> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean retainAll(final Collection <?> c) {
			return false;
		}
		
		@Override
		public boolean removeAll(final Collection <?> c) {
			return false;
		}
		
		@Override
		public void clear() {}
		
		@Override
		public Comparator <? super Object> comparator() {
			return null;
		}
		
		@Override
		public SortedSet <Object> subSet(final Object fromElement, final Object toElement) {
			return this;
		}
		
		@Override
		public SortedSet <Object> headSet(final Object toElement) {
			return this;
		}
		
		@Override
		public SortedSet <Object> tailSet(final Object fromElement) {
			return this;
		}
		
		@Override
		public Object first() {
			throw new NoSuchElementException();
		}
		
		@Override
		public Object last() {
			throw new NoSuchElementException();
		}
		
		@Override
		public int addAndGetPos(final Object object) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Object get(final int idx) {
			throw new IndexOutOfBoundsException();
		}
		
		@Override
		public Object remove(final int index) {
			throw new IndexOutOfBoundsException();
		}
		
		@Override
		public int indexOf(final Object o) {
			return -1;
		}
	}
	
	private static class UnmodifiableIterator <T> extends AbstractIterator <T> {
		
		private final Iterator <? extends T>	delegate;
		
		public UnmodifiableIterator(final Iterator <? extends T> iterator) {
			this.delegate = iterator;
		}
		
		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}
		
		@Override
		public T next() {
			return delegate.next();
		}
		
		@Override
		public boolean equals(final Object o) {
			return delegate.equals(o);
		}
		
		@Override
		public int hashCode() {
			return delegate.hashCode();
		}
		
		@Override
		public String toString() {
			return delegate.toString();
		}
	}
	
	private static class UnmodifiableIndexedSet <T> implements IndexedSet <T>, Serializable {
		
		private static final long				serialVersionUID	= 2322747999369164553L;
		private final IndexedSet <? extends T>	delegate;
		
		public UnmodifiableIndexedSet(final IndexedSet <? extends T> indexedSet) {
			this.delegate = indexedSet;
		}
		
		@Override
		public Iterator <T> iterator() {
			return new UnmodifiableIterator <T>(delegate.iterator());
		}
		
		@Override
		public int addAndGetPos(final T e) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean add(final T e) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T remove(final int index) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean addAll(final Collection <? extends T> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T get(final int index) {
			return delegate.get(index);
		}
		
		@Override
		public int indexOf(final Object o) {
			return delegate.indexOf(o);
		}
		
		@Override
		public int size() {
			return delegate.size();
		}
		
		@Override
		public boolean isEmpty() {
			return delegate.isEmpty();
		}
		
		@Override
		public boolean contains(final Object o) {
			return delegate.contains(o);
		}
		
		@Override
		public Object[] toArray() {
			return delegate.toArray();
		}
		
		@Override
		public <X>X[] toArray(final X[] a) {
			return delegate.toArray(a);
		}
		
		@Override
		public boolean containsAll(final Collection <?> c) {
			return delegate.containsAll(c);
		}
		
		@Override
		public boolean equals(final Object o) {
			return delegate.equals(o);
		}
		
		@Override
		public int hashCode() {
			return delegate.hashCode();
		}
		
		@Override
		public String toString() {
			return delegate.toString();
		}
	}
	
	private static class UnmodifiableIndexedSortedSet <T> implements IndexedSortedSet <T>,
			Serializable {
		
		private static final long			serialVersionUID	= 4131050042448540563L;
		private final IndexedSortedSet <T>	delegate;
		
		public UnmodifiableIndexedSortedSet(final IndexedSortedSet <T> indexedSet) {
			this.delegate = indexedSet;
		}
		
		@Override
		public Iterator <T> iterator() {
			return new UnmodifiableIterator <T>(delegate.iterator());
		}
		
		@Override
		public int addAndGetPos(final T e) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean add(final T e) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean addAll(final Collection <? extends T> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T remove(final int index) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T get(final int idx) {
			return this.delegate.get(idx);
		}
		
		@Override
		public int indexOf(final Object o) {
			return this.delegate.indexOf(o);
		}
		
		@Override
		public int size() {
			return this.delegate.size();
		}
		
		@Override
		public boolean isEmpty() {
			return this.delegate.isEmpty();
		}
		
		@Override
		public boolean contains(final Object o) {
			return this.delegate.contains(o);
		}
		
		@Override
		public Object[] toArray() {
			return this.delegate.toArray();
		}
		
		@Override
		public <X>X[] toArray(final X[] a) {
			return this.delegate.toArray(a);
		}
		
		@Override
		public T first() {
			return this.delegate.first();
		}
		
		@Override
		public T last() {
			return this.delegate.last();
		}
		
		@Override
		public boolean containsAll(final Collection <?> c) {
			return this.delegate.containsAll(c);
		}
		
		@Override
		public Comparator <? super T> comparator() {
			return this.delegate.comparator();
		}
		
		@Override
		public SortedSet <T> subSet(final T fromElement, final T toElement) {
			return Collections.unmodifiableSortedSet(delegate.subSet(fromElement, toElement));
		}
		
		@Override
		public SortedSet <T> headSet(final T toElement) {
			return Collections.unmodifiableSortedSet(delegate.headSet(toElement));
		}
		
		@Override
		public SortedSet <T> tailSet(final T fromElement) {
			return Collections.unmodifiableSortedSet(delegate.tailSet(fromElement));
		}
		
		@Override
		public boolean equals(final Object o) {
			return this.delegate.equals(o);
		}
		
		@Override
		public int hashCode() {
			return this.delegate.hashCode();
		}
		
		@Override
		public String toString() {
			return this.delegate.toString();
		}
	}
}
