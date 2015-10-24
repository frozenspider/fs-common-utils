package org.fs.utils.currency;

import java.math.BigDecimal;

import org.fs.utils.MathUtils;
import org.fs.utils.ObjectUtils;

/**
 * Immutable currency class, that stores values with two decimal points precision.
 * 
 * @author FS
 */
public class Money extends Number implements Comparable <Money> {
	
	private static final long		serialVersionUID	= -6819038788577007427L;
	private static final BigDecimal	zeroPointFive		= BigDecimal.valueOf(5, 1);
	private static final BigDecimal	maxDecimal			= BigDecimal.valueOf(Long.MAX_VALUE);
	private static final BigDecimal	minDecimal			= BigDecimal.valueOf(Long.MIN_VALUE);
	public static final Money		ZERO				= new Money(0);
	/** 92233720368547758.07, (less than 10<sup>18</sup>, more than 2<sup>56</sup>) */
	public static final Money		MAX_VALUE			= new Money(Long.MAX_VALUE);
	/** -92233720368547758.08, (more than -10<sup>18</sup>, less than -2<sup>56</sup>) */
	public static final Money		MIN_VALUE			= new Money(Long.MIN_VALUE);
	
	/**
	 * Returns the exact representation of a decimal string.
	 * <p>
	 * Will throw an exception, if that's not entirely possible.
	 * 
	 * @param string
	 *            String to be represented, e.g. {@code 5}, {@code 7.34}, {@code 2e3}, etc.
	 * @return an immutable representation
	 * @throws NumberFormatException
	 *             if string isn't actually a decimal string, or it is too precise to be represented
	 *             as is
	 * @throws ArithmeticException
	 *             if value is too big or too small to be stored
	 * @see #exact(BigDecimal)
	 * @see #rounded(String)
	 */
	public static Money exact(final String string) throws NumberFormatException,
			ArithmeticException {
		try {
			return exact(new BigDecimal(string));
		} catch(final NumberFormatException ex) {
			throw new NumberFormatException(string); // Why, why it's not diplayed by default?
		}
	}
	
	/**
	 * Returns the exact representation of a decimal value.
	 * <p>
	 * Will throw an exception, if that's not entirely possible.
	 * 
	 * @param value
	 *            decimal to be represented
	 * @return an immutable representation
	 * @throws NumberFormatException
	 *             if decimal is too precise to be represented as is
	 * @throws ArithmeticException
	 *             if value is too big or too small to be stored
	 * @see #exact(String)
	 * @see #rounded(BigDecimal)
	 */
	public static Money exact(final BigDecimal value) throws NumberFormatException,
			ArithmeticException {
		if (value.scale() > 2)
			throw new NumberFormatException("Decimal scale was " + value.scale()
					+ ", but it shouldn't be more than 2");
		if (value.compareTo(maxDecimal) > 0) throw new ArithmeticException("Value is too big");
		if (value.compareTo(minDecimal) < 0) throw new ArithmeticException("Value is too small");
		return rounded(value);
	}
	
	/**
	 * Returns the approximate representation of a decimal string, rounding up to two decimal
	 * places.
	 * <p>
	 * Will throw an exception, if that's not entirely possible.
	 * 
	 * @param value
	 *            String to be represented, e.g. {@code 5}, {@code 7.34}, {@code 2e3}, etc.
	 * @return an immutable representation
	 * @throws NumberFormatException
	 *             if string isn't actually a decimal string
	 * @throws ArithmeticException
	 *             if value is too big or too small to be stored
	 * @see #rounded(BigDecimal)
	 * @see #rounded(double)
	 * @see #exact(String)
	 */
	public static Money rounded(final String value) throws ArithmeticException {
		try {
			return rounded(new BigDecimal(value));
		} catch(final NumberFormatException ex) {
			throw new NumberFormatException(value); // Why, why it's not diplayed by default?
		}
	}
	
	/**
	 * Returns the approximate representation of a decimal value, rounding up to two decimal places.
	 * <p>
	 * Will throw an exception, if that's not entirely possible.
	 * 
	 * @param value
	 *            decimal to be represented
	 * @return an immutable representation
	 * @throws ArithmeticException
	 *             if value is too big or too small to be stored
	 * @see #rounded(String)
	 * @see #rounded(double)
	 * @see #exact(BigDecimal)
	 */
	public static Money rounded(final BigDecimal value) throws ArithmeticException {
		final BigDecimal rounded = value.scaleByPowerOfTen(2);
		// Idiotic way to round, I know, but...
		BigDecimal roundedUp = rounded.abs().add(zeroPointFive).divideToIntegralValue(
				BigDecimal.ONE);
		if (rounded.signum() == -1) {
			roundedUp = roundedUp.negate();
		}
		return unscaled(roundedUp.longValueExact());
	}
	
	/**
	 * Returns the approximate representation of a double, rounding up to two decimal places.
	 * <p>
	 * Will throw an exception, if that's not entirely possible.
	 * 
	 * @param value
	 *            double to be represented
	 * @return an immutable representation
	 * @throws ArithmeticException
	 *             if value is too big or too small to be stored
	 * @see #rounded(String)
	 * @see #rounded(BigDecimal)
	 */
	public static Money rounded(final double value) throws ArithmeticException {
		if (value == 0.0d) return ZERO;
		final double scaled = value * 100.0d;
		if (scaled > Long.MAX_VALUE) throw new ArithmeticException("Value is too big");
		if (scaled < Long.MIN_VALUE) throw new ArithmeticException("Value is too small");
		return new Money(Math.round(scaled));
	}
	
