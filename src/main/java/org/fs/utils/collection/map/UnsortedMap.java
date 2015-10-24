package org.fs.utils.collection.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Set;

import org.fs.utils.ObjectUtils;
import org.fs.utils.collection.set.IndexedSet;
import org.fs.utils.structure.wrap.Pair;

/**
 * This is the most straightforward unsorted {@link IndexedMap} implementation. It stores it's keys
 * and values in {@link ArrayList}, thus consuming little space, and preserves an order, in which
 * key-values are passed. The reverse side, however, is linear access time. The worst case is a
 * check for an absent element, which requires the map to check all of it's elements in order to
 * detect, that there is no such element.
 * <p>
 * Relatively low performance, however, should not be a problem for a small sized - around few
 * hundred of elements - maps (or medium sized not-so-frequently-accessed maps).
 * <p>
 * This class is also useful during development and debug, because it stores it's keys and values in
 * a primitive ArrayLists (namely, {@link #keys} and {@link #values} ), which can easily be
 * inspected by IDE.
 * <p>
 * Allows {@code null} values.
 * <p>
 * Additional public method: {@link #trimToSize()}.
 * 
 * @author FS
 * @param <K>
 *            key class
 * @param <V>
 *            value class
 */
public class UnsortedMap<K,V> implements IndexedMap <K, V>, RandomAccess, Serializable, Cloneable {
	
	private static final long	serialVersionUID	= 6521670958545595525L;
	
	/**
	 * Constructs a {@code UnsortedMap} from the supplied vararg array, which is interpreted as
	 * {@code key value key value ...} If array length is odd, last element is ignored.
	 * <p>
	 * Recommended application of this method is for debug and testing purposes.
	 * <p>
	 * This one is typed, but it's still rather dangerous because of unchecked casting (you won't
	 * get an exception right now, but a Double from Map &ltInteger, Integer&gt can make you a bit
	 * nervous).
	 * 
	 * @param keyValues
	 * @return brand new map
	 * @throws ClassCastException
	 *             if array element is of inappropriate type
	 */
	public static <K,V>UnsortedMap <K, V> make(final Object... keyValues) {
		return new UnsortedMap <K, V>(keyValues);
	}
	
	/**
	 * Constructs a {@code UnsortedMap} from the supplied vararg array, which is interpreted as
	 * {@code key value key value ...} If array length is odd, last element is ignored.
	 * <p>
	 * Recommended application of this method is for debug and testing purposes.
	 * <p>
	 * This one is untyped, so you'd better to know, what you're doing.
	 * 
	 * @param keyValues
	 * @return brand new map
	 */
	@SuppressWarnings("rawtypes")
	public static UnsortedMap <?, ?> makeUntyped(final Object... keyValues) {
		return new UnsortedMap(keyValues);
	}
	
	protected volatile transient UnsortedMapKeySet			_keySet;
	protected volatile transient UnsortedMapValueCollection	_valColl;
	protected volatile transient UnsortedMapEntrySet		_entrySet;
	//
	protected int											initialCapacity	= 10;
	/** Keys storage */
	protected ArrayList <K>									keys;
	/** Values storage */
	protected ArrayList <V>									values;
	
	/** D'oh, just make me a map! */
	public UnsortedMap() {}
	
	/**
	 * Constructs an {@link UnsortedMap} with a provided storage initial capacity
	 * 
	 * @param initialCapacity
	 *            initial capacity for a keys and values storage, default to 10
	 */
	public UnsortedMap(final int initialCapacity) {
		this.initialCapacity = initialCapacity;
	}
	
	/** A common copy-constructor. */
	@SuppressWarnings("javadoc")
	public UnsortedMap(final Map <? extends K, ? extends V> sourceMap) {
		this.initialCapacity = sourceMap.size();
		this.putAll(sourceMap);
	}
	
	/**
	 * Constructs an {@code UnsortedMap} from the supplied keys and values collection, associating
	 * key with a value in order returned by iterator. Collections must have equal size.
	 * 
	 * @param keysColl
	 *            collection of keys
	 * @param valuesColl
	 *            collection of values
	 * @throws IllegalArgumentException
	 *             if collections have different size
	 */
	public UnsortedMap(
			final Collection <? extends K> keysColl,
			final Collection <? extends V> valuesColl) {
		this.initialCapacity = keysColl.size();
		if (valuesColl.size() != initialCapacity)
			throw new IllegalArgumentException("Collections have different size");
		this.keys = new ArrayList <K>(initialCapacity);
		this.values = new ArrayList <V>(initialCapacity);
		final Iterator <? extends K> iter1 = keysColl.iterator();
		final Iterator <? extends V> iter2 = valuesColl.iterator();
		while (iter1.hasNext()) {
			keys.add(iter1.next());
			values.add(iter2.next());
		}
	}
	
