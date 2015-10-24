package org.fs.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class MathUtils {

	//
	// Math/min over collection
	//
	/**
	 * Searches for an element with a maximum long value.
	 *
	 * @param coll
	 *            collection of numbers, may include {@code null}s.
	 * @return maximum long value, or {@link Long#MIN_VALUE} if collection was empty or full of
	 *         {@code null}s.
	 */
	public static long maxLong(final Iterable <? extends Number> coll){
		ObjectUtils.requireNonNull(coll, "collection");
		long result = Long.MIN_VALUE;
		for (final Number value : coll) {
			if (value == null) {
				continue;
			}
			if (result == Long.MIN_VALUE) {
				result = value.longValue();
			} else {
				result = Math.max(result, value.longValue());
			}
		}
		return result;
	}

	/**
	 * Searches for an element with a minimum long value.
	 *
	 * @param coll
	 *            collection of numbers, may include {@code null}s.
	 * @return minimum long value, or {@link Long#MAX_VALUE} if collection was empty or full of
	 *         {@code null}s.
	 */
	public static long minLong(final Iterable <? extends Number> coll){
		ObjectUtils.requireNonNull(coll, "collection");
		long result = Long.MAX_VALUE;
		for (final Number value : coll) {
			if (value == null) {
				continue;
			}
			if (result == Long.MAX_VALUE) {
				result = value.longValue();
			} else {
				result = Math.min(result, value.longValue());
			}
		}
		return result;
	}

	/**
	 * Searches for an element with a maximum double value.
	 *
	 * @param coll
	 *            collection of numbers, may include {@code null}s.
	 * @return maximum double value, or {@link Double#NaN} if collection was empty or full of
	 *         {@code null}s.
	 */
	public static double maxDouble(final Iterable <? extends Number> coll){
		ObjectUtils.requireNonNull(coll, "collection");
		double result = Double.NaN;
		for (final Number value : coll) {
			if (value == null) {
				continue;
			}
			if (Double.isNaN(result)) {
				result = value.doubleValue();
			} else {
				result = Math.max(result, value.doubleValue());
			}
		}
		return result;
	}

	/**
	 * Searches for an element with a minimum double value.
	 *
	 * @param coll
	 *            collection of numbers, may include {@code null}s.
	 * @return minimum double value, or {@link Double#NaN} if collection was empty or full of
	 *         {@code null}s.
	 */
	public static double minDouble(final Iterable <? extends Number> coll){
		ObjectUtils.requireNonNull(coll, "collection");
		double result = Double.NaN;
		for (final Number value : coll) {
			if (value == null) {
				continue;
			}
			if (Double.isNaN(result)) {
				result = value.doubleValue();
			} else {
				result = Math.min(result, value.doubleValue());
			}
		}
		return result;
	}

	//
	// Collection summation
	//
	/**
	 * Sums all array elements.
	 *
	 * @param array
	 *            array to be summed.
	 * @return long sum
	 */
	public static long sum(final byte[] array){
		long sum = 0;
		for (final byte value : array) {
			sum += value;
		}
		return sum;
	}

	/**
	 * Sums all array elements.
	 *
	 * @param array
	 *            array to be summed.
	 * @return long sum
	 */
	public static long sum(final short[] array){
		long sum = 0;
		for (final short value : array) {
			sum += value;
		}
		return sum;
	}

	/**
	 * Sums all array elements.
	 *
	 * @param array
	 *            array to be summed.
	 * @return long sum
	 */
	public static long sum(final int[] array){
		long sum = 0;
		for (final int value : array) {
			sum += value;
		}
		return sum;
	}

	/**
	 * Sums all array elements, checking sum for overflow.
	 *
	 * @param array
	 *            array to be summed.
	 * @return sum
	 */
	public static long sum(final long[] array){
		return sum(array, true);
	}

	/**
	 * Sums all array elements.
	 *
	 * @param array
	 *            array to be summed.
	 * @param checkOverflow
	 *            whether or not check the overflow. Does NOT tries to be clever and avoid it if
	 *            possible.
	 * @return sum
	 */
	public static long sum(final long[] array, final boolean checkOverflow){
		long sum = 0;
		for (final long value : array) {
			if (checkOverflow && isSumOverflow(sum, value)) throw new ArithmeticException("Overflow");
			sum += value;
		}
		return sum;
	}

	/**
	 * Sums all array elements.
	 *
	 * @param array
	 *            array to be summed.
	 * @return double sum
	 */
	public static double sum(final float[] array){
		double sum = 0;
		for (final float value : array) {
			sum += value;
		}
		return sum;
	}

	/**
	 * Sums all array elements.
	 *
	 * @param array
	 *            array to be summed.
	 * @return double sum
	 */
	public static double sum(final double[] array){
		double sum = 0;
		for (final double value : array) {
			sum += value;
		}
		return sum;
	}

	/**
	 * Sums long values of all non-{@code null} element in the collection.
	 *
	 * @param collection
	 *            collection of number objects (may include {@code null}'s)
	 * @param checkOverflow
	 *            whether or not check the overflow. If do, try carefully to sum up without causing
	 *            it.
	 * @return sum
	 */
	public static long sumInt(final Iterable <? extends Number> collection, final boolean checkOverflow){
		if (!checkOverflow) return sumInt(collection);
		final ArrayList <Long> sums = new ArrayList <Long>(1);
		long sum = 0;
		for (final Number value : collection) {
			if (value == null) {
				continue;
			}
			if (isSumOverflow(sum, value.longValue())) {
				sums.add(sum);
				sum = 0;
			}
			sum += value.longValue();
		}
		sums.add(sum);
		//
		outer: while (sums.size() > 1) {
			sum = sums.get(0);
			for (int i = 1; i < sums.size(); ++i) {
				final long curr = sums.get(i);
				if (!isSumOverflow(sum, curr)) {
					sums.set(0, sum + curr);
					sums.remove(i);
					continue outer;
				}
			}
			throw new ArithmeticException("Overflow");
		}
		//
		return sums.get(0);
	}

	/**
	 * Sums long values of all non-{@code null} element in the collection, checking sum for
	 * overflow.
	 *
	 * @param collection
	 *            collection of number objects (may include {@code null}'s)
	 * @return sum
	 */
	public static long sumInt(final Iterable <? extends Number> collection){
		long sum = 0;
		for (final Number value : collection) {
			if (value == null) {
				continue;
			}
			sum += value.longValue();
		}
		return sum;
	}

	/**
	 * Sums double values of all non-{@code null} element in the collection.
	 *
	 * @param collection
	 *            collection of number objects (may include {@code null}'s)
	 * @return sum
	 */
	public static double sumDouble(final Iterable <? extends Number> collection){
		double sum = 0;
		for (final Number value : collection) {
			if (value == null) {
				continue;
			}
			sum += value.doubleValue();
		}
		return sum;
	}

	/**
	 * Sums all non-{@code null} BigIntegers in the collection.
	 *
	 * @param collection
	 *            collection of {@code BigInteger}s (may include {@code null}'s)
	 * @return sum
	 */
	public static BigInteger sumBig(final Iterable <? extends BigInteger> collection){
		BigInteger sum = BigInteger.ZERO;
		for (final BigInteger value : collection) {
			if (value == null) {
				continue;
			}
			sum = sum.add(value);
		}
		return sum;
	}

	/**
	 * Sums all non-{@code null} BigDecimals in the collection.
	 *
	 * @param collection
	 *            collection of {@code BigDecimal}s (may include {@code null}'s)
	 * @return sum
	 */
	public static BigDecimal sumDec(final Iterable <? extends BigDecimal> collection){
		BigDecimal sum = BigDecimal.ZERO;
		for (final BigDecimal value : collection) {
			if (value == null) {
				continue;
			}
			sum = sum.add(value);
		}
		return sum;
	}

	//
	// Primitive helpers
	//
	/** @return {@code true} if the sum of two long values will cause long type overflow. */
	@SuppressWarnings("javadoc")
	public static boolean isSumOverflow(final long a, final long b){
		// Opposite signs cannot overflow, same for x + 0
		if (Long.signum(a) != Long.signum(b)) return false;
		// Make symmetric (let a <= b)
		if (a > b) return isSumOverflow(b, a);
		// Negative overflow?
		if (a < 0) return Long.MIN_VALUE - b > a;
		// Positive overflow?
		return Long.MAX_VALUE - b < a;
	}

	/** @return {@code true} if the multiplication of two long values will cause long type overflow. */
	@SuppressWarnings("javadoc")
	public static boolean isMulOverflow(final long a, final long b){
		// x*0 or x*1 cannot overflow
		if (a == 0L || a == 1L || b == 0L || b == 1L) return false;
		// Make symmetric (let a <= b)
		if (a > b) return isMulOverflow(b, a);
		// Negative overflow?
		if (a < 0) {
			// Special case: a is Long.MIN_VALUE and b isnt 0 or 1, the overflow will happen anyway
			// (because Long.MIN_VALUE == (-Long.MAX_VALUE - 1))
			if (a == Long.MIN_VALUE) return true;
			return a < Long.MIN_VALUE / b;
		}
		// Positive overflow?
		return a > Long.MAX_VALUE / b;
	}
}
