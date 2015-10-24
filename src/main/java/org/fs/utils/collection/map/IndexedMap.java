package org.fs.utils.collection.map;

import java.util.Map;
import java.util.RandomAccess;
import java.util.SortedMap;

import org.fs.utils.collection.set.IndexedSet;

/**
 * This interface declares methods, that allows keys and values of this {@link Map} to be accessed
 * via their indices.
 * <p>
 * Instances may also implement {@link RandomAccess} to indicate fast index-based access.
 *
 * @author FS
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 */
public interface IndexedMap<K,V> extends Map <K, V> {

	/**
	 * Returns a map key at the specified index.
	 *
	 * @param index
	 * @return key object
	 * @throws IndexOutOfBoundsException
	 *             if index < 0 or >= size
	 */
	K getKeyAt(int index);

	/**
	 * Returns a map value at the specified index.
	 *
	 * @param index
	 * @return value object
	 * @throws IndexOutOfBoundsException
	 *             if index < 0 or >= size
	 */
	V getValueAt(int index);

	/**
	 * Changes the key at the given position.
	 *
	 * @param index
	 * @param newKey
	 * @return old key
	 * @throws UnsupportedOperationException
	 *             if keys cannot be changed that way (e.g. this is {@link SortedMap})
	 * @throws IndexOutOfBoundsException
	 *             if index < 0 or >= size
	 * @throws IllegalArgumentException
	 *             if this is a bounded subMap and key is out of bounds
	 */
	K setKeyAt(int index, K newKey);

	/**
	 * Changes the value at the given position.
	 *
	 * @param index
	 * @param newValue
	 * @return old value
	 * @throws IndexOutOfBoundsException
	 *             if index < 0 or >= size
	 */
	V setValueAt(int index, V newValue);

	/**
	 * Returns an index of a specified key in the map.
	 *
	 * @param key
	 * @return key index or -1 if not found
	 */
	int indexOfKey(Object key);

	/**
	 * Returns an index of a specified value in the map.
	 *
	 * @param value
	 * @return value index or -1 if not found
	 */
	int indexOfValue(Object value);

	/**
	 * Removes a map record at the specified position.
	 *
	 * @param index
	 * @return entry, containig key and value, that were deleted. Entry is obviously not backed by
	 *         the map.
	 * @throws IndexOutOfBoundsException
	 *             if index < 0 or >= size
	 */
	Entry <K, V> removeAt(int index);

	@Override
	IndexedSet <K> keySet();
}

