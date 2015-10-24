package org.fs.utils.character;

import java.util.Arrays;
import java.util.Random;

public class CharUtils {

	private static final Random	rng;
	static {
		rng = new Random();
	}

	public static char[] nTimes(final char c, final int n){
		final char[] res = new char[n];
		Arrays.fill(res, c);
		return res;
	}

	public static char getRandomChar(final boolean allowUpperCase){
		// 48-57 - Digits, 10
		// 65-90 - Upper Case, 26
		// 97-122 - Lower Case, 26
		if (allowUpperCase) {
			int t = rng.nextInt(62) + 48;
			if (t > 57) {
				t += 7;
			}
			if (t > 90) {
				t += 6;
			}
			return (char)t;
		}
		int t = rng.nextInt(36) + 48;
		if (t > 57) {
			t += 39;
		}
		return (char)t;
	}

	public static char getRandomChar(final String source){
		return source.charAt(rng.nextInt(source.length()));
	}

	public static String getStringOfRandomChars(final int desirableLength, final boolean allowUpperCase){
		final StringBuffer result = new StringBuffer(desirableLength);
		for (int i = 0; i < desirableLength; i++) {
			result.append(getRandomChar(allowUpperCase));
		}
		return result.toString();
	}

	public static String getStringOfRandomChars(final String source, final int desirableLength){
		final StringBuffer result = new StringBuffer(desirableLength);
		for (int i = 0; i < desirableLength; i++) {
			result.append(getRandomChar(source));
		}
		return result.toString();
	}

	/** @return random lowercase hexadecimal char, one of {@code [0,f]} */
	private static char getRandomHexChar(){
		// 48-57 - [0-9]
		// 97-102 - [a-f]
		int t = rng.nextInt(36);
		if (t > 9) {
			t += 39;
		}
		t += 48;
		return (char)t;
	}

	/**
	 * @param desiredLength
	 *            a length of a random hexadecimal string
	 * @return a random lowercase hexadecimal string of a given length
	 */
	public static String getRandomHex(final int desiredLength){
		final StringBuffer result = new StringBuffer(desiredLength);
		for (int i = 0; i < desiredLength; i++) {
			result.append(getRandomHexChar());
		}
		return result.toString();
	}

	private static char getRandomDecimalChar(){
		// 48-57 - [0-9]
		return (char)(rng.nextInt(10) + 48);
	}

	public static String getRandomDecimal(final int desirableLength){
		final StringBuffer result = new StringBuffer(desirableLength);
		for (int i = 0; i < desirableLength; i++) {
			result.append(getRandomDecimalChar());
		}
		return result.toString();
	}
}
