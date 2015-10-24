package org.fs.utils.collection.map;

import java.util.SortedMap;

/**
 * Unites {@link IndexedMap} and {@link SortedMap}.
 *
 * @author FS
 * @param <K>
 * @param <V>
 */
public interface IndexedSortedMap<K,V> extends IndexedMap <K, V>, SortedMap <K, V> {}

