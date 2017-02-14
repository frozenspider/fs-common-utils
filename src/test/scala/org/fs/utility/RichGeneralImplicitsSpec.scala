package org.fs.utility

import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RichGeneralImplicitsSpec extends Spec {
  import RichGeneralImplicits._

  object `rich throwable -` {
    def `stackTraceString ` = {
      val t = new Throwable("My message")
      val s = t.stackTraceString
      val lines = s.lines.toSeq
      assert(lines.size > 1)
      assert(lines(0) == "java.lang.Throwable: My message")
      assert(lines(1) startsWith s"\tat ${classOf[RichGeneralImplicitsSpec].getCanonicalName}")
    }
  }

  object `rich long -` {
    def `hhMmSsString ` = {
      val h = 123L
      val m = 53L
      val s = 18L
      val ms = 336L
      val long = (((h * 60) + m) * 60 + s) * 1000 + ms
      assert(long.hhMmSsString == s"$h:$m:$s")
    }
  }
}
