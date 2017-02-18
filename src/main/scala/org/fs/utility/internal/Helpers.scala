package org.fs.utility.internal

import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * @author FS
 */
object Helpers {

  // We won't rely on IOUtils.readFully to avoid dependency on commons-io
  protected[utility] def readFully(is: InputStream): Array[Byte] = {
    require(is != null, "Input stream was null")
    try {
      val result = new ByteArrayOutputStream()
      val buffer = Array.ofDim[Byte](1024)
      var length: Int = 0
      do {
        result.write(buffer, 0, length)
        length = is.read(buffer)
      } while (length != -1)
      result.toByteArray
    } finally {
      try {
        is.close()
      } catch {
        case _: Throwable => // Ignore
      }
    }
  }
}
