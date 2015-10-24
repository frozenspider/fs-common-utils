package org.fs.utils;

import org.fs.utils.structure.wrap.Wrapper;

/**
 * Contains generalization of {@code equals} and {@code hashCode} functions. All functions are
 * {@code null}-safe. There are three type of functions:
 * <p>
 * <ul>
 * <li>Check equality of multiple objects</li>
 * <li>Check equality of a primitive and a wrapper</li>
 * <li>Calculate a hash code, which is 0 for {@code null}</li>
 * </ul>
 * 
 * @author FS
 */
public class ObjectUtils {
	
	/**
	 * Acts like {@link java.util.Objects#requireNonNull(Object)}, but throws
	 * {@code IllegalArgumentException} instead of {@code NullPointerException}
	 * <p>
	 * Message is
	 * <p>
	 * {@code Non-null argument required}
	 * 
	 * @param argument
	 *            the object reference to check for nullity
	 * @return argument if not {@code null}
	 * @throws IllegalArgumentException
	 *             if obj is {@code null}
	 */
	@SuppressWarnings("javadoc")
	public static <T>T argNotNull(final T argument) throws IllegalArgumentException {
		if (argument == null) throw new IllegalArgumentException("Argument was null");
		return argument;
	}
	
	/**
	 * Acts like {@link java.util.Objects#requireNonNull(Object)}, but throws
	 * {@code IllegalArgumentException} instead of {@code NullPointerException}
	 * <p>
	 * Message is
	 * <p>
	 * <code>%argName% was null</code>
	 * 
	 * @param argument
	 *            the object reference to check for nullity
	 * @param argName
	 *            arg variable name to be included in message
	 * @return argument if not {@code null}
	 * @throws IllegalArgumentException
	 *             if obj is {@code null}
	 */
	@SuppressWarnings("javadoc")
	public static <T>T argNotNull(final T argument, final String argName)
			throws IllegalArgumentException {
		if (argument == null) throw new IllegalArgumentException(argName + " was null");
		return argument;
	}
	
	/**
	 * Returns value, if it's not {@code null}, or default value otherwise.
	 * 
	 * @param value
	 *            main value
	 * @param defaultValue
	 *            backup value
	 * @return main value, or default one if main is {@code null}
	 */
	public static <T>T valueOr(final T value, final T defaultValue) {
		return value != null ? value : defaultValue;
	}
	
	//
	// ++ Equals
	//
	//
	// ++ Shortcuts
	//
	public static boolean eq(final Object o1, final Object o2) {
		return equals(o1, o2);
	}
	
	/** Shortcut for {@link #equals(Object, Object, Object...)}, used to resolve a naming problem */
	@SuppressWarnings("javadoc")
	public static boolean eq(final Object o1, final Object o2, final Object... o) {
		return equals(o1, o2, o);
	}
	
	/** Shortcut for {@link #equalsTyped(Object, Object)} */
	@SuppressWarnings("javadoc")
	public static <T>boolean eqt(final T o1, final T o2) {
		return equalsTyped(o1, o2);
	}
	
	/** Shortcut for {@link #equalsTyped(Object, Object, Object...)} */
	@SuppressWarnings("javadoc")
	public static <T>boolean eqt(final T o1, final T o2, final T... o) {
		return equalsTyped(o1, o2, o);
	}
	
	//
	// -- Shortcuts
	//
	/**
	 * {@code null}-safe {@code o1.equals(o2)} computing function with a compile-time type check
	 * 
	 * @param o1
	 *            first object
	 * @param o2
	 *            second object
	 * @return {@code true} if both object are {@code null} or {@code o1.equals(o2)}, {@code false}
	 *         otherwise
	 * @see Objects#equals(Object, Object)
	 */
	@SuppressWarnings("javadoc")
	public static <T>boolean equalsTyped(final T o1, final T o2) {
		return equals(o1, o2);
	}
	
