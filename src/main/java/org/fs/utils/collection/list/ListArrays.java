package org.fs.utils.collection.list;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import org.fs.utils.ArrayUtils;

/**
 * Preferred usage of this class is via {@link ArrayUtils}{@code .asList()} function group.
 *
 * @author FS
 */
public class ListArrays {

	//
	// Lists
	//
	public static class BooleanList implements List <Boolean>, RandomAccess {

		// ++ Unsupported
		@Override
		public boolean add(final Boolean e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends Boolean> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection <? extends Boolean> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final Boolean element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Boolean remove(final int index) {
			throw new UnsupportedOperationException();
		}

		// -- Unsupported
		// ++ Polymorphic
		final int	first, last;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("[");
			for (int i = first; i <= last; i++) {
				if (i != first) {
					sb.append(", ");
				}
				sb.append(original[i]);
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public int size() {
			return last - first + 1;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public boolean contains(final Object o) {
			if (!(o instanceof Boolean)) return false;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return true;
			return false;
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			return (T[])Arrays.copyOf(toArray(), last - first + 1, a.getClass());
		}

		@Override
		public Boolean get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			return original[index + first];
		}

		@Override
		public Boolean set(final int index, final Boolean element) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			final boolean old = original[index + first];
			original[index + first] = element;
			return old;
		}

		@Override
		public int indexOf(final Object o) {
			if (!(o instanceof Boolean)) return -1;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public int lastIndexOf(final Object o) {
			if (!(o instanceof Boolean)) return -1;
			for (int i = last - 1; i >= first; i--)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			for (final Object obj : c)
				if (!contains(obj)) return false;
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof List)) return false;
			final ListIterator <?> e1 = listIterator();
			final ListIterator <?> e2 = ((List <?>)obj).listIterator();
			while (e1.hasNext() && e2.hasNext()) {
				final Object o1 = e1.next();
				final Object o2 = e2.next();
				if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
			}
			return !(e1.hasNext() || e2.hasNext());
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			final Iterator <?> i = this.iterator();
			while (i.hasNext()) {
				final Object obj = i.next();
				hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
			}
			return hashCode;
		}

		// -- Polymorphic
		final boolean[]	original;

		public BooleanList(final boolean[] original) {
			this.original = original;
			this.first = 0;
			this.last = original.length - 1;
		}

		public BooleanList(final boolean[] original, final int first, final int last) {
			this.original = original;
			this.first = first;
			this.last = last;
		}

		@Override
		public Iterator <Boolean> iterator() {
			return listIterator();
		}

		@Override
		public Object[] toArray() {
			final Boolean[] result = new Boolean[last - first + 1];
			int j = -1;
			for (int i = first; i <= last; ++i) {
				result[++j] = original[i];
			}
			return result;
		}

		@Override
		public ListIterator <Boolean> listIterator() {
			return new BooleanListIter(original, 0, first, last);
		}

		@Override
		public ListIterator <Boolean> listIterator(final int index) {
			return new BooleanListIter(original, index, first, last);
		}

		@Override
		public List <Boolean> subList(final int fromIndex, final int toIndex) {
			return new BooleanList(original, fromIndex, toIndex);
		}
	}

	public static class CharList implements List <Character>, RandomAccess {

		// ++ Unsupported
		@Override
		public boolean add(final Character e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends Character> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection <? extends Character> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final Character element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Character remove(final int index) {
			throw new UnsupportedOperationException();
		}

		// -- Unsupported
		// ++ Polymorphic
		final int	first, last;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("[");
			for (int i = first; i <= last; i++) {
				if (i != first) {
					sb.append(", ");
				}
				sb.append(original[i]);
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public int size() {
			return last - first + 1;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Number) {
				o = Character.valueOf((char)((Number)o).intValue());
			} else if (!(o instanceof Character)) return false;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return true;
			return false;
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			return (T[])Arrays.copyOf(toArray(), last - first + 1, a.getClass());
		}

		@Override
		public Character get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			return original[index + first];
		}

		@Override
		public Character set(final int index, final Character element) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			final char old = original[index + first];
			original[index + first] = element;
			return old;
		}