	/**
	 * This constructor made non-public to follow the principle of least astonishment, which may be
	 * violated by the vararg Object constructor.
	 * <p>
	 * Use {@link #make(Object...)} or {@link #makeUntyped(Object...)} instead.
	 */
	@SuppressWarnings({"unchecked", "javadoc"})
	protected UnsortedMap(final Object... keyValues) {
		this.initialCapacity = keyValues.length / 2;
		K key;
		V value;
		for (int i = 0; i < keyValues.length - 1;) {
			key = (K) keyValues[i++];
			value = (V) keyValues[i++];
			this.put(key, value);
		}
	}
	
	@Override
	public int size() {
		return keys == null ? 0 : keys.size();
	}
	
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
	
	@Override
	public boolean containsKey(final Object key) {
		return keys == null ? false : keys.contains(key);
	}
	
	@Override
	public boolean containsValue(final Object value) {
		return keys == null ? false : values.contains(value);
	}
	
	@Override
	public V get(final Object key) {
		if (keys == null) return null;
		final int i = keys.indexOf(key);
		if (i < 0) return null;
		return values.get(i);
	}
	
	@Override
	public V put(final K key, final V value) {
		if (keys == null) {
			keys = new ArrayList <K>(initialCapacity);
			values = new ArrayList <V>(initialCapacity);
		}
		final int i = keys.indexOf(key);
		if (i < 0) {
			keys.add(key);
			values.add(value);
			return null;
		}
		return values.set(i, value);
	}
	
	@Override
	public V remove(final Object key) {
		if (keys == null) return null;
		final int i = keys.indexOf(key);
		if (i < 0) return null;
		keys.remove(i);
		return values.remove(i);
	}
	
	@Override
	public K getKeyAt(final int index) {
		return keys.get(index);
	}
	
	@Override
	public V getValueAt(final int index) {
		return values.get(index);
	}
	
	@Override
	public K setKeyAt(final int index, final K newKey) {
		return keys.set(index, newKey);
	}
	
	@Override
	public V setValueAt(final int index, final V newValue) {
		return values.set(index, newValue);
	}
	
	@Override
	public int indexOfKey(final Object key) {
		return keys.indexOf(key);
	}
	
	@Override
	public int indexOfValue(final Object value) {
		return values.indexOf(value);
	}
	
	@Override
	public Entry <K, V> removeAt(final int index) {
		if (keys == null) return null;
		return new Pair <K, V>(keys.remove(index), values.remove(index));
	}
	