	/**
	 * {@code null}-safe multiple objects equality computing fuction
	 * 
	 * @param o1
	 *            first object
	 * @param o2
	 *            second object
	 * @param o
	 *            all other objects
	 * @return {@code true} if all objects are equal
	 */
	public static boolean equals(final Object o1, final Object o2, final Object... o) {
		if (o1 == null) {
			if (o2 != null) return false;
			for (final Object object : o) {
				if (object != null) return false;
			}
			return true;
		}
		if (!o1.equals(o2)) return false;
		for (final Object object : o) {
			if (!o1.equals(object)) return false;
		}
		return true;
	}
	
	/**
	 * {@code null}-safe multiple objects equality computing fuction with a compile-time type check
	 * 
	 * @param o1
	 *            first object
	 * @param o2
	 *            second object
	 * @param o
	 *            all other objects
	 * @return {@code true} if all objects are equal
	 */
	public static <T>boolean equalsTyped(final T o1, final T o2, final T... o) {
		if (o1 == null) {
			if (o2 != null) return false;
			for (final Object object : o) {
				if (object != null) return false;
			}
			return true;
		}
		if (!o1.equals(o2)) return false;
		for (final T object : o) {
			if (!o1.equals(object)) return false;
		}
		return true;
	}
	
	/** {@code null}-safe primitive-to-wrapper comparer, will return {@code false} if wrapper is null */
	@SuppressWarnings("javadoc")
	public static boolean equalsD(final double primitive, final Double obj) {
		if (obj == null) return false;
		return primitive == obj.doubleValue();
	}
	
	/** {@code null}-safe primitive-to-wrapper comparer, will return {@code false} if wrapper is null */
	@SuppressWarnings("javadoc")
	public static boolean equalsF(final float primitive, final Float obj) {
		if (obj == null) return false;
		return primitive == obj.floatValue();
	}
	
	/** {@code null}-safe primitive-to-wrapper comparer, will return {@code false} if wrapper is null */
	@SuppressWarnings("javadoc")
	public static boolean equalsI(final int primitive, final Integer obj) {
		if (obj == null) return false;
		return primitive == obj.intValue();
	}
	
	/** {@code null}-safe primitive-to-wrapper comparer, will return {@code false} if wrapper is null */
	@SuppressWarnings("javadoc")
	public static boolean equalsL(final long primitive, final Long obj) {
		if (obj == null) return false;
		return primitive == obj.longValue();
	}
	
	/** {@code null}-safe primitive-to-wrapper comparer, will return {@code false} if wrapper is null */
	@SuppressWarnings("javadoc")
	public static boolean equalsC(final char primitive, final Character obj) {
		if (obj == null) return false;
		return primitive == obj.charValue();
	}
	
	/** {@code null}-safe primitive-to-wrapper comparer, will return {@code false} if wrapper is null */
	@SuppressWarnings("javadoc")
	public static boolean equalsB(final boolean primitive, final Boolean obj) {
		if (obj == null) return false;
		return primitive == obj.booleanValue();
	}
	
	/** {@code null}-safe object-to-wrapper comparer, will return {@code false} if wrapper is null */
	@SuppressWarnings("javadoc")
	public static <T>boolean equalsW(final T object, final Wrapper <T> wrapper) {
		if (wrapper == null) return false;
		return equals(object, wrapper.getObject());
	}
	
	//
	// -- Equals
	//
	public static int hashCode(final Object obj) {
		return obj == null ? 0 : obj.hashCode();
	}
	
	public static void requireNonNull(final Object obj) {
		requireNonNull(obj, "");
	}
	
	public static void requireNonNull(final Object obj, final String name) {
		if (obj == null) throw new NullPointerException(name);
	}
	
	public static <T extends Comparable <T>>int compare(final T x, final T y) {
		return x.compareTo(y) < 0 ? -1 : x.compareTo(y) == 0 ? 0 : 1;
	}
	
	public static int compare(final byte x, final byte y) {
		return x < y ? -1 : x == y ? 0 : 1;
	}
	
	public static int compare(final short x, final short y) {
		return x < y ? -1 : x == y ? 0 : 1;
	}
	
	public static int compare(final int x, final int y) {
		return x < y ? -1 : x == y ? 0 : 1;
	}
	
	public static int compare(final long x, final long y) {
		return x < y ? -1 : x == y ? 0 : 1;
	}
}
