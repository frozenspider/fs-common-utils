package org.fs.utils.collection.iter;

import java.util.Iterator;

/**
 * Provides default implementation for {@code remove()}, throwing
 * {@code UnsupportedOperationException}.
 *
 * @author FS
 * @param <E>
 */
public abstract class AbstractIterator<E> implements Iterator <E> {

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
