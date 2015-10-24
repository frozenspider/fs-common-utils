package org.fs.utils.hash;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.fs.utils.Global;

public class Md5Hash implements Serializable {

	private static final long			serialVersionUID	= 6201167003739542330L;
	/** A MD5 {@link MessageDigest} to be used in hashing, or {@code null} if MD5 is not supported. */
	public static final MessageDigest	md					= getMessageDigest();
	private final byte[]				content				= new byte[16];

	/** @return {@code MD5} {@link MessageDigest} if available, otherwise {@code null} */
	private static MessageDigest getMessageDigest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch(final NoSuchAlgorithmException e) {
			throw new Error(e.getMessage(), e);
		}
	}

	/**
	 * Hashes the given data, producing a {@code MD5Hash} instance.
	 *
	 * @param data
	 *            data to be hashed
	 * @return a brand new {@code MD5Hash} or {@code null} if MD5 is not supported
	 * @throws NullPointerException
	 *             - If the given data is {@code null}
	 */
	public static Md5Hash doHash(final byte[] data) {
		if (data == null) throw new NullPointerException("data cannot be null");
		if (md == null) return null;
		return new Md5Hash(md.digest(data));
	}

	/**
	 * Hashes the given string data in the given encoding, producing a {@code MD5Hash} instance.
	 *
	 * @param data
	 *            string to be hashed
	 * @param charset
	 *            string encoding
	 * @return a brand new {@code MD5Hash} or {@code null} if MD5 is not supported
	 * @throws NullPointerException
	 *             if the given data is {@code null}
	 * @throws IllegalCharsetNameException
	 *             if the given charset name is illegal
	 * @throws IllegalArgumentException
	 *             if the given charset name is {@code null}
	 * @throws UnsupportedCharsetException
	 *             if no support for the named charset is available in this instance of the Java
	 *             virtual machine
	 */
	public static Md5Hash doHash(final String data, final String charset) {
		if (data == null) throw new NullPointerException("data cannot be null");
		if (md == null) return null;
		return new Md5Hash(md.digest(data.getBytes(Charset.forName(charset))));
	}

	/**
	 * Create an {@code MD5Hash} from the given 32-char string representation of hashed MD5 data
	 *
	 * @param md5str
	 *            case-insensitive string representation of a hash (e.g.
	 *            {@code 5F4DCC3B5AA765D61D8327DEB882CF99})
	 */
	public Md5Hash(final String md5str) {
		if (md5str.length() != 32)
			throw new IllegalArgumentException("String length must be 32, not " + md5str.length());
		for (int i = 0; i < 16; i++) {
			content[i] = (byte)Integer.parseInt(md5str.substring(i * 2, i * 2 + 2), 16);
		}
	}

	/**
	 * Create an {@code MD5Hash} from the given 16 bytes of hashed MD5 data.
	 *
	 * @param hash
	 *            array of bytes representing MD5 hash
	 */
	public Md5Hash(final byte[] hash) {
		if (hash.length != 16)
			throw new IllegalArgumentException("Byte array length must be 16, not " + hash.length);
		for (int i = 0; i < 16; i++) {
			content[i] = hash[i];
		}
	}

	/** Returns a string representation of this MD5 hash in a uppercase hexadecimal appearance. */
	@Override
	public String toString() {
		return Global.toHexString(content);
	}

	/** @return a byte array, representing hash content. Array is safe for modification. */
	public byte[] getContent() {
		return Arrays.copyOf(content, content.length);
	}

	/**
	 * Tests two object for equality. Returns {@code true} if the given object is an {@link Md5Hash}
	 * with the same content.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Md5Hash)) return false;
		for (int i = 0; i < 16; i++)
			if (content[i] != ((Md5Hash)obj).content[i]) return false;
		return true;
	}

	/** Hash code of a hash code, errr... */
	@Override
	public int hashCode() {
		int group1 = 0; // XOR of all bytes
		for (int i = 0; i < 16; i++) {
			group1 ^= content[i];
		}
		group1 &= 0xFF;
		int group2 = 0; // XOR of fist half bytes
		for (int i = 0; i < 8; i++) {
			group2 ^= content[i];
		}
		group2 &= 0xFF;
		int group3 = 0; // XOR of second half bytes
		for (int i = 8; i < 16; i++) {
			group3 ^= content[i];
		}
		group3 &= 0xFF;
		int group4 = 0; // Residue of dividing numbers by 256
		for (int i = 8; i < 16; i++) {
			group4 = (group4 + content[i]) % 0x100;
		}
		group4 &= 0xFF;
		return group1 + 0x100 * group2 + 0x10000 * group3 + 0x1000000 * group4;
	}
}

