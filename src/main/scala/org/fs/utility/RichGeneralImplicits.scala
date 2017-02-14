package org.fs.utility

import java.io.StringWriter
import java.io.PrintWriter

/**
 * Some general implicit helpers
 *
 * @author FS
 */
object RichGeneralImplicits {
  /** Throwable enriched with some of most general support methods */
  implicit class RichThrowable[Th <: Throwable](th: Th) {
    /** @return stack trace printed to a string */
    def stackTraceString: String = {
      val writer = new StringWriter
      th.printStackTrace(new PrintWriter(writer, true))
      writer.toString
    }
  }

  implicit class RichByteSeq(bs: Seq[Byte]) {
    def toHexString: String = {
      val sb = new StringBuilder(bs.length * 2)
      for (b <- bs) {
        val t = (if (b > 0) b else 256 + b).toHexString
        if (t.length() == 1) {
          sb.append("0")
        }
        if (t.length() == 3) {
          sb.append("00")
        } else {
          sb.append(t)
        }
      }
      sb.toString
    }
  }

  implicit class RichLong(l: Long) {
    /**
     * Pretty-printed version of a time between two events, in form {@code HH:mm:ss}, e.g. 112:35:16
     * (112 hours, 35 minutes and 16 seconds). Will round the value to the closest integer.
     * <p>
     * Mostly useful with {@link System#currentTimeMillis}.
     *
     * @param elapsedMilliSeconds
     *            the difference between two time marks, e.g.
     *            {@code System.currentTimeMills - oldMark}
     * @return {@code HH:mm:ss} string
     */
    def hhMmSsString: String = {
      val totalSeconds = Math.round(l.toDouble / 1000)
      val hours = totalSeconds / 3600
      val remainingSeconds = totalSeconds % 3600
      val minutes = remainingSeconds / 60
      val seconds = remainingSeconds % 60
      String.format("%d:%02d:%02d", hours: java.lang.Long, minutes: java.lang.Long, seconds: java.lang.Long)
    }
  }

}
