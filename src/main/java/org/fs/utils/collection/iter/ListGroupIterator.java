package org.fs.utils.collection.iter;

import java.util.Arrays;
import java.util.List;

/**
 * This is array iterator, that returns groups of N nearest elements. Returns an array, obtained
 * with {@link Arrays#copyOfRange(Object[], int, int)}. <br>
 * Example:<br>
 * <code>ArrayGroupIterator(Arrays.asList{"a","b","c","d"}, 2)</code> will return
 * <code>["a","b"], ["b","c"], ["c","d"]</code>
 *
 * @param <T>
 * @author FS
 */
public class ListGroupIterator<T> extends AbstractIterator <T[]> {

	@SuppressWarnings("unchecked")
	private final T[]		emptyList	= (T[])new Object[0];
	private final List <T>	objectList;
	private final int		width;
	private int				cpos;
	private final int		len;

	/** Iterates over a list with a group of a given width */
	@SuppressWarnings("javadoc")
	public ListGroupIterator(final List <T> list, final int width) {
		this(list, width, 0);
	}

	/** Iterates over a list with a group of a given width, starting from a specified position */
	@SuppressWarnings("javadoc")
	public ListGroupIterator(final List <T> list, final int width, final int cpos) {
		if (width <= 0) throw new IllegalArgumentException("width must be positive");
		this.objectList = list;
		this.width = width;
		this.cpos = cpos;
		len = objectList.size();
	}

	@Override
	public boolean hasNext() {
		return cpos + width <= len;
	}

	@Override
	public T[] next() {
		if (!hasNext()) return null;
		final List <T> result = nextList();
		return result.toArray(emptyList);
	}

	public List <T> nextList() {
		if (!hasNext()) return null;
		final List <T> result = objectList.subList(cpos, cpos + width);
		cpos++;
		return result;
	}
}