		@Override
		public int indexOf(Object o) {
			if (o instanceof Number) {
				o = Character.valueOf((char)((Number)o).intValue());
			} else if (!(o instanceof Character)) return -1;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			if (o instanceof Number) {
				o = Character.valueOf((char)((Number)o).intValue());
			} else if (!(o instanceof Character)) return -1;
			for (int i = last - 1; i >= first; i--)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			for (final Object obj : c)
				if (!contains(obj)) return false;
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof List)) return false;
			final ListIterator <?> e1 = listIterator();
			final ListIterator <?> e2 = ((List <?>)obj).listIterator();
			while (e1.hasNext() && e2.hasNext()) {
				final Object o1 = e1.next();
				final Object o2 = e2.next();
				if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
			}
			return !(e1.hasNext() || e2.hasNext());
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			final Iterator <?> i = this.iterator();
			while (i.hasNext()) {
				final Object obj = i.next();
				hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
			}
			return hashCode;
		}

		// -- Polymorphic
		final char[]	original;

		public CharList(final char[] original) {
			this.original = original;
			this.first = 0;
			this.last = original.length - 1;
		}

		public CharList(final char[] original, final int first, final int last) {
			this.original = original;
			this.first = first;
			this.last = last;
		}

		@Override
		public Iterator <Character> iterator() {
			return listIterator();
		}

		@Override
		public Object[] toArray() {
			final Character[] result = new Character[last - first + 1];
			int j = -1;
			for (int i = first; i <= last; ++i) {
				result[++j] = original[i];
			}
			return result;
		}

		@Override
		public ListIterator <Character> listIterator() {
			return new CharListIter(original, 0, first, last);
		}

		@Override
		public ListIterator <Character> listIterator(final int index) {
			return new CharListIter(original, index, first, last);
		}

		@Override
		public List <Character> subList(final int fromIndex, final int toIndex) {
			return new CharList(original, fromIndex, toIndex);
		}
	}

	public static class ByteList implements List <Byte>, RandomAccess {

		// ++ Unsupported
		@Override
		public boolean add(final Byte e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends Byte> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection <? extends Byte> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final Byte element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Byte remove(final int index) {
			throw new UnsupportedOperationException();
		}

		// -- Unsupported
		// ++ Polymorphic
		final int	first, last;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("[");
			for (int i = first; i <= last; i++) {
				if (i != first) {
					sb.append(", ");
				}
				sb.append(original[i]);
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public int size() {
			return last - first + 1;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Number) {
				o = Byte.valueOf(((Number)o).byteValue());
			} else if (o instanceof Character) {
				o = Byte.valueOf((byte)((Character)o).charValue());
			} else
				return false;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return true;
			return false;
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			return (T[])Arrays.copyOf(toArray(), last - first + 1, a.getClass());
		}

		@Override
		public Byte get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			return original[index + first];
		}

		@Override
		public Byte set(final int index, final Byte element) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			final byte old = original[index + first];
			original[index + first] = element;
			return old;
		}

		@Override
		public int indexOf(Object o) {
			if (o instanceof Number) {
				o = Byte.valueOf(((Number)o).byteValue());
			} else if (o instanceof Character) {
				o = Byte.valueOf((byte)((Character)o).charValue());
			} else
				return -1;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			if (o instanceof Number) {
				o = Byte.valueOf(((Number)o).byteValue());
			} else if (o instanceof Character) {
				o = Byte.valueOf((byte)((Character)o).charValue());
			} else
				return -1;
			for (int i = last - 1; i >= first; i--)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			for (final Object obj : c)
				if (!contains(obj)) return false;
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof List)) return false;
			final ListIterator <?> e1 = listIterator();
			final ListIterator <?> e2 = ((List <?>)obj).listIterator();
			while (e1.hasNext() && e2.hasNext()) {
				final Object o1 = e1.next();
				final Object o2 = e2.next();
				if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
			}
			return !(e1.hasNext() || e2.hasNext());
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			final Iterator <?> i = this.iterator();
			while (i.hasNext()) {
				final Object obj = i.next();
				hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
			}
			return hashCode;
		}

		// -- Polymorphic
		final byte[]	original;

		public ByteList(final byte[] original) {
			this.original = original;
			this.first = 0;
			this.last = original.length - 1;
		}

		public ByteList(final byte[] original, final int first, final int last) {
			this.original = original;
			this.first = first;
			this.last = last;
		}

		@Override
		public Iterator <Byte> iterator() {
			return listIterator();
		}

		@Override
		public Object[] toArray() {
			final Byte[] result = new Byte[last - first + 1];
			int j = -1;
			for (int i = first; i <= last; ++i) {
				result[++j] = original[i];
			}
			return result;
		}

		@Override
		public ListIterator <Byte> listIterator() {
			return new ByteListIter(original, 0, first, last);
		}

		@Override
		public ListIterator <Byte> listIterator(final int index) {
			return new ByteListIter(original, index, first, last);
		}

		@Override
		public List <Byte> subList(final int fromIndex, final int toIndex) {
			return new ByteList(original, fromIndex, toIndex);
		}
	}

	public static class ShortList implements List <Short>, RandomAccess {

		// ++ Unsupported
		@Override
		public boolean add(final Short e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends Short> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection <? extends Short> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final Short element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Short remove(final int index) {
			throw new UnsupportedOperationException();
		}

		// -- Unsupported
		// ++ Polymorphic
		final int	first, last;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("[");
			for (int i = first; i <= last; i++) {
				if (i != first) {
					sb.append(", ");
				}
				sb.append(original[i]);
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public int size() {
			return last - first + 1;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Number) {
				o = Short.valueOf(((Number)o).shortValue());
			} else if (o instanceof Character) {
				o = Short.valueOf((short)((Character)o).charValue());
			} else
				return false;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return true;
			return false;
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			return (T[])Arrays.copyOf(toArray(), last - first + 1, a.getClass());
		}

		@Override
		public Short get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			return original[index + first];
		}

		@Override
		public Short set(final int index, final Short element) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			final short old = original[index + first];
			original[index + first] = element;
			return old;
		}

		@Override
		public int indexOf(Object o) {
			if (o instanceof Number) {
				o = Short.valueOf(((Number)o).shortValue());
			} else if (o instanceof Character) {
				o = Short.valueOf((short)((Character)o).charValue());
			} else
				return -1;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			if (o instanceof Number) {
				o = Short.valueOf(((Number)o).shortValue());
			} else if (o instanceof Character) {
				o = Short.valueOf((short)((Character)o).charValue());
			} else
				return -1;
			for (int i = last - 1; i >= first; i--)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			for (final Object obj : c)
				if (!contains(obj)) return false;
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof List)) return false;
			final ListIterator <?> e1 = listIterator();
			final ListIterator <?> e2 = ((List <?>)obj).listIterator();
			while (e1.hasNext() && e2.hasNext()) {
				final Object o1 = e1.next();
				final Object o2 = e2.next();
				if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
			}
			return !(e1.hasNext() || e2.hasNext());
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			final Iterator <?> i = this.iterator();
			while (i.hasNext()) {
				final Object obj = i.next();
				hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
			}
			return hashCode;
		}

		// -- Polymorphic
		final short[]	original;

		public ShortList(final short[] original) {
			this.original = original;
			this.first = 0;
			this.last = original.length - 1;
		}

		public ShortList(final short[] original, final int first, final int last) {
			this.original = original;
			this.first = first;
			this.last = last;
		}

		@Override
		public Iterator <Short> iterator() {
			return listIterator();
		}

		@Override
		public Object[] toArray() {
			final Short[] result = new Short[last - first + 1];
			int j = -1;
			for (int i = first; i <= last; ++i) {
				result[++j] = original[i];
			}
			return result;
		}

		@Override
		public ListIterator <Short> listIterator() {
			return new ShortListIter(original, 0, first, last);
		}

		@Override
		public ListIterator <Short> listIterator(final int index) {
			return new ShortListIter(original, index, first, last);
		}

		@Override
		public List <Short> subList(final int fromIndex, final int toIndex) {
			return new ShortList(original, fromIndex, toIndex);
		}
	}

	public static class IntegerList implements List <Integer>, RandomAccess {

		// ++ Unsupported
		@Override
		public boolean add(final Integer e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends Integer> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection <? extends Integer> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final Integer element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Integer remove(final int index) {
			throw new UnsupportedOperationException();
		}

		// -- Unsupported
		// ++ Polymorphic
		final int	first, last;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("[");
			for (int i = first; i <= last; i++) {
				if (i != first) {
					sb.append(", ");
				}
				sb.append(original[i]);
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public int size() {
			return last - first + 1;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Number) {
				o = Integer.valueOf(((Number)o).intValue());
			} else if (o instanceof Character) {
				o = Integer.valueOf(((Character)o).charValue());
			} else
				return false;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return true;
			return false;
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			return (T[])Arrays.copyOf(toArray(), last - first + 1, a.getClass());
		}

		@Override
		public Integer get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			return original[index + first];
		}

		@Override
		public Integer set(final int index, final Integer element) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			final int old = original[index + first];
			original[index + first] = element;
			return old;
		}

		@Override
		public int indexOf(Object o) {
			if (o instanceof Number) {
				o = Integer.valueOf(((Number)o).intValue());
			} else if (o instanceof Character) {
				o = Integer.valueOf(((Character)o).charValue());
			} else
				return -1;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			if (o instanceof Number) {
				o = Integer.valueOf(((Number)o).intValue());
			} else if (o instanceof Character) {
				o = Integer.valueOf(((Character)o).charValue());
			} else
				return -1;
			for (int i = last - 1; i >= first; i--)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			for (final Object obj : c)
				if (!contains(obj)) return false;
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof List)) return false;
			final ListIterator <?> e1 = listIterator();
			final ListIterator <?> e2 = ((List <?>)obj).listIterator();
			while (e1.hasNext() && e2.hasNext()) {
				final Object o1 = e1.next();
				final Object o2 = e2.next();
				if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
			}
			return !(e1.hasNext() || e2.hasNext());
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			final Iterator <?> i = this.iterator();
			while (i.hasNext()) {
				final Object obj = i.next();
				hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
			}
			return hashCode;
		}

		// -- Polymorphic
		final int[]	original;

		public IntegerList(final int[] original) {
			this.original = original;
			this.first = 0;
			this.last = original.length - 1;
		}

		public IntegerList(final int[] original, final int first, final int last) {
			this.original = original;
			this.first = first;
			this.last = last;
		}

		@Override
		public Iterator <Integer> iterator() {
			return listIterator();
		}

		@Override
		public Object[] toArray() {
			final Integer[] result = new Integer[last - first + 1];
			int j = -1;
			for (int i = first; i <= last; ++i) {
				result[++j] = original[i];
			}
			return result;
		}

		@Override
		public ListIterator <Integer> listIterator() {
			return new IntegerListIter(original, 0, first, last);
		}

		@Override
		public ListIterator <Integer> listIterator(final int index) {
			return new IntegerListIter(original, index, first, last);
		}

		@Override
		public List <Integer> subList(final int fromIndex, final int toIndex) {
			return new IntegerList(original, fromIndex, toIndex);
		}
	}

	public static class LongList implements List <Long>, RandomAccess {

		// ++ Unsupported
		@Override
		public boolean add(final Long e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends Long> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection <? extends Long> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final Long element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Long remove(final int index) {
			throw new UnsupportedOperationException();
		}

		// -- Unsupported
		// ++ Polymorphic
		final int	first, last;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("[");
			for (int i = first; i <= last; i++) {
				if (i != first) {
					sb.append(", ");
				}
				sb.append(original[i]);
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public int size() {
			return last - first + 1;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Number) {
				o = Long.valueOf(((Number)o).longValue());
			} else if (o instanceof Character) {
				o = Long.valueOf(((Character)o).charValue());
			} else
				return false;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return true;
			return false;
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			return (T[])Arrays.copyOf(toArray(), last - first + 1, a.getClass());
		}

		@Override
		public Long get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			return original[index + first];
		}

		@Override
		public Long set(final int index, final Long element) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			final long old = original[index + first];
			original[index + first] = element;
			return old;
		}

		@Override
		public int indexOf(Object o) {
			if (o instanceof Number) {
				o = Long.valueOf(((Number)o).longValue());
			} else if (o instanceof Character) {
				o = Long.valueOf(((Character)o).charValue());
			} else
				return -1;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			if (o instanceof Number) {
				o = Long.valueOf(((Number)o).longValue());
			} else if (o instanceof Character) {
				o = Long.valueOf(((Character)o).charValue());
			} else
				return -1;
			for (int i = last - 1; i >= first; i--)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			for (final Object obj : c)
				if (!contains(obj)) return false;
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof List)) return false;
			final ListIterator <?> e1 = listIterator();
			final ListIterator <?> e2 = ((List <?>)obj).listIterator();
			while (e1.hasNext() && e2.hasNext()) {
				final Object o1 = e1.next();
				final Object o2 = e2.next();
				if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
			}
			return !(e1.hasNext() || e2.hasNext());
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			final Iterator <?> i = this.iterator();
			while (i.hasNext()) {
				final Object obj = i.next();
				hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
			}
			return hashCode;
		}

		// -- Polymorphic
		final long[]	original;

		public LongList(final long[] original) {
			this.original = original;
			this.first = 0;
			this.last = original.length - 1;
		}

		public LongList(final long[] original, final int first, final int last) {
			this.original = original;
			this.first = first;
			this.last = last;
		}

		@Override
		public Iterator <Long> iterator() {
			return listIterator();
		}

		@Override
		public Object[] toArray() {
			final Long[] result = new Long[last - first + 1];
			int j = -1;
			for (int i = first; i <= last; ++i) {
				result[++j] = original[i];
			}
			return result;
		}

		@Override
		public ListIterator <Long> listIterator() {
			return new LongListIter(original, 0, first, last);
		}

		@Override
		public ListIterator <Long> listIterator(final int index) {
			return new LongListIter(original, index, first, last);
		}

		@Override
		public List <Long> subList(final int fromIndex, final int toIndex) {
			return new LongList(original, fromIndex, toIndex);
		}
	}

	public static class FloatList implements List <Float>, RandomAccess {

		// ++ Unsupported
		@Override
		public boolean add(final Float e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends Float> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection <? extends Float> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final Float element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Float remove(final int index) {
			throw new UnsupportedOperationException();
		}

		// -- Unsupported
		// ++ Polymorphic
		final int	first, last;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("[");
			for (int i = first; i <= last; i++) {
				if (i != first) {
					sb.append(", ");
				}
				sb.append(original[i]);
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public int size() {
			return last - first + 1;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Number) {
				o = Float.valueOf(((Number)o).floatValue());
			} else if (o instanceof Character) {
				o = Float.valueOf(((Character)o).charValue());
			} else
				return false;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return true;
			return false;
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			return (T[])Arrays.copyOf(toArray(), last - first + 1, a.getClass());
		}

		@Override
		public Float get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			return original[index + first];
		}

		@Override
		public Float set(final int index, final Float element) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			final float old = original[index + first];
			original[index + first] = element;
			return old;
		}

		@Override
		public int indexOf(Object o) {
			if (o instanceof Number) {
				o = new Float(((Number)o).floatValue());
			} else if (o instanceof Character) {
				o = new Float(((Character)o).charValue());
			} else
				return -1;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			if (o instanceof Number) {
				o = new Float(((Number)o).floatValue());
			} else if (o instanceof Character) {
				o = new Float(((Character)o).charValue());
			} else
				return -1;
			for (int i = last - 1; i >= first; i--)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			for (final Object obj : c)
				if (!contains(obj)) return false;
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof List)) return false;
			final ListIterator <?> e1 = listIterator();
			final ListIterator <?> e2 = ((List <?>)obj).listIterator();
			while (e1.hasNext() && e2.hasNext()) {
				final Object o1 = e1.next();
				final Object o2 = e2.next();
				if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
			}
			return !(e1.hasNext() || e2.hasNext());
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			final Iterator <?> i = this.iterator();
			while (i.hasNext()) {
				final Object obj = i.next();
				hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
			}
			return hashCode;
		}

		// -- Polymorphic
		final float[]	original;

		public FloatList(final float[] original) {
			this.original = original;
			this.first = 0;
			this.last = original.length - 1;
		}

		public FloatList(final float[] original, final int first, final int last) {
			this.original = original;
			this.first = first;
			this.last = last;
		}

		@Override
		public Iterator <Float> iterator() {
			return listIterator();
		}

		@Override
		public Object[] toArray() {
			final Float[] result = new Float[last - first + 1];
			int j = -1;
			for (int i = first; i <= last; ++i) {
				result[++j] = original[i];
			}
			return result;
		}

		@Override
		public ListIterator <Float> listIterator() {
			return new FloatListIter(original, 0, first, last);
		}

		@Override
		public ListIterator <Float> listIterator(final int index) {
			return new FloatListIter(original, index, first, last);
		}

		@Override
		public List <Float> subList(final int fromIndex, final int toIndex) {
			return new FloatList(original, fromIndex, toIndex);
		}
	}

	public static class DoubleList implements List <Double>, RandomAccess {

		// ++ Unsupported
		@Override
		public boolean add(final Double e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends Double> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection <? extends Double> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final Double element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Double remove(final int index) {
			throw new UnsupportedOperationException();
		}

		// -- Unsupported
		// ++ Polymorphic
		final int	first, last;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("[");
			for (int i = first; i <= last; i++) {
				if (i != first) {
					sb.append(", ");
				}
				sb.append(original[i]);
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public int size() {
			return last - first + 1;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Number) {
				o = Double.valueOf(((Number)o).doubleValue());
			} else if (o instanceof Character) {
				o = Double.valueOf(((Character)o).charValue());
			} else
				return false;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return true;
			return false;
		}

		@Override
		public <T>T[] toArray(final T[] a) {
			return (T[])Arrays.copyOf(toArray(), last - first + 1, a.getClass());
		}

		@Override
		public Double get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			return original[index + first];
		}

		@Override
		public Double set(final int index, final Double element) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			final double old = original[index + first];
			original[index + first] = element;
			return old;
		}

		@Override
		public int indexOf(Object o) {
			if (o instanceof Number) {
				o = new Double(((Number)o).doubleValue());
			} else if (o instanceof Character) {
				o = new Double(((Character)o).charValue());
			} else
				return -1;
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			if (o instanceof Number) {
				o = new Double(((Number)o).doubleValue());
			} else if (o instanceof Character) {
				o = new Double(((Character)o).charValue());
			} else
				return -1;
			for (int i = last - 1; i >= first; i--)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			for (final Object obj : c)
				if (!contains(obj)) return false;
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof List)) return false;
			final ListIterator <?> e1 = listIterator();
			final ListIterator <?> e2 = ((List <?>)obj).listIterator();
			while (e1.hasNext() && e2.hasNext()) {
				final Object o1 = e1.next();
				final Object o2 = e2.next();
				if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
			}
			return !(e1.hasNext() || e2.hasNext());
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			final Iterator <?> i = this.iterator();
			while (i.hasNext()) {
				final Object obj = i.next();
				hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
			}
			return hashCode;
		}

		// -- Polymorphic
		final double[]	original;

		public DoubleList(final double[] original) {
			this.original = original;
			this.first = 0;
			this.last = original.length - 1;
		}

		public DoubleList(final double[] original, final int first, final int last) {
			this.original = original;
			this.first = first;
			this.last = last;
		}

		@Override
		public Iterator <Double> iterator() {
			return listIterator();
		}

		@Override
		public Object[] toArray() {
			final Double[] result = new Double[last - first + 1];
			int j = -1;
			for (int i = first; i <= last; ++i) {
				result[++j] = original[i];
			}
			return result;
		}

		@Override
		public ListIterator <Double> listIterator() {
			return new DoubleListIter(original, 0, first, last);
		}

		@Override
		public ListIterator <Double> listIterator(final int index) {
			return new DoubleListIter(original, index, first, last);
		}

		@Override
		public List <Double> subList(final int fromIndex, final int toIndex) {
			return new DoubleList(original, fromIndex, toIndex);
		}
	}

	public static class ObjectList<T> implements List <T>, RandomAccess {

		// ++ Unsupported
		@Override
		public boolean add(final Object e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection <? extends T> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection <? extends T> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection <?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final T element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public T remove(final int index) {
			throw new UnsupportedOperationException();
		}

		// -- Unsupported
		// ++ Polymorphic
		final int	first, last;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("[");
			for (int i = first; i <= last; i++) {
				if (i != first) {
					sb.append(", ");
				}
				sb.append(original[i]);
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public int size() {
			return last - first + 1;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public boolean contains(final Object o) {
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return true;
			return false;
		}

		@Override
		public <U>U[] toArray(final U[] a) {
			return (U[])Arrays.copyOf(toArray(), last - first + 1, a.getClass());
		}

		@Override
		public T get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			return original[index + first];
		}

		@Override
		public T set(final int index, final T element) {
			if (index < 0) throw new IndexOutOfBoundsException(index + " < 0");
			if (index > last - first) throw new IndexOutOfBoundsException(index + " > " + (last - first));
			final T old = original[index + first];
			original[index + first] = element;
			return old;
		}

		@Override
		public int indexOf(final Object o) {
			for (int i = first; i <= last; i++)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public int lastIndexOf(final Object o) {
			for (int i = last - 1; i >= first; i--)
				if (o.equals(original[i])) return i - first;
			return -1;
		}

		@Override
		public boolean containsAll(final Collection <?> c) {
			for (final Object obj : c)
				if (!contains(obj)) return false;
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof List)) return false;
			final ListIterator <?> e1 = listIterator();
			final ListIterator <?> e2 = ((List <?>)obj).listIterator();
			while (e1.hasNext() && e2.hasNext()) {
				final Object o1 = e1.next();
				final Object o2 = e2.next();
				if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
			}
			return !(e1.hasNext() || e2.hasNext());
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			final Iterator <?> i = this.iterator();
			while (i.hasNext()) {
				final Object obj = i.next();
				hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
			}
			return hashCode;
		}

		// -- Polymorphic
		final T[]	original;

		public ObjectList(final T[] original) {
			this.original = original;
			this.first = 0;
			this.last = original.length - 1;
		}

		public ObjectList(final T[] original, final int first, final int last) {
			this.original = original;
			this.first = first;
			this.last = last;
		}

		@Override
		public Iterator <T> iterator() {
			return listIterator();
		}

		@Override
		public Object[] toArray() {
			final T[] result = (T[])Array.newInstance(original.getClass(), last - first + 1);
			int j = -1;
			for (int i = first; i <= last; ++i) {
				result[++j] = original[i];
			}
			return result;
		}

		@Override
		public ListIterator <T> listIterator() {
			return new ObjectListIter <T>(original, 0, first, last);
		}

		@Override
		public ListIterator <T> listIterator(final int index) {
			return new ObjectListIter <T>(original, index, first, last);
		}

		@Override
		public List <T> subList(final int fromIndex, final int toIndex) {
			return new ObjectList <T>(original, fromIndex, toIndex);
		}
	}

	//
	// ListIterator
	//
	public static class BooleanListIter implements ListIterator <Boolean> {

		private final boolean[]	original;
		private int				pos		= -1;
		private int				first	= -1;
		private int				last	= -1;

		public BooleanListIter(final boolean[] original, final int pos, final int first, final int last) {
			this.original = original;
			this.pos = pos - 1;
			this.first = first;
			this.last = last;
		}

		@Override
		public boolean hasNext() {
			return pos < last;
		}

		@Override
		public boolean hasPrevious() {
			return pos > first;
		}

		@Override
		public Boolean next() {
			if (!hasNext()) throw new NoSuchElementException();
			return original[++pos];
		}

		@Override
		public Boolean previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return original[--pos];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final Boolean e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int nextIndex() {
			return pos - first + 1;
		}

		@Override
		public int previousIndex() {
			return pos - first - 1;
		}

		@Override
		public void set(final Boolean e) {
			original[pos] = e;
		}
	}

	public static class CharListIter implements ListIterator <Character> {

		private final char[]	original;
		private int				pos		= -1;
		private int				first	= -1;
		private int				last	= -1;

		public CharListIter(final char[] original, final int pos, final int first, final int last) {
			this.original = original;
			this.pos = pos - 1;
			this.first = first;
			this.last = last;
		}

		@Override
		public boolean hasNext() {
			return pos < last;
		}

		@Override
		public boolean hasPrevious() {
			return pos > first;
		}

		@Override
		public Character next() {
			if (!hasNext()) throw new NoSuchElementException();
			return original[++pos];
		}

		@Override
		public Character previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return original[--pos];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final Character e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int nextIndex() {
			return pos - first + 1;
		}

		@Override
		public int previousIndex() {
			return pos - first - 1;
		}

		@Override
		public void set(final Character e) {
			original[pos] = e;
		}
	}

	public static class ByteListIter implements ListIterator <Byte> {

		private final byte[]	original;
		private int				pos		= -1;
		private int				first	= -1;
		private int				last	= -1;

		public ByteListIter(final byte[] original, final int pos, final int first, final int last) {
			this.original = original;
			this.pos = pos - 1;
			this.first = first;
			this.last = last;
		}

		@Override
		public boolean hasNext() {
			return pos < last;
		}

		@Override
		public boolean hasPrevious() {
			return pos > first;
		}

		@Override
		public Byte next() {
			if (!hasNext()) throw new NoSuchElementException();
			return original[++pos];
		}

		@Override
		public Byte previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return original[--pos];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final Byte e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int nextIndex() {
			return pos - first + 1;
		}

		@Override
		public int previousIndex() {
			return pos - first - 1;
		}

		@Override
		public void set(final Byte e) {
			original[pos] = e;
		}
	}

	public static class ShortListIter implements ListIterator <Short> {

		private final short[]	original;
		private int				pos		= -1;
		private int				first	= -1;
		private int				last	= -1;

		public ShortListIter(final short[] original, final int pos, final int first, final int last) {
			this.original = original;
			this.pos = pos - 1;
			this.first = first;
			this.last = last;
		}

		@Override
		public boolean hasNext() {
			return pos < last;
		}

		@Override
		public boolean hasPrevious() {
			return pos > first;
		}

		@Override
		public Short next() {
			if (!hasNext()) throw new NoSuchElementException();
			return original[++pos];
		}

		@Override
		public Short previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return original[--pos];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final Short e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int nextIndex() {
			return pos - first + 1;
		}

		@Override
		public int previousIndex() {
			return pos - first - 1;
		}

		@Override
		public void set(final Short e) {
			original[pos] = e;
		}
	}

	public static class IntegerListIter implements ListIterator <Integer> {

		private final int[]	original;
		private int			pos		= -1;
		private int			first	= -1;
		private int			last	= -1;

		public IntegerListIter(final int[] original, final int pos, final int first, final int last) {
			this.original = original;
			this.pos = pos - 1;
			this.first = first;
			this.last = last;
		}

		@Override
		public boolean hasNext() {
			return pos < last;
		}

		@Override
		public boolean hasPrevious() {
			return pos > first;
		}

		@Override
		public Integer next() {
			if (!hasNext()) throw new NoSuchElementException();
			return original[++pos];
		}

		@Override
		public Integer previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return original[--pos];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final Integer e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int nextIndex() {
			return pos - first + 1;
		}

		@Override
		public int previousIndex() {
			return pos - first - 1;
		}

		@Override
		public void set(final Integer e) {
			original[pos] = e;
		}
	}

	public static class LongListIter implements ListIterator <Long> {

		private final long[]	original;
		private int				pos		= -1;
		private int				first	= -1;
		private int				last	= -1;

		public LongListIter(final long[] original, final int pos, final int first, final int last) {
			this.original = original;
			this.pos = pos - 1;
			this.first = first;
			this.last = last;
		}

		@Override
		public Long next() {
			if (!hasNext()) throw new NoSuchElementException();
			return original[++pos];
		}

		@Override
		public Long previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return original[--pos];
		}

		@Override
		public void set(final Long e) {
			original[pos] = e;
		}

		@Override
		public void add(final Long e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return pos < last;
		}

		@Override
		public boolean hasPrevious() {
			return pos > first;
		}

		@Override
		public int nextIndex() {
			return pos - first + 1;
		}

		@Override
		public int previousIndex() {
			return pos - first - 1;
		}
	}

	public static class FloatListIter implements ListIterator <Float> {

		private final float[]	original;
		private int				pos		= -1;
		private int				first	= -1;
		private int				last	= -1;

		public FloatListIter(final float[] original, final int pos, final int first, final int last) {
			this.original = original;
			this.pos = pos - 1;
			this.first = first;
			this.last = last;
		}

		@Override
		public Float next() {
			if (!hasNext()) throw new NoSuchElementException();
			return original[++pos];
		}

		@Override
		public Float previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return original[--pos];
		}

		@Override
		public void set(final Float e) {
			original[pos] = e;
		}

		@Override
		public void add(final Float e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return pos < last;
		}

		@Override
		public boolean hasPrevious() {
			return pos > first;
		}

		@Override
		public int nextIndex() {
			return pos - first + 1;
		}

		@Override
		public int previousIndex() {
			return pos - first - 1;
		}
	}

	public static class DoubleListIter implements ListIterator <Double> {

		private final double[]	original;
		private int				pos		= -1;
		private int				first	= -1;
		private int				last	= -1;

		public DoubleListIter(final double[] original, final int pos, final int first, final int last) {
			this.original = original;
			this.pos = pos - 1;
			this.first = first;
			this.last = last;
		}

		@Override
		public Double next() {
			if (!hasNext()) throw new NoSuchElementException();
			return original[++pos];
		}

		@Override
		public Double previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return original[--pos];
		}

		@Override
		public void set(final Double e) {
			original[pos] = e;
		}

		@Override
		public void add(final Double e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return pos < last;
		}

		@Override
		public boolean hasPrevious() {
			return pos > first;
		}

		@Override
		public int nextIndex() {
			return pos - first + 1;
		}

		@Override
		public int previousIndex() {
			return pos - first - 1;
		}
	}

	private static class ObjectListIter<T> implements ListIterator <T> {

		private final T[]	original;
		private int			pos		= -1;
		private int			first	= -1;
		private int			last	= -1;

		public ObjectListIter(final T[] original, final int pos, final int first, final int last) {
			this.original = original;
			this.pos = pos - 1;
			this.first = first;
			this.last = last;
		}

		@Override
		public T next() {
			if (!hasNext()) throw new NoSuchElementException();
			return original[++pos];
		}

		@Override
		public T previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return original[--pos];
		}

		@Override
		public void set(final T e) {
			original[pos] = e;
		}

		@Override
		public void add(final Object e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return pos < last;
		}

		@Override
		public boolean hasPrevious() {
			return pos > first;
		}

		@Override
		public int nextIndex() {
			return pos - first + 1;
		}

		@Override
		public int previousIndex() {
			return pos - first - 1;
		}
	}
}