	/**
	 * Returns the representation of given cent amount (1/100<sup>th</sup> part).
	 * <p>
	 * This is the most straightforward way to obtain an instance.
	 * 
	 * @param cents
	 *            cents to be represented
	 * @return an immutable representation
	 */
	public static Money unscaled(final long cents) {
		if (cents == 0) return ZERO;
		return new Money(cents);
	}
	
	/** Amount of "cents", 1/100 */
	private final long	cents;
	
	/**
	 * Non-visible constructor
	 * 
	 * @param cents
	 *            Amount of "cents", 1/100'th of value
	 */
	protected Money(final long cents) {
		this.cents = cents;
	}
	
	//
	// Arithmetic
	//
	public long getCents() {
		return cents;
	}
	
	public Money add(final Money another) throws ArithmeticException, NullPointerException {
		ObjectUtils.requireNonNull(another);
		return addCents(another.getCents());
	}
	
	public Money addCents(final long toAdd) throws ArithmeticException {
		if (MathUtils.isSumOverflow(cents, toAdd)) throw new ArithmeticException("Overflow");
		final Money result = new Money(cents + toAdd);
		return result;
	}
	
	public Money sub(final Money another) throws ArithmeticException, NullPointerException {
		ObjectUtils.requireNonNull(another);
		final long toSub = another.getCents();
		if (toSub == Long.MIN_VALUE) throw new ArithmeticException("Overflow");
		return addCents(-toSub);
	}
	
	public Money subCents(final long toSub) throws ArithmeticException {
		if (toSub == Long.MIN_VALUE) throw new ArithmeticException("Overflow");
		return addCents(-toSub);
	}
	
	public Money multiply(final long value) throws ArithmeticException {
		if (value == 0L) return ZERO;
		if (MathUtils.isMulOverflow(cents, value)) throw new ArithmeticException("Overflow");
		final Money result = new Money(cents * value);
		return result;
	}
	
	public Money multiply(final double value) throws ArithmeticException {
		if (value == 0.0d) return ZERO;
		{
			final double abs = Math.abs(value);
			if (abs > Long.MAX_VALUE //
					|| MathUtils.isMulOverflow(cents, (long) Math.ceil(abs)))
				throw new ArithmeticException("Overflow");
		}
		final Money result = Money.rounded(doubleValue() * value);
		return result;
	}
	
	public Money multiply(final BigDecimal value) throws ArithmeticException, NullPointerException {
		ObjectUtils.requireNonNull(value);
		if (value.compareTo(BigDecimal.ZERO) == 0) return ZERO;
		if (MathUtils.isMulOverflow(cents, (long) Math.ceil(Math.abs(value.doubleValue()))))
			throw new ArithmeticException("Overflow");
		final Money result = Money.rounded(decimalValue().multiply(value));
		return result;
	}
	
	public Money divide(final long value) throws ArithmeticException {
		if (value == 0L) throw new ArithmeticException("Divided by zero");
		final Money result = new Money(cents / value);
		return result;
	}
	
	public Money divide(final double value) throws ArithmeticException {
		if (value == 0.0d) throw new ArithmeticException("Divided by zero");
		final Money result = Money.rounded(doubleValue() / value);
		return result;
	}
	
	public Money divide(final BigDecimal value) throws ArithmeticException, NullPointerException {
		ObjectUtils.requireNonNull(value);
		if (value.compareTo(BigDecimal.ZERO) == 0)
			throw new ArithmeticException("Divided by zero");
		final Money result = Money.rounded(decimalValue().divide(value));
		return result;
	}
	
	//
	// Conversion
	//
	/** Returns the integer part as int-type value */
	@Override
	public int intValue() {
		return (int) (cents / 100);
	}
	
	/** Returns the integer part as long-type value */
	@Override
	public long longValue() {
		return cents / 100;
	}
	
	@Override
	public float floatValue() {
		return cents * 0.01f;
	}
	
	@Override
	public double doubleValue() {
		return cents * 0.01d;
	}
	
	/**
	 * Returns the value as a {@link BigDecimal} with the scale of two, i.e. there will always be
	 * two digits after decimal point.
	 * <p>
	 * That said, {@code 12.3} would be {@code 12.30}, and {@code 512} would be {@code 512.00}.
	 * 
	 * @return money value if decimal form
	 */
	public BigDecimal decimalValue() {
		return BigDecimal.valueOf(cents, 2);
	}
	
	public boolean isZero() {
		return cents == 0L;
	}
	
	public boolean isPositive() {
		return cents > 0L;
	}
	
	public boolean isNegative() {
		return cents < 0L;
	}
	
	/**
	 * @param cents
	 *            amount of cents to compare with
	 * @return whether or not this instance represents the given amount of cents
	 */
	public boolean equalsCents(final long cents) {
		return this.cents == cents;
	}
	
	//
	// Auto-generated and junk
	//
	@Override
	public String toString() {
		return decimalValue().toPlainString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (cents ^ cents >>> 32);
		return result;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Money)) return false;
		final Money other = (Money) obj;
		if (getCents() != other.getCents()) return false;
		return true;
	}
	
	@Override
	public int compareTo(final Money o) {
		ObjectUtils.requireNonNull(o);
		return ObjectUtils.compare(getCents(), o.getCents());
	}
}
