package org.fs.utils.collection.iter;

import java.util.Iterator;

/**
 * A iterator over keyed collection.
 *
 * @author FS
 * @param <T>
 *            content type
 * @param <K>
 *            key type
 */
public interface KeyIterator<T,K> extends Iterator <T> {

	/**
	 * @return current iterator key
	 * @throws IllegalStateException
	 *             if {@link #next()} has not been called yet, or {@link #remove()} has been invoked
	 *             on current element
	 */
	public K getKey() throws IllegalStateException;
}
