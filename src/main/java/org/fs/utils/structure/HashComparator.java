package org.fs.utils.structure;

import java.util.Comparator;

import org.fs.utils.ObjectUtils;

/**
 * Compares objects by their hashCode.
 *
 * @author FS
 */
public class HashComparator implements Comparator <Object> {

	@Override
	public int compare(final Object o1, final Object o2) {
		final int hash1 = ObjectUtils.hashCode(o1);
		final int hash2 = ObjectUtils.hashCode(o2);
		return ObjectUtils.compare(hash1, hash2);
	}
}