	@Override
	public void putAll(final Map <? extends K, ? extends V> m) {
		for (final Entry <? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	public void clear() {
		keys = null;
		values = null;
	}
	
	@Override
	public IndexedSet <K> keySet() {
		if (_keySet == null) {
			_keySet = new UnsortedMapKeySet();
		}
		return _keySet;
	}
	
	@Override
	public Collection <V> values() {
		if (_valColl == null) {
			_valColl = new UnsortedMapValueCollection();
		}
		return _valColl;
	}
	
	@Override
	public Set <Entry <K, V>> entrySet() {
		if (_entrySet == null) {
			_entrySet = new UnsortedMapEntrySet();
		}
		return _entrySet;
	}
	
	/**
	 * Trims the capacity of this map to be the map's current size. An application can use this
	 * operation to minimize the storage of a map instance.
	 * 
	 * @see ArrayList#trimToSize()
	 */
	public void trimToSize() {
		if (keys != null) {
			keys.trimToSize();
			values.trimToSize();
		}
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		final Iterator <Entry <K, V>> i = entrySet().iterator();
		while (i.hasNext()) {
			final Entry <?, ?> next = i.next();
			hashCode += next.hashCode();
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Map <?, ?>)) return false;
		final Map <?, ?> cast = (Map <?, ?>) obj;
		if (size() != cast.size()) return false;
		return entrySet().equals(((Map <?, ?>) obj).entrySet());
	}
	
	@Override
	public Object clone() {
		try {
			final UnsortedMap <K, V> clone = (UnsortedMap <K, V>) super.clone();
			clone.keys = keys == null ? null : (ArrayList <K>) keys.clone();
			clone.values = values == null ? null : (ArrayList <V>) values.clone();
			clone._keySet = null;
			clone._valColl = null;
			clone._entrySet = null;
			return clone;
		} catch(final CloneNotSupportedException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		final int sz = size();
		for (int i = 0; i < sz; i++) {
			final K key = keys.get(i);
			// These conditions is needed to simualte basic Stack Overflow prevention,
			// that is provided e.g. by TreeMap
			if (key == this) {
				sb.append("(this Map)");
			} else if (key != null && key == _keySet) {
				sb.append("[(this Collection)]");
			} else {
				sb.append(key);
			}
			sb.append("=");
			final V value = values.get(i);
			if (value == this) {
				sb.append("(this Map)");
			} else if (value != null && value == _valColl) {
				sb.append("[(this Collection)]");
			} else {
				sb.append(value);
			}
			if (i < sz - 1) {
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
	// Backed collections implementation
	protected class UnsortedMapKeySet implements IndexedSet <K> {
		
		@Override
		public int size() {
			return UnsortedMap.this.size();
		}
		
		@Override
		public boolean isEmpty() {
			return UnsortedMap.this.isEmpty();
		}
		
		@Override
		public boolean contains(final Object o) {
			return UnsortedMap.this.containsKey(o);
		}
		
		@Override
		public Iterator <K> iterator() {
			return new UnsortedMapKeyIterator();
		}
		
		@Override
		public Object[] toArray() {
			return keys == null ? new Object[0] : keys.toArray();
		}
		
		@Override
		public <T>T[] toArray(final T[] a) {
			return keys == null ? Collections.emptyList().toArray(a) : keys.toArray(a);
		}
		
		@Override
		public int addAndGetPos(final K object) {
			throw new UnsupportedOperationException("addAndGetPos");
		}
		
		@Override
		public boolean add(final K e) {
			throw new UnsupportedOperationException("add");
		}
		
		@Override
		public boolean remove(final Object o) {
			return UnsortedMap.this.remove(o) != null;
		}
		
		@Override
		public boolean containsAll(final Collection <?> c) {
			if (keys == null) return c.isEmpty();
			return keys.containsAll(c);
		}
		
		@Override
		public boolean addAll(final Collection <? extends K> c) {
			throw new UnsupportedOperationException("add");
		}
		
		@Override
		public boolean retainAll(final Collection <?> c) {
			if (keys == null) return false;
			final Iterator <K> iter = this.iterator();
			boolean change = false;
			while (iter.hasNext()) {
				final K next = iter.next();
				if (!c.contains(next)) {
					iter.remove();
					change = true;
				}
			}
			return change;
		}
		
		@Override
		public boolean removeAll(final Collection <?> c) {
			if (keys == null) return false;
			final Iterator <K> iter = this.iterator();
			boolean change = false;
			while (iter.hasNext()) {
				final K next = iter.next();
				if (c.contains(next)) {
					iter.remove();
					change = true;
				}
			}
			return change;
		}
		
		@Override
		public void clear() {
			UnsortedMap.this.clear();
		}
		
		@Override
		public String toString() {
			if (keys == null) return "[]";
			return keys.toString();
		}
		
		@Override
		public int hashCode() {
			if (keys == null) return 0;
			int hashCode = 0;
			for (final Object obj : keys) {
				hashCode += ObjectUtils.hashCode(obj);
			}
			return hashCode;
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof Set)) return false;
			final Set <?> cast = (Set <?>) obj;
			if (keys == null) return cast.isEmpty();
			if (this.size() != cast.size()) return false;
			final Iterator <?> iter = iterator();
			while (iter.hasNext()) {
				if (!cast.contains(iter.next())) return false;
			}
			return true;
		}
		
		@Override
		public K get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException("idx < 0");
			if (index >= size()) throw new IndexOutOfBoundsException("idx >= size()");
			return keys.get(index);
		}
		
		@Override
		public K remove(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException("idx < 0");
			if (index >= size()) throw new IndexOutOfBoundsException("idx >= size()");
			return UnsortedMap.this.removeAt(index).getKey();
		}
		
		@Override
		public int indexOf(final Object o) {
			if (keys == null) return -1;
			return keys.indexOf(o);
		}
	}
	
	protected class UnsortedMapValueCollection implements Set <V> {
		
		@Override
		public int size() {
			return UnsortedMap.this.size();
		}
		
		@Override
		public boolean isEmpty() {
			return UnsortedMap.this.isEmpty();
		}
		
		@Override
		public boolean contains(final Object o) {
			return UnsortedMap.this.containsValue(o);
		}
		
		@Override
		public Iterator <V> iterator() {
			return new UnsortedMapValueIterator();
		}
		
		@Override
		public Object[] toArray() {
			return values == null ? new Object[0] : values.toArray();
		}
		
		@Override
		public <T>T[] toArray(final T[] a) {
			return values == null ? Collections.emptyList().toArray(a) : values.toArray(a);
		}
		
		@Override
		public boolean add(final V e) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean remove(final Object o) {
			if (values == null) return false;
			final int idx = values.indexOf(o);
			if (idx == -1) return false;
			UnsortedMap.this.removeAt(idx);
			return true;
		}
		
		@Override
		public boolean containsAll(final Collection <?> c) {
			return values == null ? c.isEmpty() : values.containsAll(c);
		}
		
		@Override
		public boolean addAll(final Collection <? extends V> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean removeAll(final Collection <?> c) {
			boolean change = false;
			for (final Object object : c) {
				change = remove(object) | change;
			}
			return change;
		}
		
		@Override
		public boolean retainAll(final Collection <?> c) {
			boolean change = false;
			final Iterator <V> iter = iterator();
			while (iter.hasNext()) {
				final V next = iter.next();
				if (!c.contains(next)) {
					change = true;
					iter.remove();
				}
			}
			return change;
		}
		
		@Override
		public void clear() {
			UnsortedMap.this.clear();
		}
		
		@Override
		public String toString() {
			if (keys == null) return "[]";
			return values.toString();
		}
		
		@Override
		public int hashCode() {
			if (keys == null) return 0;
			int hashCode = 0;
			for (final Object obj : values) {
				hashCode += ObjectUtils.hashCode(obj);
			}
			return hashCode;
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof Set)) return false;
			final Set <?> cast = (Set <?>) obj;
			if (keys == null) return cast.isEmpty();
			if (this.size() != cast.size()) return false;
			final Iterator <?> iter = iterator();
			while (iter.hasNext()) {
				if (!cast.contains(iter.next())) return false;
			}
			return true;
		}
	}
	
	protected class UnsortedMapEntrySet implements Set <Entry <K, V>> {
		
		@Override
		public boolean add(final Entry <K, V> e) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean addAll(final Collection <? extends Entry <K, V>> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public int size() {
			return UnsortedMap.this.size();
		}
		
		@Override
		public boolean isEmpty() {
			return UnsortedMap.this.isEmpty();
		}
		
		@Override
		public boolean contains(final Object o) {
			if (!(o instanceof Entry <?, ?>)) return false;
			if (keys == null) return false;
			final Entry <?, ?> cast = (java.util.Map.Entry <?, ?>) o;
			final int idx = keys.indexOf(cast.getKey());
			if (idx == -1) return false;
			return values.get(idx).equals(cast.getValue());
		}
		
		@Override
		public Iterator <Entry <K, V>> iterator() {
			return new UnsortedMapEntrySetIterator();
		}
		
		@Override
		public Object[] toArray() {
			if (keys == null) return new Object[0];
			final int size = UnsortedMap.this.size();
			final Object[] result = new Object[size];
			int iter = -1;
			for (int i = 0; i < size; i++) {
				result[++iter] = new Pair <K, V>(keys.get(i), values.get(i));
			}
			return result;
		}
		
		@Override
		public <T>T[] toArray(final T[] a) {
			final Object[] result = toArray();
			return (T[]) Arrays.copyOf(result, result.length, a.getClass());
		}
		
		@Override
		public boolean remove(final Object o) {
			if (!(o instanceof Entry)) return false;
			final Object key = ((Entry <?, ?>) o).getKey();
			return UnsortedMap.this.remove(key) != null;
		}
		
		@Override
		public boolean containsAll(final Collection <?> c) {
			final Iterator <?> iter = c.iterator();
			while (iter.hasNext()) {
				final Object next = iter.next();
				if (!(next instanceof Entry)) return false;
				final Object key = ((Entry <?, ?>) next).getKey();
				if (!UnsortedMap.this.containsKey(key)) return false;
			}
			return true;
		}
		
		@Override
		public boolean retainAll(final Collection <?> c) {
			boolean changed = false;
			int end = keys.size();
			for (int i = 0; keys != null && i < end;) {
				final K key = keys.get(i);
				if (c.contains(new Pair <K, V>(key, values.get(i)))) {
					++i;
				} else {
					changed = true;
					keys.remove(i);
					values.remove(i);
					--end;
					if (keys.isEmpty()) {
						keys = null;
						values = null;
					}
				}
			}
			return changed;
		}
		
		@Override
		public boolean removeAll(final Collection <?> c) {
			boolean changed = false;
			final Iterator <?> iter = c.iterator();
			while (iter.hasNext()) {
				final Object next = iter.next();
				if (!(next instanceof Entry)) {
					continue;
				}
				final Object key = ((Entry <?, ?>) next).getKey();
				if (UnsortedMap.this.remove(key) != null) {
					changed = true;
				}
			}
			return changed;
		}
		
		@Override
		public void clear() {
			UnsortedMap.this.clear();
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("[");
			boolean first = true;
			for (final Entry <K, V> entry : this) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(String.valueOf(entry));
			}
			sb.append(']');
			return sb.toString();
		}
		
		@Override
		public int hashCode() {
			if (keys == null) return 0;
			int hashCode = 0;
			for (final Object obj : values) {
				hashCode += ObjectUtils.hashCode(obj);
			}
			return hashCode;
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof Set)) return false;
			final Set <?> cast = (Set <?>) obj;
			if (keys == null) return cast.isEmpty();
			if (this.size() != cast.size()) return false;
			final Iterator <?> iter = iterator();
			while (iter.hasNext()) {
				final Object next = iter.next();
				if (!cast.contains(next)) return false;
			}
			return true;
		}
	}
	
	protected class UnsortedMapBackedEntry extends AbstractMapEntry <K, V> {
		
		protected final int	idx;
		protected final K	key;
		protected V			value;
		
		public UnsortedMapBackedEntry(final int idx, final K key, final V value) {
			this.idx = idx;
			this.key = key;
			this.value = value;
		}
		
		@Override
		public K getKey() {
			return key;
		}
		
		@Override
		public V getValue() {
			return value;
		}
		
		@Override
		public V setValue(final V value) {
			this.value = value;
			return values.set(idx, value);
		}
	}
	
	// Iterators
	protected class UnsortedMapKeyIterator implements Iterator <K> {
		
		protected int		cPos;
		protected boolean	hasBeenRemoved	= false;
		
		protected UnsortedMapKeyIterator() {
			cPos = -1;
		}
		
		@Override
		public boolean hasNext() {
			return keys == null ? false : cPos + 1 < keys.size();
		}
		
		@Override
		public K next() {
			if (!hasNext()) throw new NoSuchElementException();
			hasBeenRemoved = false;
			return keys.get(++cPos);
		}
		
		@Override
		public void remove() {
			if (hasBeenRemoved) throw new IllegalStateException();
			hasBeenRemoved = true;
			UnsortedMap.this.removeAt(cPos);
			--cPos;
		}
	}
	
	protected class UnsortedMapValueIterator implements Iterator <V> {
		
		protected int		cPos;
		protected boolean	hasBeenRemoved	= false;
		
		protected UnsortedMapValueIterator() {
			cPos = -1;
		}
		
		@Override
		public boolean hasNext() {
			return keys == null ? false : cPos + 1 < keys.size();
		}
		
		@Override
		public V next() {
			if (!hasNext()) throw new NoSuchElementException();
			hasBeenRemoved = false;
			return values.get(++cPos);
		}
		
		@Override
		public void remove() {
			if (hasBeenRemoved) throw new IllegalStateException();
			hasBeenRemoved = true;
			UnsortedMap.this.removeAt(cPos);
			--cPos;
		}
	}
	
	protected class UnsortedMapEntrySetIterator implements Iterator <Entry <K, V>> {
		
		protected int		cPos;
		protected boolean	hasBeenRemoved	= false;
		
		protected UnsortedMapEntrySetIterator() {
			cPos = -1;
		}
		
		@Override
		public boolean hasNext() {
			return keys == null ? false : cPos + 1 < keys.size();
		}
		
		@Override
		public Entry <K, V> next() {
			if (!hasNext()) throw new NoSuchElementException();
			hasBeenRemoved = false;
			++cPos;
			return new UnsortedMapBackedEntry(cPos, keys.get(cPos), values.get(cPos));
		}
		
		@Override
		public void remove() {
			if (hasBeenRemoved) throw new IllegalStateException();
			hasBeenRemoved = true;
			UnsortedMap.this.removeAt(cPos);
			--cPos;
		}
	}
}
