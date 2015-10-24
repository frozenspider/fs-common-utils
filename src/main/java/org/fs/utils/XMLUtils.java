package org.fs.utils;

public class XMLUtils {

	/**
	 * Retrieves the document part from the tag start to it's end (inclusive). Supports short
	 * &lttags/>. If the tag is incomplete, will return the determined part. It only checks
	 * hierarchy level without checking match of opening and closing tags, so on a malformed XML it
	 * will return a hierarchy part down to the same level closing tag.
	 * <p>
	 * Warning: Does not perform strong syntax checking, so may fail on a specially formed invalid
	 * XML.
	 * <p>
	 * Example 1:
	 * <p>
	 * We have the following document:
	 * <p>
	 * <code>&lthtml>&ltdiv>&ltdiv>&lt/div>&ltdiv>&lt/div>&lt/div>&lt/html></code>
	 * <p>
	 * Structure:
	 *
	 * <pre>
	 * &lthtml>
	 *     &ltdiv>
	 *         &ltdiv>&lt/div>
	 *         &ltdiv>&lt/div>
	 *     &lt/div>
	 * &lt/html>
	 * </pre>
	 *
	 * Let's get the content of a first <code>&ltdiv></code>
	 * <p>
	 * <code>getFullTag("&lthtml>&ltdiv>&ltdiv>&lt/div>&ltdiv>&lt/div>&lt/div>&lt/html>", 6);</code>
	 * Content of a first <code>&ltdiv></code> is the following:
	 * <p>
	 * <code>&ltdiv>&ltdiv>&lt/div>&ltdiv>&lt/div>&lt/div></code>
	 * <p>
	 * Structure:
	 *
	 * <pre>
	 * &ltdiv>
	 *     &ltdiv>&lt/div>
	 *     &ltdiv>&lt/div>
	 * &lt/div>
	 * </pre>
	 * <p>
	 * Example 2:
	 * <p>
	 * <code>getFullTag("&lta>&ltb>&lt/c>&lt/b>&lt/a>", 3) == "&ltb>&lt/c>";</code>
	 * <p>
	 * Does not keep reference to an original XML character content.
	 *
	 * @param xmlContent
	 *            the XML content
	 * @param tagStartIdx
	 *            index of a tag's <code>&lt</code> character
	 * @param asSubstring
	 *            if {@code true}, returns the substring of an original String (does not allocate
	 *            new string, but may case memory leaks, as the original string will not be
	 *            garbage-collected)
	 * @return tag content
	 */
	public static String getFullTag(final String xmlContent, final int tagStartIdx, final boolean asSubstring) {
		if (xmlContent.charAt(tagStartIdx) != '<')
			throw new IllegalArgumentException("Pointed character must be <");
		int chrIdx = tagStartIdx;
		final int contentLength = xmlContent.length();
		//
		// Modes:
		// 0 - between tags
		// not 0 - other
		//
		// Mode flags:
		// 1 - tag content
		// 2 - string sttribute content
		//
		int mode = 0;
		// Level in increaced upon opening tag start and decreased upon closing tag end
		int level = 0;
		char stringChar = '"'; // may be " or '
		boolean closingTag = false;
		while (chrIdx < contentLength) {
			final char c = xmlContent.charAt(chrIdx);
			++chrIdx;
			if (mode == 0) {
				if (c == '<') {
					mode = 1;
					if (chrIdx < contentLength) {
						closingTag = xmlContent.charAt(chrIdx) == '/';
						if (!closingTag) {
							++level;
						}
					}
					continue;
				}
			} else if ((mode & 2) == 2) {
				// String content
				if (c == stringChar) {
					mode = mode & ~2; // Remove the string flag
					continue;
				}
			} else if ((mode & 1) == 1) {
				// Non-string tag content
				if (c == '>') {
					mode = mode & ~1; // Remove the tag flag
					if (chrIdx > 1 && xmlContent.charAt(chrIdx - 2) == '/') {
						closingTag = true;
					}
					if (closingTag) {
						--level;
						if (level == 0) {
							final String result = xmlContent.substring(tagStartIdx, chrIdx);
							return asSubstring ? result : new String(result);
						}
					}
					continue;
				} else if (c == '\"' || c == '\'') {
					mode = mode | 2; // Set the string flag
					stringChar = c;
					continue;
				}
			} else
				throw new RuntimeException("Implementation fault - undetermined mode: " + mode);
		}
		final String result = xmlContent.substring(tagStartIdx);
		return asSubstring ? result : new String(result);
	}
}

