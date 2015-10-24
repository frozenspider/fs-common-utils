package org.fs.utils;

import static org.fs.utils.character.UTF.*;

import java.util.Arrays;

import org.fs.utils.character.CharUtils;

public class StringUtils {
	
	public static String nTimes(final char c, final int n) {
		return new String(CharUtils.nTimes(c, n));
	}
	
	public static String nTimes(final String str, final int n) {
		final StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; ++i) {
			sb.append(str);
		}
		return sb.toString();
	}
	
	/**
	 * Returns an <i>independant</i> substring, which does not preserve it's source character
	 * arrray.
	 * 
	 * @param source
	 *            source string.
	 * @param beginIndex
	 *            the beginning index, inclusive.
	 * @return an independant substring
	 * @see String#substring(int)
	 */
	public static String substring(final String source, final int beginIndex) {
		return new String(source.substring(beginIndex));
	}
	
	/**
	 * Returns an <i>independant</i> substring, which does not preserve it's source character
	 * arrray.
	 * 
	 * @param source
	 *            source string.
	 * @param beginIndex
	 *            the beginning index, inclusive.
	 * @param endIndex
	 *            the ending index, exclusive.
	 * @return an independant substring
	 * @see String#substring(int, int)
	 */
	public static String substring(final String source, final int beginIndex, final int endIndex) {
		return new String(source.substring(beginIndex, endIndex));
	}
	
	/**
	 * <pre>
	 *                               SI     BINARY
	 * 
	 *                    0:        0 B        0 B
	 *                   27:       27 B       27 B
	 *                  999:      999 B      999 B
	 *                 1000:     1.0 kB     1000 B
	 *                 1023:     1.0 kB     1023 B
	 *                 1024:     1.0 kB    1.0 KiB
	 *                 1728:     1.7 kB    1.7 KiB
	 *               110592:   110.6 kB  108.0 KiB
	 *              7077888:     7.1 kB    6.8 MiB
	 *            452984832:   453.0 MB  432.0 MiB
	 *          28991029248:    29.0 GB   27.0 GiB
	 *        1855425871872:     1.9 TB    1.7 TiB
	 *  9223372036854775807:     9.2 EB    8.0 EiB   (Long.MAX_VALUE)
	 * </pre>
	 * 
	 * @param bytes
	 *            the value to be represented
	 * @param si
	 *            {@code true} for 1k=1000 representaion, {@code false} for 1k=1024
	 * @author aioobe
	 * @return human-readable byte count
	 * @see <a
	 *      href="http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java/3758880">StackOverflow</a>
	 */
	public static String humanReadableByteCount(final long bytes, final boolean si) {
		final int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		final int exp = (int) (Math.log(bytes) / Math.log(unit));
		final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public static String truncate(final String str, final int maxLen) {
		return str.length() <= maxLen ? str : str.substring(0, maxLen) + "...";
	}
	
	/**
	 * Unites two strings using the {@link String#concat(String)}. The only difference it that this
	 * one is <code>null</code>-aware.
	 * 
	 * @param val1
	 *            first string
	 * @param val2
	 *            second string
	 * @return if one of the string is null, returns another, else returns String.concat() of them
	 */
	public static String unite(final String val1, final String val2) {
		if (val1 == null) return val2;
		if (val2 == null) return val1;
		return val1.concat(val2);
	}
	
	/**
	 * Gives exactly the same result, as the {@link String#split(String)} with the single char
	 * string supplied as the delimiter, but with about 3 times higher perfomance.
	 * <p>
	 * Does not keep reference to an original string.
	 * 
	 * @param source
	 *            string to split. If {@code null}, then empty array is returned.
	 * @param delimeter
	 *            delimiting character (e.g. <code>'/'</code> for file path in *nix or
	 *            <code>'\\'</code> in Windows)
	 * @return array of string parts
	 */
	public static String[] split(final String source, final char delimeter) {
		if (source == null) return new String[0];
		int[] startIdx = new int[100];
		startIdx[0] = 0;
		int idxLen = 1;
		for (int i = 0; i < source.length(); i++) {
			if (source.charAt(i) == delimeter) {
				if (idxLen >= startIdx.length) {
					startIdx = Arrays.copyOf(startIdx, startIdx.length / 2 * 3 + 1);
				}
				startIdx[idxLen] = i + 1;
				idxLen++;
			}
		}
		// String.split() ignores all delimiters after the last non-delimiter character.
		int zeroPos = 0;
		final boolean zeroLast = startIdx[idxLen - 1] == source.length();
		String[] result;
		if (zeroLast) {
			for (int i = idxLen - 1; i > 0; i--) {
				if (startIdx[i] != startIdx[i - 1] + 1) {
					zeroPos = i;
					break;
				}
			}
			result = new String[zeroPos];
			for (int i = 0; i < zeroPos; i++) {
				result[i] = new String(source.substring(startIdx[i], startIdx[i + 1] - 1));
			}
		} else {
			result = new String[idxLen];
			for (int i = 0; i < idxLen - 1; i++) {
				result[i] = new String(source.substring(startIdx[i], startIdx[i + 1] - 1));
			}
			result[idxLen - 1] = new String(source.substring(startIdx[idxLen - 1], source.length()));
		}
		return result;
	}
	
	/**
	 * Same, as {@link Integer#valueOf(String)}, but upon failure this will return {@code null}
	 * istead of throwing an exception
	 * 
	 * @param str
	 * @return an integer or {@code null}
	 */
	public static Integer toInteger(final String str) {
		try {
			return Integer.valueOf(str);
		} catch(final NumberFormatException ex) {
			return null;
		}
	}
	
	/** @see #toInteger(String) */
	@SuppressWarnings("javadoc")
	public static Long toLong(final String str) {
		try {
			return Long.valueOf(str);
		} catch(final NumberFormatException ex) {
			return null;
		}
	}
	
	/** @see #toInteger(String) */
	@SuppressWarnings("javadoc")
	public static Short toShort(final String str) {
		try {
			return Short.valueOf(str);
		} catch(final NumberFormatException ex) {
			return null;
		}
	}
	
	/** @see #toInteger(String) */
	@SuppressWarnings("javadoc")
	public static Byte toByte(final String str) {
		try {
			return Byte.valueOf(str);
		} catch(final NumberFormatException ex) {
			return null;
		}
	}
	
	/** @see #toInteger(String) */
	@SuppressWarnings("javadoc")
	public static Float toFloat(final String str) {
		try {
			return Float.valueOf(str);
		} catch(final NumberFormatException ex) {
			return null;
		}
	}
	
	/** @see #toInteger(String) */
	@SuppressWarnings("javadoc")
	public static Double toDouble(final String str) {
		try {
			return Double.valueOf(str);
		} catch(final NumberFormatException ex) {
			return null;
		}
	}
	
	/**
	 * Converts a byte array to a UTF-8 string. Shortcut for a
	 * {@code new String(content, Charset.forName("UTF-8"))}
	 */
	@SuppressWarnings("javadoc")
	public static String toUTF8(final byte[] content) {
		return new String(content, CHARSET_UTF8);
	}
	
	/**
	 * Shortcut for {@code str.getBytes(Charset.forName("UTF-8"))}
	 * 
	 * @param str
	 *            UTF-8 string
	 * @return string bytes
	 */
	public static byte[] fromUTF8(final String str) {
		return str.getBytes(CHARSET_UTF8);
	}
}
