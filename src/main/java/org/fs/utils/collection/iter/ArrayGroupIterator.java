package org.fs.utils.collection.iter;

import java.util.Arrays;

/**
 * This is array iterator, that returns groups of N nearest elements. Returns an array, obtained
 * with {@link Arrays#copyOfRange(Object[], int, int)}. <br>
 * Example:<br>
 * <code>ArrayGroupIterator(new String[]{"a","b","c","d"}, 2)</code> will return
 * <code>["a","b"], ["b","c"], ["c","d"]</code>
 *
 * @param <T>
 * @author FS
 */
public class ArrayGroupIterator<T> extends AbstractIterator <T[]> {

	private final T[]	objectArr;
	private final int	width;
	private int			cpos;
	private final int	len;

	/** Iterates over an array with a group of a given width */
	@SuppressWarnings("javadoc")
	public ArrayGroupIterator(final T[] arr, final int width) {
		this(arr, width, 0);
	}

	/** Iterates over an array with a group of a given width, starting from a specified position */
	@SuppressWarnings("javadoc")
	public ArrayGroupIterator(final T[] arr, final int width, final int cpos) {
		if (width <= 0) throw new IllegalArgumentException("width must be positive");
		this.objectArr = arr;
		this.width = width;
		this.cpos = cpos;
		this.len = objectArr.length;
	}

	@Override
	public boolean hasNext() {
		return cpos + width <= len;
	}

	@Override
	public T[] next() {
		if (!hasNext()) return null;
		final T[] result = Arrays.copyOfRange(objectArr, cpos, cpos + width);
		cpos++;
		return result;
	}
}

