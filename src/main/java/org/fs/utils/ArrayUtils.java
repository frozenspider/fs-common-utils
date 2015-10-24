package org.fs.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.fs.utils.collection.list.ListArrays;

/**
 * Some utility methods in respect to primitive arrays
 * 
 * @see List#indexOf(Object)
 * @see Arrays#asList(Object...)
 * @author FS
 */
public class ArrayUtils {
	
	//
	// Boolean
	//
	public static void swap(final boolean[] array, final int i, final int j) {
		if (i == j) return;
		final boolean tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	public static void rotate(final boolean[] array, int distance) {
		// Implementation taken from Collections.rotate(...)
		final int size = array.length;
		if (size == 0) return;
		distance = distance % size;
		if (distance < 0) {
			distance += size;
		}
		if (distance == 0) return;
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			boolean displaced = array[cycleStart];
			boolean temp;
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size) {
					i -= size;
				}
				temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved++;
			} while (i != cycleStart);
		}
	}
	
	public static int indexOf(final boolean[] array, final boolean toFind) {
		final int len = array.length;
		for (int i = 0; i < len; i++)
			if (array[i] == toFind) return i;
		return -1;
	}
	
	public static int indexOf(final boolean[] array, final boolean[] toFind) {
		final int len1 = array.length;
		final int len2 = toFind.length;
		final int seekTo = len1 - len2 + 1;
		if (len2 > len1) return -1;
		outer: for (int i = 0; i < seekTo; i++) {
			for (int j = 0; j < len2; ++j) {
				if (array[i + j] != toFind[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}
	
	public static boolean hasCommonElements(final boolean[] array1, final boolean[] array2) {
		for (int i = 0; i < array2.length; i++) {
			if (indexOf(array1, array2[i]) != -1) return true;
		}
		return false;
	}
	
	public static List <Boolean> asList(final boolean[] array) {
		return new ListArrays.BooleanList(array);
	}
	
	public static boolean[] toArrayBoolean(final List <Boolean> list) {
		final boolean[] result = new boolean[list.size()];
		final Iterator <Boolean> iter = list.iterator();
		final int len = result.length;
		for (int i = 0; i < len; ++i) {
			result[i] = iter.next();
		}
		return result;
	}
	
	//
	// Char
	//
	public static void swap(final char[] array, final int i, final int j) {
		if (i == j) return;
		final char tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	public static void rotate(final char[] array, int distance) {
		// Implementation taken from Collections.rotate(...)
		final int size = array.length;
		if (size == 0) return;
		distance = distance % size;
		if (distance < 0) {
			distance += size;
		}
		if (distance == 0) return;
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			char displaced = array[cycleStart];
			char temp;
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size) {
					i -= size;
				}
				temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved++;
			} while (i != cycleStart);
		}
	}
	
	public static int indexOf(final char[] array, final char toFind) {
		final int len = array.length;
		for (int i = 0; i < len; i++)
			if (array[i] == toFind) return i;
		return -1;
	}
	
	public static int indexOf(final char[] array, final char[] toFind) {
		final int len1 = array.length;
		final int len2 = toFind.length;
		final int seekTo = len1 - len2 + 1;
		if (len2 > len1) return -1;
		outer: for (int i = 0; i < seekTo; i++) {
			for (int j = 0; j < len2; ++j) {
				if (array[i + j] != toFind[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}
	
	public static boolean hasCommonElements(final char[] array1, final char[] array2) {
		for (int i = 0; i < array2.length; i++) {
			if (indexOf(array1, array2[i]) != -1) return true;
		}
		return false;
	}
	
	public static List <Character> asList(final char[] array) {
		return new ListArrays.CharList(array);
	}
	
	public static char[] toArrayChar(final List <Character> list) {
		final char[] result = new char[list.size()];
		final Iterator <Character> iter = list.iterator();
		final int len = result.length;
		for (int i = 0; i < len; ++i) {
			result[i] = iter.next();
		}
		return result;
	}
	
	public static void replaceAll(char[] src, char from, char to) {
		for (int i = 0; i < src.length; i++) {
			if (src[i] == from) {
				src[i] = to;
			}
		}
	}
	
	//
	// Integers
	//
	public static void reverse(final int[] array) {
		for (int i = array.length / 2 - 1; i >= 0; --i) {
			swap(array, i, array.length - i - 1);
		}
	}
	
	public static int compare(final int[] array1, final int[] array2) {
		int result = ObjectUtils.compare(array1.length, array2.length);
		if (result != 0) return result;
		for (int i = 0; i < array1.length; ++i) {
			result = ObjectUtils.compare(array1[i], array2[i]);
			if (result != 0) return result;
		}
		return 0;
	}
	
	public static void swap(final byte[] array, final int i, final int j) {
		if (i == j) return;
		final byte tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	public static void swap(final short[] array, final int i, final int j) {
		if (i == j) return;
		final short tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	public static void swap(final int[] array, final int i, final int j) {
		if (i == j) return;
		final int tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	public static void swap(final long[] array, final int i, final int j) {
		if (i == j) return;
		final long tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	public static void rotate(final byte[] array, int distance) {
		// Implementation taken from Collections.rotate(...)
		final int size = array.length;
		if (size == 0) return;
		distance = distance % size;
		if (distance < 0) {
			distance += size;
		}
		if (distance == 0) return;
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			byte displaced = array[cycleStart];
			byte temp;
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size) {
					i -= size;
				}
				temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved++;
			} while (i != cycleStart);
		}
	}
	
	public static void rotate(final short[] array, int distance) {
		// Implementation taken from Collections.rotate(...)
		final int size = array.length;
		if (size == 0) return;
		distance = distance % size;
		if (distance < 0) {
			distance += size;
		}
		if (distance == 0) return;
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			short displaced = array[cycleStart];
			short temp;
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size) {
					i -= size;
				}
				temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved++;
			} while (i != cycleStart);
		}
	}
	
	public static void rotate(final int[] array, int distance) {
		// Implementation taken from Collections.rotate(...)
		final int size = array.length;
		if (size == 0) return;
		distance = distance % size;
		if (distance < 0) {
			distance += size;
		}
		if (distance == 0) return;
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			int displaced = array[cycleStart];
			int temp;
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size) {
					i -= size;
				}
				temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved++;
			} while (i != cycleStart);
		}
	}
	
	public static void rotate(final long[] array, int distance) {
		// Implementation taken from Collections.rotate(...)
		final int size = array.length;
		if (size == 0) return;
		distance = distance % size;
		if (distance < 0) {
			distance += size;
		}
		if (distance == 0) return;
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			long displaced = array[cycleStart];
			long temp;
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size) {
					i -= size;
				}
				temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved++;
			} while (i != cycleStart);
		}
	}
	
	public static int indexOf(final byte[] array, final byte toFind) {
		final int len = array.length;
		for (int i = 0; i < len; i++)
			if (array[i] == toFind) return i;
		return -1;
	}
	
	public static int indexOf(final short[] array, final short toFind) {
		final int len = array.length;
		for (int i = 0; i < len; i++)
			if (array[i] == toFind) return i;
		return -1;
	}
	
	public static int indexOf(final int[] array, final int toFind) {
		final int len = array.length;
		for (int i = 0; i < len; i++)
			if (array[i] == toFind) return i;
		return -1;
	}
	
	public static int indexOf(final long[] array, final long toFind) {
		final int len = array.length;
		for (int i = 0; i < len; i++)
			if (array[i] == toFind) return i;
		return -1;
	}
	
	public static int indexOf(final byte[] array, final byte[] toFind) {
		final int len1 = array.length;
		final int len2 = toFind.length;
		final int seekTo = len1 - len2 + 1;
		if (len2 > len1) return -1;
		outer: for (int i = 0; i < seekTo; i++) {
			for (int j = 0; j < len2; ++j) {
				if (array[i + j] != toFind[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}
	
	public static int indexOf(final short[] array, final short[] toFind) {
		final int len1 = array.length;
		final int len2 = toFind.length;
		final int seekTo = len1 - len2 + 1;
		if (len2 > len1) return -1;
		outer: for (int i = 0; i < seekTo; i++) {
			for (int j = 0; j < len2; ++j) {
				if (array[i + j] != toFind[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}
	
	public static int indexOf(final int[] array, final int[] toFind) {
		final int len1 = array.length;
		final int len2 = toFind.length;
		final int seekTo = len1 - len2 + 1;
		if (len2 > len1) return -1;
		outer: for (int i = 0; i < seekTo; i++) {
			for (int j = 0; j < len2; ++j) {
				if (array[i + j] != toFind[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}
	
	public static int indexOf(final long[] array, final long[] toFind) {
		final int len1 = array.length;
		final int len2 = toFind.length;
		final int seekTo = len1 - len2 + 1;
		if (len2 > len1) return -1;
		outer: for (int i = 0; i < seekTo; i++) {
			for (int j = 0; j < len2; ++j) {
				if (array[i + j] != toFind[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}
	
	public static boolean hasCommonElements(final byte[] array1, final byte[] array2) {
		for (int i = 0; i < array2.length; i++) {
			if (indexOf(array1, array2[i]) != -1) return true;
		}
		return false;
	}
	
	public static boolean hasCommonElements(final short[] array1, final short[] array2) {
		for (int i = 0; i < array2.length; i++) {
			if (indexOf(array1, array2[i]) != -1) return true;
		}
		return false;
	}
	
	public static boolean hasCommonElements(final int[] array1, final int[] array2) {
		for (int i = 0; i < array2.length; i++) {
			if (indexOf(array1, array2[i]) != -1) return true;
		}
		return false;
	}
	
	public static boolean hasCommonElements(final long[] array1, final long[] array2) {
		for (int i = 0; i < array2.length; i++) {
			if (indexOf(array1, array2[i]) != -1) return true;
		}
		return false;
	}
	
	public static List <Byte> asList(final byte[] array) {
		return new ListArrays.ByteList(array);
	}
	
	public static List <Short> asList(final short[] array) {
		return new ListArrays.ShortList(array);
	}
	
	public static List <Integer> asList(final int[] array) {
		return new ListArrays.IntegerList(array);
	}
	
	public static List <Long> asList(final long[] array) {
		return new ListArrays.LongList(array);
	}
	
	public static byte[] toArrayByte(final List <Byte> list) {
		final byte[] result = new byte[list.size()];
		final Iterator <Byte> iter = list.iterator();
		final int len = result.length;
		for (int i = 0; i < len; ++i) {
			result[i] = iter.next();
		}
		return result;
	}
	
	public static short[] toArrayShort(final List <Short> list) {
		final short[] result = new short[list.size()];
		final Iterator <Short> iter = list.iterator();
		final int len = result.length;
		for (int i = 0; i < len; ++i) {
			result[i] = iter.next();
		}
		return result;
	}
	
	public static int[] toArrayInteger(final List <Integer> list) {
		final int[] result = new int[list.size()];
		final Iterator <Integer> iter = list.iterator();
		final int len = result.length;
		for (int i = 0; i < len; ++i) {
			result[i] = iter.next();
		}
		return result;
	}
	
	public static long[] toArrayLong(final List <Long> list) {
		final long[] result = new long[list.size()];
		final Iterator <Long> iter = list.iterator();
		final int len = result.length;
		for (int i = 0; i < len; ++i) {
			result[i] = iter.next();
		}
		return result;
	}
	
	public static void replaceAll(byte[] src, byte from, byte to) {
		for (int i = 0; i < src.length; i++) {
			if (src[i] == from) {
				src[i] = to;
			}
		}
	}
	
	public static void replaceAll(short[] src, short from, short to) {
		for (int i = 0; i < src.length; i++) {
			if (src[i] == from) {
				src[i] = to;
			}
		}
	}
	
	public static void replaceAll(int[] src, int from, int to) {
		for (int i = 0; i < src.length; i++) {
			if (src[i] == from) {
				src[i] = to;
			}
		}
	}
	
	public static void replaceAll(long[] src, long from, long to) {
		for (int i = 0; i < src.length; i++) {
			if (src[i] == from) {
				src[i] = to;
			}
		}
	}
	
	//
	// Floating point
	//
	public static void swap(final float[] array, final int i, final int j) {
		if (i == j) return;
		final float tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	public static void swap(final double[] array, final int i, final int j) {
		if (i == j) return;
		final double tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	public static void rotate(final float[] array, int distance) {
		// Implementation taken from Collections.rotate(...)
		final int size = array.length;
		if (size == 0) return;
		distance = distance % size;
		if (distance < 0) {
			distance += size;
		}
		if (distance == 0) return;
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			float displaced = array[cycleStart];
			float temp;
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size) {
					i -= size;
				}
				temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved++;
			} while (i != cycleStart);
		}
	}
	
	public static void rotate(final double[] array, int distance) {
		// Implementation taken from Collections.rotate(...)
		final int size = array.length;
		if (size == 0) return;
		distance = distance % size;
		if (distance < 0) {
			distance += size;
		}
		if (distance == 0) return;
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			double displaced = array[cycleStart];
			double temp;
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size) {
					i -= size;
				}
				temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved++;
			} while (i != cycleStart);
		}
	}
	
	public static int indexOf(final float[] array, final float toFind) {
		final int len = array.length;
		for (int i = 0; i < len; i++)
			if (array[i] == toFind) return i;
		return -1;
	}
	
	public static int indexOf(final double[] array, final double toFind) {
		final int len = array.length;
		for (int i = 0; i < len; i++)
			if (array[i] == toFind) return i;
		return -1;
	}
	
	public static int indexOf(final float[] array, final float[] toFind) {
		final int len1 = array.length;
		final int len2 = toFind.length;
		final int seekTo = len1 - len2 + 1;
		if (len2 > len1) return -1;
		outer: for (int i = 0; i < seekTo; i++) {
			for (int j = 0; j < len2; ++j) {
				if (array[i + j] != toFind[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}
	
	public static int indexOf(final double[] array, final double[] toFind) {
		final int len1 = array.length;
		final int len2 = toFind.length;
		final int seekTo = len1 - len2 + 1;
		if (len2 > len1) return -1;
		outer: for (int i = 0; i < seekTo; i++) {
			for (int j = 0; j < len2; ++j) {
				if (array[i + j] != toFind[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}
	
	public static boolean hasCommonElements(final float[] array1, final float[] array2) {
		for (int i = 0; i < array2.length; i++) {
			if (indexOf(array1, array2[i]) != -1) return true;
		}
		return false;
	}
	
	public static boolean hasCommonElements(final double[] array1, final double[] array2) {
		for (int i = 0; i < array2.length; i++) {
			if (indexOf(array1, array2[i]) != -1) return true;
		}
		return false;
	}
	
	public static List <Float> asList(final float[] array) {
		return new ListArrays.FloatList(array);
	}
	
	public static List <Double> asList(final double[] array) {
		return new ListArrays.DoubleList(array);
	}
	
	public static float[] toArrayFloat(final List <Float> list) {
		final float[] result = new float[list.size()];
		final Iterator <Float> iter = list.iterator();
		final int len = result.length;
		for (int i = 0; i < len; ++i) {
			result[i] = iter.next();
		}
		return result;
	}
	
	public static double[] toArrayDouble(final List <Double> list) {
		final double[] result = new double[list.size()];
		final Iterator <Double> iter = list.iterator();
		final int len = result.length;
		for (int i = 0; i < len; ++i) {
			result[i] = iter.next();
		}
		return result;
	}
	
	public static void replaceAll(float[] src, float from, float to) {
		for (int i = 0; i < src.length; i++) {
			if (src[i] == from) {
				src[i] = to;
			}
		}
	}
	
	public static void replaceAll(double[] src, double from, double to) {
		for (int i = 0; i < src.length; i++) {
			if (src[i] == from) {
				src[i] = to;
			}
		}
	}
	
	//
	// Object
	//
	public static <T>void swap(final T[] array, final int i, final int j) {
		if (i == j) return;
		final T tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	public static <T>void rotate(final T[] array, int distance) {
		// Implementation taken from Collections.rotate(...)
		final int size = array.length;
		if (size == 0) return;
		distance = distance % size;
		if (distance < 0) {
			distance += size;
		}
		if (distance == 0) return;
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			T displaced = array[cycleStart];
			T temp;
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size) {
					i -= size;
				}
				temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved++;
			} while (i != cycleStart);
		}
	}
	
	public static <T>int indexOf(final T[] array, final T toFind) {
		final int len = array.length;
		for (int i = 0; i < len; i++)
			if (ObjectUtils.equals(array[i], toFind)) return i;
		return -1;
	}
	
	public static <T>int indexOf(final T[] array, final T[] toFind) {
		final int len1 = array.length;
		final int len2 = toFind.length;
		final int seekTo = len1 - len2 + 1;
		if (len2 > len1) return -1;
		outer: for (int i = 0; i < seekTo; i++) {
			for (int j = 0; j < len2; ++j) {
				if (!ObjectUtils.equals(array[i + j], toFind[j])) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}
	
	public static <T>boolean hasCommonElements(final T[] array1, final T[] array2) {
		for (int i = 0; i < array2.length; i++) {
			if (indexOf(array1, array2[i]) != -1) return true;
		}
		return false;
	}
	
	public static <T>List <T> asList(final T[] array) {
		return new ListArrays.ObjectList <T>(array);
	}
	
	public static <T>void replaceAll(T[] src, T from, T to) {
		for (int i = 0; i < src.length; i++) {
			if (ObjectUtils.equals(src[i], from)) {
				src[i] = to;
			}
		}
	}
	
}
