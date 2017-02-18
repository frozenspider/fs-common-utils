package org.fs.utility

import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class StopWatchSpec extends Spec {
  def `peek ` = {
    val sw = new StopWatch
    Thread.sleep(100)
    val peek = sw.peek
    assert(peek >= 100 && peek <= 150)
  }

  def `measure ` = {
    val (r, t) = StopWatch.measure {
      Thread.sleep(100)
      123
    }
    assert(t >= 100 && t <= 150)
    assert(r == 123)
  }

  def `measure and call` = {
    var rc: Int = 0
    var tc: Long = 0
    val r = StopWatch.measureAndCall {
      Thread.sleep(100)
      123
    } { (r, t) =>
      rc = r
      tc = t
    }
    assert(r == 123)
    assert(rc == 123)
    assert(tc >= 100 && tc <= 150)
  }
}
