package org.fs.utils.collection.map;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

import org.fs.utils.ListUtils;
import org.fs.utils.ObjectUtils;
import org.fs.utils.collection.list.SortedArrayList;
import org.fs.utils.collection.set.IndexedSet;
import org.fs.utils.structure.wrap.Pair;

/**
 * Rather straightforward {@link SortedMap} implementation. Relies upon {@link SortedArrayList} for
 * key sorting/searching/storing and uses {@link ArrayList} for value storing. Entries are
 * retrieved, inserted and removed using binary search algorithm, that executes in {@code O(logN)}.
 * Thus, it is slower than {@link java.util.HashMap}, which operates roughly in {@code O(1)}, but
 * consumes lesser space and does not relies upon {@link Object#hashCode()}. As a side bonus, it's
 * content is easy to inspect during debug phase.
 * <p>
 * The key and value lists are lazily created upon first insertion and are {@code null}ified upon
 * emptying, so empty map consumes nearly no memory.
 * <p>
 * This class also contains the followig public methods, that are not inherited from
 * {@code SortedMap}: {@link #offer(Comparable, Object)} and {@link #trimToSize()}
 * <p>
 * Iterators, delivered from this class, are fail-fast and will most likely throw a
 * {@code ConcurrentModificationException} if map were structurally changed during the iteration.
 * But the warning must be given once again - such behavior is not guaranteed and should not be
 * relied upon; it's only use is a bug catching during debug phase.
 * <p>
 * Does not supports {@code null} keys.
 *
 * @author FS
 * @param <K>
 *            the key class, must implement {@link Comparable} {@code <K>}
 * @param <V>
 *            the value class
 */
