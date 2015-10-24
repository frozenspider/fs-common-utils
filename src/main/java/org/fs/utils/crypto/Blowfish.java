package org.fs.utils.crypto;

import static org.fs.utils.character.UTF.*;

import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.fs.utils.exception.CipherException;

public class Blowfish {

	public static byte[] doCrypt(final byte[] data, final byte[] key, final boolean encrypt) {
		try {
			final SecretKeySpec skeySpec = new SecretKeySpec(key, "Blowfish");
			final Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, skeySpec);
			return cipher.doFinal(data);
		} catch(final Exception ex) {
			throw new CipherException(ex);
		}
	}

	public static byte[] encrypt(final byte[] data, final byte[] key) {
		return doCrypt(data, key, true);
	}

	public static byte[] encrypt(final String data, final String encoding, final String key) {
		return doCrypt(data.getBytes(Charset.forName(encoding)), key.getBytes(CHARSET_UTF8), true);
	}

	public static byte[] decrypt(final byte[] data, final byte[] key) {
		return doCrypt(data, key, false);
	}

	public static byte[] decrypt(final String data, final String encoding, final String key) {
		return doCrypt(data.getBytes(Charset.forName(encoding)), key.getBytes(CHARSET_UTF8), false);
	}
}

