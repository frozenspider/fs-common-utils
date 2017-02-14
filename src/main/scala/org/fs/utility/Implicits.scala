package org.fs.utility

import java.io.PrintWriter
import java.io.StringWriter

object Implicits {
  /** Throwable enriched with some of most general support methods */
  implicit class RichThrowable[Th <: Throwable](th: Th) {
    /** @return stack trace printed to a string */
    def stackTraceString: String = {
      val writer = new StringWriter
      th.printStackTrace(new PrintWriter(writer, true))
      writer.toString
    }
  }
}
