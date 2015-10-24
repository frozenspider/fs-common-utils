package org.fs.utils.collection.map;

import java.util.Map.Entry;

import org.fs.utils.ObjectUtils;

/**
 * Abstract implementation of {@link java.util.Map.Entry} class, implementing {@code hashCode()},
 * {@code equals()} and {@code toString()}.
 * <p>
 * You still need to implement {@link Entry#getKey() getKey()}, {@link Entry#getValue() getValue()}
 * and {@link Entry#setValue(Object) setValue(Object)}.
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 * @author FS
 */
public abstract class AbstractMapEntry<K,V> implements Entry <K, V> {
	
	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(getKey()) ^ ObjectUtils.hashCode(getValue());
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Entry <?, ?>)) return false;
		return ObjectUtils.equals(getKey(), ((Entry <?, ?>) obj).getKey())
				&& ObjectUtils.equals(getValue(), ((Entry <?, ?>) obj).getValue());
	}
	
	@Override
	public String toString() {
		return getKey() + "=" + getValue();
	}
}
