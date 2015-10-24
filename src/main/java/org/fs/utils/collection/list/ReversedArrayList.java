package org.fs.utils.collection.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * You may think of this class like of {@link Stack} (it implements {@link #push(Object)},
 * {@link #pop()} and {@link #peek()} methods), that is <b>not synchronized</b>.
 * <p>
 * Note, however, that, although {@code push}, {@code peek} and {@code pop} method will yield
 * identical result for {@code Stack} and {@code ReversedArrayList}, their implementation and inner
 * order differs, so result of mixing {@code push/pop/peek} and {@code add/set/remove} for these two
 * will be different.
 * <p>
 * It acts excatly as normal {@link ArrayList}, but all of it's method are executed with reversed
 * index. It will return the same results, as the normal ArrayList would do. The difference is in
 * perfomance and memory consumption - the insertion in the beginning of the list will transform
 * into insertion to the end. That means, that {@code add(0, Object)} will act as fast, as
 * {@code  add(Object)} in normal ArrayList. Unfortunately, vice versa, {@code add(Object)} will have
 * perfomance of {@code add(0, Object)}.
 *
 * @param <E>
 *            any {@code Object}
 * @author FS
 */
public class ReversedArrayList<E> extends ArrayList <E> {

	private static final long	serialVersionUID	= -2120113500129768205L;

	// Constructors
	public ReversedArrayList() {
		super();
	}

	public ReversedArrayList(final Collection <? extends E> c) {
		super(c);
	}

	public ReversedArrayList(final int initialCapacity) {
		super(initialCapacity);
	}

	// Privates
	private int getRealIndex(final int index) {
		return size() - index - 1;
	}

	// Publics - custom
	// @formatter:off
	/**
	 * Pushes an item onto the top of this list. This has exactly the same effect as: <blockquote>
	 * {@code add(0, item)} </blockquote>
	 *
	 * @param 	item	the item to be pushed onto this stack.
	 * @return 	the <code>item</code> argument.
	 * @see 	#add(int, Object)
	 * @see 	java.util.Stack#push(Object)
	 */
	public E push(final E item){
		super.add(item);
		return item;
	}

	// @formatter:off
	/**
	 * Removes the object at the top of this list and returns that object as the value of this
	 * function.
	 *
	 * @return		the object at the top of this stack (the last item of the {@code ArrayList} object).
	 * @exception 	EmptyStackException if this list is empty.
	 * @see 		java.util.Stack#pop()
	 */
	// @formatter:on
	public E pop() {
		final int last = size() - 1;
		if (last == -1) throw new EmptyStackException();
		final E e = super.get(last);
		super.remove(last);
		return e;
	}

	// @formatter:off
	/**
     * Looks at the object at the top of this stack without removing it from the stack.
     *
     * @return		the object at the top of this stack (the last item of the {@code ArrayList} object).
     * @exception	EmptyStackException if this stack is empty.
     * @see			java.util.Stack#peek()
     */
	// @formatter:on
	public E peek() {
		final int last = size() - 1;
		if (last == -1) throw new EmptyStackException();
		return super.get(last);
	}

	// Publics - List default
	@Override
	public boolean add(final E e) {
		super.add(0, e);
		return true;
	}

	@Override
	public void add(final int index, final E element) {
		super.add(getRealIndex(index) + 1, element);
	}

	@Override
	public boolean addAll(final Collection <? extends E> c) {
		ensureCapacity(size() + c.size());
		for (final E e : c) {
			super.add(0, e);
		}
		return true;
	}

	@Override
	public boolean addAll(final int index, final Collection <? extends E> c) {
		ensureCapacity(size() + c.size());
		final int idx = getRealIndex(index) + 1;
		for (final E e : c) {
			super.add(idx, e);
		}
		return true;
	}

	@Override
	public ReversedArrayList <E> clone() {
		return (ReversedArrayList <E>)super.clone();
	}

	@Override
	public E get(final int index) {
		return super.get(getRealIndex(index));
	}

	@Override
	public int indexOf(final Object o) {
		final int idx = super.lastIndexOf(o);
		if (idx == -1) return -1;
		return getRealIndex(idx);
	}

	@Override
	public int lastIndexOf(final Object o) {
		final int idx = super.indexOf(o);
		if (idx == -1) return -1;
		return getRealIndex(idx);
	}

	@Override
	public E remove(final int index) {
		return super.remove(getRealIndex(index));
	}

	@Override
	public E set(final int index, final E element) {
		return super.set(getRealIndex(index), element);
	}

	@Override
	public Object[] toArray() {
		final Object[] result = super.toArray();
		Object temp;
		for (int i = 0; i < result.length / 2; i++) {
			temp = result[i];
			result[i] = result[result.length - i - 1];
			result[result.length - i - 1] = temp;
		}
		return result;
	}

	@Override
	public <T>T[] toArray(final T[] a) {
		final T[] result = super.toArray(a);
		T temp;
		for (int i = 0; i < result.length / 2; i++) {
			temp = result[i];
			result[i] = result[result.length - i - 1];
			result[result.length - i - 1] = temp;
		}
		return result;
	}

	public static void main(final String[] args) {
		// ReversedArrayList <String> ral = new ReversedArrayList <String>();
		final Stack <String> ral = new Stack <String>();
		ral.push("1");
		ral.push("2");
		ral.push("3");
		ral.push("4");
		System.out.println(ral);
		System.out.print(ral.pop() + " ");
		System.out.print(ral.pop() + " ");
		System.out.print(ral.pop() + " ");
		System.out.print(ral.pop() + " ");
	}
}

