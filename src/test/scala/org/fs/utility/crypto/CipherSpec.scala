package org.fs.utility.crypto

import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner
import javax.crypto.BadPaddingException

@RunWith(classOf[JUnitRunner])
class CipherSpec extends Spec {
  object `blowfish -` {
    val c = new Cipher("Blowfish")

    object `16 bytes -` {
      val key = "abcdefgh"
      val data = "Text to encrypt".map(_.toByte).toArray

      def `encrypt ` = {
        val enc = c.encrypt(data, key)
        val enc2String = "B64D0BA946160001740BCCA7028E15A9"
        val enc2 = BigInt("B64D0BA946160001740BCCA7028E15A9", 16).toByteArray.tail // To drop a byte with sign bit
        assert(enc.toSeq != data.toSeq)
        assert(enc.toSeq == enc2.toSeq)
      }

      def `decrypt ` = {
        val enc = c.encrypt(data, key)
        assert(c.decrypt(enc, key).toSeq == data.toSeq)
        intercept[BadPaddingException] {
          c.decrypt(c.encrypt(data, "A" + key.tail), key)
        }
      }
    }
  }
}
