package org.fs.utility.crypto

import javax.crypto.{ Cipher => JCipher }
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom
import java.security.cert.Certificate

/**
 * Simplifies working with Java ciphers for encryption/decryption purposes.
 *
 * @author FS
 */
class Cipher(
    val transformation: String,
    rand: SecureRandom = SecureRandom.getInstanceStrong) {

  def encrypt(data: Seq[Byte], key: Seq[Byte]): Array[Byte] =
    callCipher(data, key, true)

  def encrypt(data: Seq[Byte], key: String): Array[Byte] =
    callCipher(data, key.getBytes("UTF-8"), true)

  def encrypt(data: Seq[Byte], cert: Certificate): Array[Byte] =
    callCipher(data, cert, true)

  def decrypt(data: Seq[Byte], key: Seq[Byte]): Array[Byte] =
    callCipher(data, key, false)

  def decrypt(data: Seq[Byte], key: String): Array[Byte] =
    callCipher(data, key.getBytes("UTF-8"), false)

  def decrypt(data: Seq[Byte], cert: Certificate): Array[Byte] =
    callCipher(data, cert, true)

  protected def callCipher(data: Seq[Byte], key: Seq[Byte], encrypt: Boolean): Array[Byte] = {
    callCipher(data) { c =>
      val k = new SecretKeySpec(key.toArray, transformation)
      c.init(if (encrypt) JCipher.ENCRYPT_MODE else JCipher.DECRYPT_MODE, k, rand)
    }
  }

  protected def callCipher(data: Seq[Byte], cert: Certificate, encrypt: Boolean): Array[Byte] = {
    callCipher(data) {
      _.init(if (encrypt) JCipher.ENCRYPT_MODE else JCipher.DECRYPT_MODE, cert, rand)
    }
  }

  protected def callCipher(data: Seq[Byte])(init: JCipher => Unit): Array[Byte] = {
    val c = JCipher.getInstance(transformation)
    init(c)
    c.doFinal(data.toArray)
  }
}