public class BasicSortedMap<K extends Comparable <K>,V> implements IndexedSortedMap <K, V>,
		RandomAccess, Serializable, Cloneable {

	/**
	 * Constructs a {@code BasicSortedMap} from the supplied vararg array, which is interpreted as
	 * {@code key value key value ...} If array length is odd, last element is ignored.
	 * <p>
	 * Recommended application of this method is for debug and testing purposes.
	 * <p>
	 * This one is typed, but it's still rather dangerous because of unchecked casting.
	 *
	 * @param keyValues
	 * @return newly constructed map
	 * @throws ClassCastException
	 *             if array element is of inappropriate type
	 * @see #makeUntyped(Object...)
	 */
	public static <K extends Comparable <K>,V>BasicSortedMap <K, V> make(final Object... keyValues) {
		return new BasicSortedMap <K, V>(keyValues);
	}

	/**
	 * Constructs a {@code BasicSortedMap} from the supplied vararg array, which is interpreted as
	 * {@code key value key value ...} If array length is odd, last element is ignored.
	 * <p>
	 * Recommended application of this method is for debug and testing purposes.
	 * <p>
	 * This one is untyped, so you'd better to know, what you're doing.
	 *
	 * @param keyValues
	 * @return newly constructed map
	 * @throws ClassCastException
	 *             if array element is of inappropriate type
	 * @see #make(Object...)
	 */
	@SuppressWarnings("rawtypes")
	public static BasicSortedMap <?, ?> makeUntyped(final Object... keyValues) {
		return new BasicSortedMap(keyValues);
	}

	protected static final long						serialVersionUID	= -2183223994948061503L;
	/** Initial capacity of key and value lists */
	protected int									initialCapacity		= 10;
	/**
	 * The number of times this map's key set has been structurally modified. Used by the iterators
	 * to detect unexpected changes.
	 *
	 * @see AbstractList#modCount
	 */
	protected transient int							keyModCount			= 0;
	/**
	 * The number of times this map's values has been structurally modified. Used by the iterators
	 * to detect unexpected changes.
	 *
	 * @see AbstractList#modCount
	 */
	protected transient int							valModCount			= 0;
	// Backed collections
	protected volatile transient KeySet				_keySet;
	protected volatile transient ValueCollection	_valColl;
	protected volatile transient EntrySet			_entrySet;
	protected BasicSortedMap <K, V>					parent;
	// Submap key limits
	/** low endpoint (inclusive) or {@code null} */
	protected K										minLimit;
	/** high endpoint (exclusive) or {@code null} */
	protected K										maxLimit;
	// Data storage
	protected SortedArrayList <K>					keyList;
	protected ArrayList <V>							valueList;

	/**
	 * Constructs an empty {@code BasicSortedMap} with key and value list capacity of 10.
	 * <p>
	 * Note, that both lists will be allocated upon first data insertion, and their capacity will be
	 * extended upon limit reaching.
	 */
	public BasicSortedMap() {}

	/**
	 * Constructs an empty {@code BasicSortedMap} with the given starting key and value list
	 * capacity.
	 * <p>
	 * Note, that both lists will be allocated upon first data insertion, and their capacity will be
	 * extended upon limit reaching.
	 * <p>
	 * This constructor, however, will help improve perfomance, if map size overhead is known.
	 *
	 * @param initialCapacity
	 *            initial (expected) map size
	 */
	public BasicSortedMap(final int initialCapacity) {
		this.initialCapacity = initialCapacity;
	}

	/**
	 * Constructs a {@code BasicSortedMap}, containing all elements, that supplied {@link Map}
	 * instance contains.
	 *
	 * @param m
	 */
	public BasicSortedMap(final Map <? extends K, ? extends V> m) {
		this.initialCapacity = m.size();
		this.putAll(m);
	}

	/**
	 * This constructor made non-public to follow the principle of least astonishment, which may be
	 * violated by the vararg Object constructor.
	 * <p>
	 * Use {@link #make(Object...)} or {@link #makeUntyped(Object...)} instead.
	 *
	 * @param keyValues
	 *            key1 value1 key2 value2 ...
	 */
	@SuppressWarnings("unchecked")
	protected BasicSortedMap(final Object... keyValues) {
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
		if (keyList == null) return 0;
		int size = keyList.size();
		if (maxLimit != null) {
			final int idx = Collections.binarySearch(keyList, maxLimit);
			size = idx < 0 ? -1 - idx : idx;
		}
		if (minLimit != null) {
			final int idx = Collections.binarySearch(keyList, minLimit);
			size -= idx < 0 ? -1 - idx : idx;
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsKey(final Object key) {
		if (key == null) throw new NullPointerException("Key cannot be null");
		if (keyList == null) return false;
		if (!(key instanceof Comparable <?>)) return false;
		try {
			if (isOutOfBounds((K) key)) return false;
			return keyList.contains(key);
		} catch(final ClassCastException ex) {
			return false;
		}
	}

	@Override
	public boolean containsValue(final Object value) {
		if (valueList == null) return false;
		// if (!(value instanceof Comparable <?>)) return false;
		final int idx = valueList.indexOf(value);
		if (idx < 0) return false;
		if (isOutOfBounds(keyList.get(idx))) return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(final Object key) {
		if (key == null) throw new NullPointerException("Key cannot be null");
		if (keyList == null) return null;
		try {
			if (isOutOfBounds((K) key)) return null;
			final int idx = keyList.indexOf(key);
			if (idx < 0) return null;
			return valueList.get(idx);
		} catch(final ClassCastException ex) {
			return null;
		}
	}

	/** @see #offer(Comparable, Object) */
	@Override
	public V put(final K key, final V value) {
		if (key == null) throw new NullPointerException("Key cannot be null");
		if (isOutOfBounds(key)) throw new IllegalArgumentException(key.toString());
		if (keyList == null) {
			keyList = new SortedArrayList <K>(initialCapacity);
			keyList.add(key);
			valueList = new ArrayList <V>(initialCapacity);
			valueList.add(value);
			return null;
		}
		final int idx = keyList.addIfNotInSet(key);
		V oldValue;
		incValModCount(1);
		if (idx < 0) {
			incKeyModCount(1);
			oldValue = null;
			valueList.add(-1 - idx, value);
		} else {
			oldValue = valueList.get(idx);
			valueList.set(idx, value);
		}
		return oldValue;
	}

	/**
	 * Offers the {@code key-value} pair to the map. This operation will only succeed, if the map
	 * currently have no mapping for the given {@code key} and - in case of submap - {@code key} is
	 * within submap bounds.
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return {@code true}, if map has been changed through this operation, {@code false} otherwise
	 * @see #put(Comparable, Object)
	 */
	public boolean offer(final K key, final V value) {
		if (key == null) throw new NullPointerException("Key cannot be null");
		if (isOutOfBounds(key)) return false;
		if (keyList == null) {
			keyList = new SortedArrayList <K>(initialCapacity);
			keyList.add(key);
			valueList = new ArrayList <V>(initialCapacity);
			valueList.add(value);
			return false;
		}
		final int idx = keyList.addIfNotInSet(key);
		if (idx < 0) {
			valueList.add(-1 - idx, value);
			incKeyModCount(1);
			incValModCount(1);
			return true;
		}
		return false;
	}

	@Override
	public K getKeyAt(int index) {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
		if (minLimit == null) return keyList.get(index);
		final int minLimitIdx = prefIndexOfKey(minLimit);
		index += minLimitIdx;
		if (maxLimit == null) return keyList.get(index);
		return keyList.get(index);
	}

	@Override
	public V getValueAt(int index) {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
		if (minLimit == null) return valueList.get(index);
		final int minLimitIdx = prefIndexOfKey(minLimit);
		index += minLimitIdx;
		if (maxLimit == null) return valueList.get(index);
		return valueList.get(index);
	}

	@Override
	public K setKeyAt(final int index, final K newKey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V setValueAt(final int index, final V newValue) {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
		if (minLimit == null) return valueList.set(index, newValue);
		final int minLimitIdx = prefIndexOfKey(minLimit);
		return valueList.set(minLimitIdx + index, newValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int indexOfKey(final Object key) {
		if (key == null) throw new NullPointerException();
		if (!(key instanceof Comparable <?>)) return -1;
		try {
			if (isOutOfBounds((K) key)) return -1;
		} catch(final ClassCastException ex) {
			return -1;
		}
		if (keyList == null) return -1;
		final int index = keyList.indexOf(key);
		if (minLimit == null) return index;
		final int minLimitIdx = prefIndexOfKey(minLimit);
		if (maxLimit == null) return index - minLimitIdx;
		final int maxLimitIdx = prefIndexOfKey(maxLimit);
		if (index >= maxLimitIdx) return -1;
		return index - minLimitIdx;
	}

	@Override
	public int indexOfValue(final Object value) {
		if (valueList == null) return -1;
		final int index = valueList.indexOf(value);
		if (minLimit == null) return index;
		final int minLimitIdx = prefIndexOfKey(minLimit);
		if (maxLimit == null) return index - minLimitIdx;
		final int maxLimitIdx = prefIndexOfKey(maxLimit);
		if (index >= maxLimitIdx) return -1;
		return index - minLimitIdx;
	}

	@Override
	public Entry <K, V> removeAt(final int index) {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
		K oldKey;
		V oldValue;
		if (minLimit == null) {
			oldKey = keyList.remove(index);
			oldValue = valueList.remove(index);
		} else {
			final int minLimitIdx = prefIndexOfKey(minLimit);
			oldKey = keyList.remove(minLimitIdx + index);
			oldValue = valueList.remove(minLimitIdx + index);
		}
		return new Pair <K, V>(oldKey, oldValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(final Object key) {
		if (key == null) throw new NullPointerException("Key cannot be null");
		if (keyList == null) return null;
		try {
			if (isOutOfBounds((K) key)) return null;
			final int idx = keyList.indexOf(key);
			if (idx < 0) return null;
			keyList.remove(idx);
			incKeyModCount(1);
			incValModCount(1);
			final V oldValue = valueList.remove(idx);
			if (keyList.isEmpty()) {
				keyList = null;
				valueList = null;
			}
			return oldValue;
		} catch(final ClassCastException ex) {
			return null;
		}
	}

	@Override
	public void putAll(final Map <? extends K, ? extends V> m) {
		final Iterator <?> iter = m.entrySet().iterator();
		while (iter.hasNext()) {
			final Entry <? extends K, ? extends V> entry = (Entry <? extends K, ? extends V>) iter.next();
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		if (keyList == null) return;
		// From now on, keysList != null
		if (minLimit == null && maxLimit == null) {
			// Unlimited
			incKeyModCount(keyList.size());
			incValModCount(keyList.size());
			keyList = null;
			valueList = null;
		} else if (minLimit == null) {
			// Only maximum limit
			int idx = Collections.binarySearch(keyList, maxLimit);
			if (idx < 0)
			 {
				idx = -1 - idx; // (-1 - idx)
			}
			incKeyModCount(keyList.size() - idx);
			incValModCount(keyList.size() - idx);
			keyList.truncateToRange(idx, valueList.size());
			valueList = ListUtils.subList(valueList, idx, valueList.size());
		} else if (maxLimit == null) {
			// Only minimum limit
			int idx = Collections.binarySearch(keyList, minLimit);
			if (idx < 0)
			 {
				idx = -1 - idx; // (-1 - idx)
			}
			incKeyModCount(idx);
			incValModCount(idx);
			keyList.truncateToRange(0, idx);
			valueList = ListUtils.subList(valueList, 0, idx);
		} else {
			// Both limits
			int lowerIdx = Collections.binarySearch(keyList, minLimit);
			if (lowerIdx < 0)
			 {
				lowerIdx = -1 - lowerIdx; // (-1 - idx)
			}
			int upperIdx = Collections.binarySearch(keyList, maxLimit);
			if (upperIdx < 0)
			 {
				upperIdx = -1 - upperIdx; // (-1 - idx)
			}
			incKeyModCount(upperIdx - lowerIdx);
			incValModCount(upperIdx - lowerIdx);
			keyList.truncateRange(lowerIdx, upperIdx);
			final ArrayList <V> part1 = ListUtils.subList(valueList, 0, lowerIdx);
			final ArrayList <V> part2 = ListUtils.subList(valueList, upperIdx, valueList.size());
			valueList.clear();
			valueList.ensureCapacity(part1.size() + part2.size());
			valueList.addAll(part1);
			valueList.addAll(part2);
		}
	}

	@Override
	public IndexedSet <K> keySet() {
		if (_keySet == null) {
			_keySet = new KeySet();
		}
		return _keySet;
	}

	@Override
	public Collection <V> values() {
		if (_valColl == null) {
			_valColl = new ValueCollection();
		}
		return _valColl;
	}

	@Override
	public Set <Entry <K, V>> entrySet() {
		if (_entrySet == null) {
			_entrySet = new EntrySet();
		}
		return _entrySet;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		int from = 0;
		int to = keyList == null ? 0 : keyList.size();
		if (maxLimit != null || minLimit != null) {
			final Limits limits = getLimit();
			from = limits.lowerLimit;
			to = limits.upperLimit;
		}
		for (int i = from; i < to; i++) {
			sb.append(keyList.get(i));
			sb.append("=");
			final V value = valueList.get(i);
			// These conditions is needed to simualte basic Stack Overflow prevention,
			// that is provided e.g. by TreeMap
			if (value == this) {
				sb.append("(this Map)");
			} else if (value != null && value == _valColl) {
				sb.append("[(this Collection)]");
			} else {
				sb.append(value);
			}
			if (i < to - 1) {
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	// /**
	// * @return a String representation of map content, ignoring submap bounds (if any)
	// */
	// public String toStringUnbounded(){
	// final StringBuilder sb = new StringBuilder("[");
	// final int to = keyList == null ? 0 : keyList.size();
	// for (int i = 0; i < to; i++) {
	// sb.append(keyList.get(i));
	// sb.append("=");
	// V value = valueList.get(i);
	// // These conditions is needed to simualte basic Stack Overflow prevention,
	// // that is provided e.g. by TreeMap
	// if (value == this) {
	// sb.append("(this Map)");
	// } else if (value == _valColl) {
	// sb.append("[(this Collection)]");
	// } else {
	// sb.append(value);
	// }
	// if (i < to - 1) sb.append(",");
	// }
	// sb.append("]");
	// return sb.toString();
	// }
	/**
	 * Trims the capacity of this map to be the map's current size. An application can use this
	 * operation to minimize the storage of a map instance.
	 *
	 * @see ArrayList#trimToSize()
	 */
	public void trimToSize() {
		if (keyList != null) {
			incKeyModCount(1);
			incValModCount(1);
			keyList.trimToSize();
			valueList.trimToSize();
		}
	}

	/**
	 * Creates and return a map's shallow copy. It will be safe for structural changes such as
	 * adding/removing/modifying, but the stored keys and values themself are not copied and may be
	 * unsafe for modification. This does not affect an immutable classes, that creates a self-copy
	 * upon modification (such as {@code String} or {@code Integer}
	 */
	@Override
	public BasicSortedMap <K, V> clone() {
		try {
			final BasicSortedMap <K, V> clone = (BasicSortedMap <K, V>) super.clone();
			clone.keyList = keyList == null ? null : (SortedArrayList <K>) keyList.clone();
			clone.valueList = valueList == null ? null : (ArrayList <V>) valueList.clone();
			clone._keySet = null;
			clone._valColl = null;
			clone._entrySet = null;
			clone.keyModCount = 0;
			clone.valModCount = 0;
			if (parent != null) {
				clone.parent = parent;
			} else {
				clone.parent = null;
			}
			return clone;
		} catch(final CloneNotSupportedException ex) {
			assert false;
			return null; // Impossible
		}
	}

	@Override
	public Comparator <? super K> comparator() {
		return null;
	}

	/**
	 * Returns a view of the portion of this map whose keys range from {@code fromKey}, inclusive,
	 * to {@code toKey}, exclusive. (If {@code fromKey} and {@code toKey} are equal, the returned
	 * map is empty.) The returned map is backed by this map, so changes in the returned map are
	 * reflected in this map, and vice-versa. The returned map supports all optional map operations
	 * that this map supports.
	 * <p>
	 * The returned map will throw an {@code IllegalArgumentException} on an attempt to insert a key
	 * outside its range.
	 * <p>
	 * Implementation note: the returned map is not a wrapper and uses the same data storage, as the
	 * original map. Unlike {@link AbstractList#subList(int, int)}, recursively calling the
	 * {@code subMap}, {@code headMap} or {@code tailMap} for returned map will not cause any
	 * performance issues.
	 *
	 * @param fromKey
	 *            low endpoint (inclusive) of the keys in the returned map
	 * @param toKey
	 *            high endpoint (exclusive) of the keys in the returned map
	 * @return a view of the portion of this map whose keys range from <tt>fromKey</tt>, inclusive,
	 *         to <tt>toKey</tt>, exclusive
	 * @see #headMap(Comparable)
	 * @see #tailMap(Comparable)
	 */
	@Override
	public BasicSortedMap <K, V> subMap(final K fromKey, final K toKey) {
		ObjectUtils.requireNonNull(fromKey, "fromKey");
		ObjectUtils.requireNonNull(toKey, "toKey");
		if (fromKey.compareTo(toKey) > 0) throw new IllegalArgumentException("fromKey > toKey");
		if (maxLimit != null && maxLimit.compareTo(fromKey) < 1)
			throw new IllegalArgumentException("fromKey out of range");
		if (minLimit != null && minLimit.compareTo(toKey) > -1)
			throw new IllegalArgumentException("toKey out of range");
		final BasicSortedMap <K, V> result = new BasicSortedMap <K, V>();
		result.keyList = keyList;
		result.valueList = valueList;
		result.minLimit = fromKey;
		result.maxLimit = toKey;
		if (parent != null) {
			result.parent = parent;
		} else {
			result.parent = this;
		}
		return result;
	}

	/**
	 * Returns a view of the portion of this map whose keys are strictly less than {@code toKey}.
	 * The returned map is backed by this map, so changes in the returned map are reflected in this
	 * map, and vice-versa. The returned map supports all optional map operations that this map
	 * supports.
	 * <p>
	 * The returned map will throw an {@code IllegalArgumentException} on an attempt to insert a key
	 * outside its range.
	 * <p>
	 * Implementation note: the returned map is not a wrapper and uses the same data storage, as the
	 * original map. Unlike {@link AbstractList#subList(int, int)}, recursively calling the
	 * {@code subMap}, {@code headMap} or {@code tailMap} for returned map will not cause any
	 * perfomance issues.
	 *
	 * @see #subMap(Comparable,Comparable)
	 * @see #tailMap(Comparable)
	 */
	@Override
	public BasicSortedMap <K, V> headMap(final K toKey) {
		ObjectUtils.requireNonNull(toKey, "toKey");
		if (minLimit != null && minLimit.compareTo(toKey) > -1)
			throw new IllegalArgumentException("toKey out of range");
		final BasicSortedMap <K, V> result = new BasicSortedMap <K, V>();
		result.keyList = keyList;
		result.valueList = valueList;
		result.minLimit = minLimit;
		result.maxLimit = toKey;
		if (parent != null) {
			result.parent = parent;
		} else {
			result.parent = this;
		}
		return result;
	}

	/**
	 * Returns a view of the portion of this map whose keys are greater than or equal to fromKey.
	 * The returned map is backed by this map, so changes in the returned map are reflected in this
	 * map, and vice-versa. The returned map supports all optional map operations that this map
	 * supports.
	 * <p>
	 * The returned map will throw an {@code IllegalArgumentException} on an attempt to insert a key
	 * outside its range.
	 * <p>
	 * Implementation note: the returned map is not a wrapper and uses the same data storage, as the
	 * original map. Unlike {@link AbstractList#subList(int, int)}, recursively calling the
	 * {@code subMap}, {@code headMap} or {@code tailMap} for returned map will not cause any
	 * performance issues.
	 *
	 * @see #headMap(Comparable)
	 * @see #subMap(Comparable,Comparable)
	 */
	@Override
	public BasicSortedMap <K, V> tailMap(final K fromKey) {
		ObjectUtils.requireNonNull(fromKey, "fromKey");
		if (maxLimit != null && maxLimit.compareTo(fromKey) < 1)
			throw new IllegalArgumentException("fromKey out of range");
		final BasicSortedMap <K, V> result = new BasicSortedMap <K, V>();
		result.keyList = keyList;
		result.valueList = valueList;
		result.minLimit = fromKey;
		result.maxLimit = maxLimit;
		if (parent != null) {
			result.parent = parent;
		} else {
			result.parent = this;
		}
		return result;
	}

	@Override
	public K firstKey() {
		if (isEmpty()) throw new NoSuchElementException("Map is empty");
		if (minLimit != null) {
			final int idx = Collections.binarySearch(keyList, minLimit);
			return idx < 0 ? keyList.get(-1 - idx) : keyList.get(idx);
		}
		if (keyList != null) return keyList.get(0);
		return null;
	}

	@Override
	public K lastKey() {
		if (isEmpty()) throw new NoSuchElementException("Map is empty");
		if (maxLimit != null) {
			final int idx = Collections.binarySearch(keyList, maxLimit);
			return idx < 0 ? keyList.get(-2 - idx) : keyList.get(idx - 1);
		}
		if (keyList != null) return keyList.get(keyList.size() - 1);
		return null;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Map <?, ?>)) return false;
		final Map <?, ?> cast = (Map <?, ?>) o;
		if (keyList == null) return cast.isEmpty();
		if (size() != cast.size()) return false;
		return entrySet().equals(cast.entrySet());
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		final Iterator <Entry <K, V>> i = entrySet().iterator();
		while (i.hasNext()) {
			hashCode += i.next().hashCode();
		}
		return hashCode;
	}

	// Internal
	protected void incKeyModCount(final int i) {
		this.keyModCount += i;
		if (parent != null) {
			parent.keyModCount += i;
		}
	}

	protected void incValModCount(final int i) {
		this.valModCount += i;
		if (parent != null) {
			parent.valModCount += i;
		}
	}

	protected boolean isOutOfBounds(final K k) {
		if (minLimit != null && minLimit.compareTo(k) > 0) return true;
		if (maxLimit != null && maxLimit.compareTo(k) <= 0) return true;
		return false;
	}

	/**
	 * @param k
	 *            a key, that may or may not exist
	 * @return an index of a key (from a whole keyset), if it exists, or a position, where it should
	 *         be, if there's none.
	 */
	protected int prefIndexOfKey(final K k) {
		int idx = Collections.binarySearch(keyList, k);
		if (idx < 0) {
			idx = -idx - 1;
		}
		return idx;
	}

	protected Limits getLimit() {
		int lowerLimit = 0;
		if (minLimit != null) {
			final int idx = Collections.binarySearch(keyList, minLimit);
			lowerLimit = idx < 0 ? -1 - idx : idx;
		}
		int upperLimit;
		if (maxLimit != null) {
			final int idx = Collections.binarySearch(keyList, maxLimit);
			upperLimit = idx < 0 ? -1 - idx : idx;
		} else {
			upperLimit = keyList.size();
		}
		return new Limits(lowerLimit, upperLimit);
	}

	protected class Limits {

		int	lowerLimit;
		int	upperLimit;

		public Limits(final int lowerLimit, final int upperLimit) {
			this.lowerLimit = lowerLimit;
			this.upperLimit = upperLimit;
		}
	}

	// Backed collections implementation
	protected class KeySet implements IndexedSet <K>, Cloneable {

		@Override
		public int addAndGetPos(final K object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(final K element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(final Object key) {
			return BasicSortedMap.this.containsKey(key);
		}

		@Override
		public boolean remove(final Object key) {
			return BasicSortedMap.this.remove(key) == null;
		}

		@Override
		public int size() {
			return BasicSortedMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return BasicSortedMap.this.isEmpty();
		}

		@Override
		public Iterator <K> iterator() {
			return new KeyIterator();
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			if (keyList == null) return c.isEmpty();
			if (minLimit == null && maxLimit == null) return keyList.containsAll(c);
			final Limits limits = BasicSortedMap.this.getLimit();
			return keyList.subList(limits.lowerLimit, limits.upperLimit).containsAll(c);
		}

		@Override
		public BasicSortedMap <K, V> clone() throws CloneNotSupportedException {
			final BasicSortedMap <K, V> result = (BasicSortedMap <K, V>) super.clone();
			return result;
		}

		@Override
		public Object[] toArray() {
			if (keyList == null) return new Object[0];
			if (minLimit == null && maxLimit == null) {
				keyList.toArray();
			}
			final Limits limits = BasicSortedMap.this.getLimit();
			return keyList.subList(limits.lowerLimit, limits.upperLimit).toArray();
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			if (keyList == null) return (T[]) Array.newInstance(a.getClass(), 0);
			if (minLimit == null && maxLimit == null) return keyList.toArray(a);
			final Limits limits = BasicSortedMap.this.getLimit();
			return keyList.subList(limits.lowerLimit, limits.upperLimit).toArray(a);
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			final Iterator <?> iter = c.iterator();
			boolean changed = false;
			while (iter.hasNext()) {
				changed = BasicSortedMap.this.remove(iter.next()) != null || changed;
			}
			return changed;
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			boolean changed = false;
			int start = 0;
			int end = keyList.size();
			if (minLimit != null || maxLimit != null) {
				final Limits limits = BasicSortedMap.this.getLimit();
				start = limits.lowerLimit;
				end = limits.upperLimit;
			}
			for (int i = start; keyList != null && i < end;) {
				final K key = keyList.get(i);
				if (c.contains(key)) {
					++i;
				} else {
					changed = true;
					keyList.remove(i);
					valueList.remove(i);
					--end;
					if (keyList.isEmpty()) {
						keyList = null;
						valueList = null;
					}
				}
			}
			return changed;
		}

		@Override
		public void clear() {
			BasicSortedMap.this.clear();
		}

		@Override
		public String toString() {
			if (keyList == null) return "[]";
			if (minLimit == null && maxLimit == null) return keyList.toString();
			final Limits limits = BasicSortedMap.this.getLimit();
			return keyList.subList(limits.lowerLimit, limits.upperLimit).toString();
		}

		@Override
		public boolean equals(final Object o) {
			if (keyList == null) return false;
			if (o == null) return false;
			if (!(o instanceof Set)) return false;
			final Set <?> cast = (Set <?>) o;
			if (this.size() != cast.size()) return false;
			final Limits limits = BasicSortedMap.this.getLimit();
			final Iterator <?> iter = minLimit == null && maxLimit == null ? iterator()
					: keyList.subList(limits.lowerLimit, limits.upperLimit).iterator();
			while (iter.hasNext()) {
				if (!cast.contains(iter.next())) return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			if (keyList == null) return 0;
			if (minLimit == null && maxLimit == null) return keyList.hashCode();
			final Limits limits = BasicSortedMap.this.getLimit();
			return keyList.subList(limits.lowerLimit, limits.upperLimit).hashCode();
		}

		@Override
		public K get(final int idx) {
			return BasicSortedMap.this.getKeyAt(idx);
		}

		@Override
		public K remove(final int index) {
			return BasicSortedMap.this.removeAt(index).getKey();
		}

		@Override
		public int indexOf(final Object key) {
			return BasicSortedMap.this.indexOfKey(key);
		}
	}

	protected class ValueCollection implements Collection <V> {

		@Override
		public boolean add(final V e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends V> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return BasicSortedMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return BasicSortedMap.this.isEmpty();
		}

		@Override
		public boolean contains(final Object o) {
			return BasicSortedMap.this.containsValue(o);
		}

		@Override
		public Iterator <V> iterator() {
			return new ValueIterator();
		}

		@Override
		public Object[] toArray() {
			if (keyList == null) return new Object[0];
			if (minLimit == null && maxLimit == null) {
				valueList.toArray();
			}
			final Limits limits = BasicSortedMap.this.getLimit();
			return valueList.subList(limits.lowerLimit, limits.upperLimit).toArray();
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			if (keyList == null) return (T[]) Array.newInstance(a.getClass(), 0);
			if (minLimit == null && maxLimit == null) return valueList.toArray(a);
			final Limits limits = BasicSortedMap.this.getLimit();
			return valueList.subList(limits.lowerLimit, limits.upperLimit).toArray(a);
		}

		@Override
		public boolean remove(final Object o) {
			if (keyList == null) return false;
			final int idx = valueList.indexOf(o);
			if (idx < 0) return false;
			return BasicSortedMap.this.remove(keyList.get(idx)) != null;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			if (keyList == null) return c.isEmpty();
			if (minLimit == null && maxLimit == null) return valueList.containsAll(c);
			final Limits limits = BasicSortedMap.this.getLimit();
			return valueList.subList(limits.lowerLimit, limits.upperLimit).containsAll(c);
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			int start = 0;
			int end = keyList.size();
			if (minLimit != null || maxLimit != null) {
				final Limits limits = BasicSortedMap.this.getLimit();
				start = limits.lowerLimit;
				end = limits.upperLimit;
			}
			boolean changed = false;
			for (int i = start; keyList != null && i < end;) {
				if (!c.contains(valueList.get(i))) {
					++i;
				} else {
					changed = true;
					keyList.remove(i);
					valueList.remove(i);
					--end;
					if (keyList.isEmpty()) {
						keyList = null;
						valueList = null;
					}
				}
			}
			return changed;
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			int start = 0;
			int end = keyList.size();
			if (minLimit != null || maxLimit != null) {
				final Limits limits = BasicSortedMap.this.getLimit();
				start = limits.lowerLimit;
				end = limits.upperLimit;
			}
			boolean changed = false;
			for (int i = start; keyList != null && i < end;) {
				if (c.contains(valueList.get(i))) {
					++i;
				} else {
					changed = true;
					keyList.remove(i);
					valueList.remove(i);
					--end;
					if (keyList.isEmpty()) {
						keyList = null;
						valueList = null;
					}
				}
			}
			return changed;
		}

		@Override
		public void clear() {
			BasicSortedMap.this.clear();
		}

		@Override
		public String toString() {
			if (keyList == null) return "[]";
			if (minLimit == null && maxLimit == null) return valueList.toString();
			final Limits limits = BasicSortedMap.this.getLimit();
			return valueList.subList(limits.lowerLimit, limits.upperLimit).toString();
		}

		@Override
		public boolean equals(final Object o) {
			if (keyList == null) return false;
			if (o == null) return false;
			if (!(o instanceof Collection)) return false;
			final Collection <?> s = (Collection <?>) o;
			if (this.size() != s.size()) return false;
			final Limits limits = BasicSortedMap.this.getLimit();
			final Iterator <?> iter1 = minLimit == null && maxLimit == null ? iterator()
					: valueList.subList(limits.lowerLimit, limits.upperLimit).iterator();
			final Iterator <?> iter2 = s.iterator();
			while (iter1.hasNext() && iter2.hasNext()) {
				if (!iter1.next().equals(iter2.next())) return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			if (keyList == null) return 0;
			if (minLimit == null && maxLimit == null) return valueList.hashCode();
			final Limits limits = BasicSortedMap.this.getLimit();
			return valueList.subList(limits.lowerLimit, limits.upperLimit).hashCode();
		}
	}

	protected class EntrySet implements Set <Entry <K, V>> {

		@Override
		public boolean add(final java.util.Map.Entry <K, V> e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends java.util.Map.Entry <K, V>> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return BasicSortedMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return BasicSortedMap.this.isEmpty();
		}

		@Override
		public boolean contains(final Object obj) {
			if (!(obj instanceof Entry)) return false;
			final Object keyObj = ((Entry <?, ?>) obj).getKey();
			final Object valueObj = ((Entry <?, ?>) obj).getValue();
			final int idx = BasicSortedMap.this.indexOfKey(keyObj);
			if (idx < 0) return false;
			final Object valueThis = BasicSortedMap.this.getValueAt(idx);
			if (valueThis == null) return valueObj == null;
			return valueThis.equals(valueObj);
		}

		@Override
		public Iterator <Entry <K, V>> iterator() {
			return new EntrySetIterator();
		}

		@Override
		public Object[] toArray() {
			if (keyList == null) return new Object[0];
			final int size = BasicSortedMap.this.size();
			@SuppressWarnings("unchecked")
			final Entry <K, V>[] result = new Entry[size];
			int from = 0;
			int to = size;
			if (minLimit != null || maxLimit != null) {
				final Limits limits = BasicSortedMap.this.getLimit();
				from = limits.lowerLimit;
				to = limits.upperLimit;
			}
			int iter = -1;
			for (int i = from; i < to; i++) {
				result[++iter] = new Pair <K, V>(keyList.get(i), valueList.get(i));
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T>T[] toArray(final T[] a) {
			if (keyList == null) return (T[]) Array.newInstance(a.getClass(), 0);
			final int size = BasicSortedMap.this.size();
			final Entry <K, V>[] result = new Entry[keyList.size()];
			int from = 0;
			int to = size;
			if (minLimit != null || maxLimit != null) {
				final Limits limits = BasicSortedMap.this.getLimit();
				from = limits.lowerLimit;
				to = limits.upperLimit;
			}
			int iter = -1;
			for (int i = from; i < to; i++) {
				result[++iter] = new Pair <K, V>(keyList.get(i), valueList.get(i));
			}
			return (T[]) Arrays.copyOf(result, result.length, a.getClass());
		}

		@Override
		public boolean remove(final Object o) {
			if (!(o instanceof Entry)) return false;
			final Object key = ((Entry <?, ?>) o).getKey();
			return BasicSortedMap.this.remove(key) != null;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			final Iterator <?> iter = c.iterator();
			while (iter.hasNext()) {
				final Object next = iter.next();
				if (!(next instanceof Entry)) return false;
				final Object key = ((Entry <?, ?>) next).getKey();
				if (!BasicSortedMap.this.containsKey(key)) return false;
			}
			return true;
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			boolean changed = false;
			int start = 0;
			int end = keyList.size();
			if (minLimit != null || maxLimit != null) {
				final Limits limits = BasicSortedMap.this.getLimit();
				start = limits.lowerLimit;
				end = limits.upperLimit;
			}
			for (int i = start; keyList != null && i < end;) {
				final K key = keyList.get(i);
				if (c.contains(new Pair <K, V>(key, valueList.get(i)))) {
					++i;
				} else {
					changed = true;
					keyList.remove(i);
					valueList.remove(i);
					--end;
					if (keyList.isEmpty()) {
						keyList = null;
						valueList = null;
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
				if (BasicSortedMap.this.remove(key) != null) {
					changed = true;
				}
			}
			return changed;
		}

		@Override
		public void clear() {
			BasicSortedMap.this.clear();
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
		public boolean equals(final Object o) {
			if (o == null) return false;
			if (!(o instanceof Set)) return false;
			final Set <?> s = (Set <?>) o;
			if (this.size() != s.size()) return false;
			final Iterator <?> iter = iterator();
			while (iter.hasNext()) {
				final Object next = iter.next();
				if (!s.contains(next)) return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			int result = 0;
			for (final Entry <K, V> element : this) {
				result += element.hashCode();
			}
			return result;
		}
	}

	protected class BackedMapEntry extends AbstractMapEntry <K, V> {

		protected final K	key;
		protected V			value;

		public BackedMapEntry(final K key, final V value) {
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
			return valueList.set(keyList.indexOf(key), value);
		}
	}

	// Iterators
	protected class KeyIterator implements Iterator <K> {

		protected int		pos				= -10;
		protected int		lower			= -1;
		protected int		upper			= -1;
		protected boolean	removeCalled	= true;
		protected int		expectedModCount;
		protected int		expectedParentModCount;

		protected void checkForComodification() {
			if (expectedModCount != keyModCount) throw new ConcurrentModificationException();
			if (parent != null && expectedParentModCount != parent.keyModCount)
				throw new ConcurrentModificationException();
		}

		protected KeyIterator() {
			expectedModCount = keyModCount;
			if (parent != null) {
				expectedParentModCount = parent.keyModCount;
			}
		}

		protected void detPos() {
			if (keyList == null) {
				lower = 0;
				upper = 0;
			} else if (minLimit == null && maxLimit == null) {
				lower = 0;
				upper = keyList.size();
			} else {
				final Limits limits = BasicSortedMap.this.getLimit();
				lower = limits.lowerLimit;
				upper = limits.upperLimit;
			}
			pos = lower - 1;
		}

		@Override
		public boolean hasNext() {
			checkForComodification();
			if (pos == -10) {
				detPos();
			}
			return keyList != null && pos + 1 < upper;
		}

		@Override
		public K next() {
			checkForComodification();
			if (keyList == null) throw new NoSuchElementException();
			if (pos == -10) {
				detPos();
			}
			++pos;
			if (pos >= upper) throw new NoSuchElementException();
			removeCalled = false;
			return keyList.get(pos);
		}

		@Override
		public void remove() {
			if (keyList == null || pos < lower)
				throw new IllegalStateException("Remove only allowed after next() call");
			if (removeCalled) throw new IllegalStateException("Remove has already been called");
			removeCalled = true;
			checkForComodification();
			BasicSortedMap.this.remove(keyList.get(pos));
			++expectedModCount;
			++expectedParentModCount;
			--upper;
			--pos;
		}
	}

	protected class ValueIterator implements Iterator <V> {

		protected int		pos				= -10;
		protected int		lower			= -1;
		protected int		upper			= -1;
		protected boolean	removeCalled	= true;
		protected int		expectedModCount;
		protected int		expectedParentModCount;

		protected void checkForComodification() {
			if (expectedModCount != valModCount) throw new ConcurrentModificationException();
			if (parent != null && expectedParentModCount != parent.valModCount)
				throw new ConcurrentModificationException();
		}

		protected ValueIterator() {
			expectedModCount = valModCount;
			if (parent != null) {
				expectedParentModCount = parent.valModCount;
			}
		}

		private void detPos() {
			if (keyList == null) {
				lower = 0;
				upper = 0;
			} else if (minLimit == null && maxLimit == null) {
				lower = 0;
				upper = keyList.size();
			} else {
				final Limits limits = BasicSortedMap.this.getLimit();
				lower = limits.lowerLimit;
				upper = limits.upperLimit;
			}
			pos = lower - 1;
		}

		@Override
		public boolean hasNext() {
			checkForComodification();
			if (pos == -10) {
				detPos();
			}
			return keyList != null && pos + 1 < upper;
		}

		@Override
		public V next() {
			checkForComodification();
			if (keyList == null) throw new NoSuchElementException();
			if (pos == -10) {
				detPos();
			}
			++pos;
			if (pos >= upper) throw new NoSuchElementException();
			removeCalled = false;
			return valueList.get(pos);
		}

		@Override
		public void remove() {
			if (keyList == null || pos < lower)
				throw new IllegalStateException("Remove only allowed after next() call");
			if (removeCalled) throw new IllegalStateException("Remove has already been called");
			removeCalled = true;
			checkForComodification();
			BasicSortedMap.this.remove(keyList.get(pos));
			++expectedModCount;
			++expectedParentModCount;
			--upper;
			--pos;
		}
	}

	protected class EntrySetIterator implements Iterator <Entry <K, V>> {

		protected int		pos				= -10;
		protected int		lower			= -1;
		protected int		upper			= -1;
		protected boolean	removeCalled	= true;
		protected int		expectedKeyModCount;
		protected int		expectedParentKeyModCount;
		protected int		expectedValModCount;
		protected int		expectedParentValModCount;

		protected void checkForComodification() {
			if (expectedKeyModCount != keyModCount) throw new ConcurrentModificationException();
			if (parent != null && expectedParentKeyModCount != parent.keyModCount)
				throw new ConcurrentModificationException();
			if (expectedValModCount != valModCount) throw new ConcurrentModificationException();
			if (parent != null && expectedParentValModCount != parent.valModCount)
				throw new ConcurrentModificationException();
		}

		protected EntrySetIterator() {
			expectedKeyModCount = keyModCount;
			if (parent != null) {
				expectedParentKeyModCount = parent.keyModCount;
			}
			expectedValModCount = valModCount;
			if (parent != null) {
				expectedParentValModCount = parent.valModCount;
			}
		}

		private void detPos() {
			if (keyList == null) {
				lower = 0;
				upper = 0;
			} else if (minLimit == null && maxLimit == null) {
				lower = 0;
				upper = keyList.size();
			} else {
				final Limits limits = BasicSortedMap.this.getLimit();
				lower = limits.lowerLimit;
				upper = limits.upperLimit;
			}
			pos = lower - 1;
		}

		@Override
		public boolean hasNext() {
			checkForComodification();
			if (pos == -10) {
				detPos();
			}
			return keyList != null && pos + 1 < upper;
		}

		@Override
		public Entry <K, V> next() {
			checkForComodification();
			if (keyList == null) throw new NoSuchElementException();
			if (pos == -10) {
				detPos();
			}
			++pos;
			if (pos >= upper) throw new NoSuchElementException();
			removeCalled = false;
			return new BackedMapEntry(keyList.get(pos), valueList.get(pos));
		}

		@Override
		public void remove() {
			if (keyList == null || pos < lower)
				throw new IllegalStateException("Remove only allowed after next() call");
			if (removeCalled) throw new IllegalStateException("Remove has already been called");
			removeCalled = true;
			checkForComodification();
			BasicSortedMap.this.remove(keyList.get(pos));
			++expectedKeyModCount;
			++expectedParentKeyModCount;
			++expectedValModCount;
			++expectedParentValModCount;
			--upper;
			--pos;
		}
	}
}
