package org.fs.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ListUtils {

	/**
	 * Returns the sublist of a given list, that is independant of the source list.
	 *
	 * @param <E>
	 *            the source list type
	 * @param original
	 *            the original list
	 * @param from
	 *            start index, inclusive
	 * @param to
	 *            end index, exclusive
	 * @return a new {@code ArrayList <E>}
	 */
	public static <E>ArrayList <E> subList(final List <E> original, final int from, final int to) {
		final ArrayList <E> al = new ArrayList <E>(original.subList(from, to));
		return al;
	}

	public static <T extends Number>T inc(final List <T> list, final int idx) {
		final T oldValue = list.get(idx);
		T newValue;
		if (oldValue instanceof Byte) {
			newValue = (T)Byte.valueOf((byte)(oldValue.byteValue() + 1));
		} else if (oldValue instanceof Short) {
			newValue = (T)Short.valueOf((short)(oldValue.shortValue() + 1));
		} else if (oldValue instanceof Integer) {
			newValue = (T)Integer.valueOf(oldValue.intValue() + 1);
		} else if (oldValue instanceof Long) {
			newValue = (T)Long.valueOf(oldValue.longValue() + 1L);
		} else if (oldValue instanceof Float) {
			newValue = (T)Float.valueOf(oldValue.floatValue() + 1.0f);
		} else if (oldValue instanceof Double) {
			newValue = (T)Double.valueOf(oldValue.doubleValue() + 1.0d);
		} else if (oldValue instanceof Double) {
			newValue = (T)Double.valueOf(oldValue.doubleValue() + 1.0d);
		} else if (oldValue instanceof BigInteger) {
			newValue = (T)((BigInteger)oldValue).add(BigInteger.ONE) ;
		} else if (oldValue instanceof BigDecimal) {
			newValue = (T)((BigDecimal)oldValue).add(BigDecimal.ONE) ;
		} else
			throw new IllegalArgumentException(oldValue.getClass().toString());
		list.set(idx, newValue);
		return newValue;
	}

	public static <T extends Number>T dec(final List <T> list, final int idx) {
		final T oldValue = list.get(idx);
		T newValue;
		if (oldValue instanceof Byte) {
			newValue = (T)Byte.valueOf((byte)(oldValue.byteValue() - 1));
		} else if (oldValue instanceof Short) {
			newValue = (T)Short.valueOf((short)(oldValue.shortValue() - 1));
		} else if (oldValue instanceof Integer) {
			newValue = (T)Integer.valueOf(oldValue.intValue() - 1);
		} else if (oldValue instanceof Long) {
			newValue = (T)Long.valueOf(oldValue.longValue() - 1L);
		} else if (oldValue instanceof Float) {
			newValue = (T)Float.valueOf(oldValue.floatValue() - 1.0f);
		} else if (oldValue instanceof Double) {
			newValue = (T)Double.valueOf(oldValue.doubleValue() - 1.0d);
		} else if (oldValue instanceof BigInteger) {
			newValue = (T)((BigInteger)oldValue).subtract(BigInteger.ONE) ;
		} else if (oldValue instanceof BigDecimal) {
			newValue = (T)((BigDecimal)oldValue).subtract(BigDecimal.ONE) ;
		} else
			throw new IllegalArgumentException(oldValue.getClass().toString());
		list.set(idx, newValue);
		return newValue;
	}
}

