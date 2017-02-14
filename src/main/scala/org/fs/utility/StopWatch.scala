package org.fs.utility

/**
 * @author FS
 */
class StopWatch {
  val timestamp = System.currentTimeMillis

  def peek: Long =
    System.currentTimeMillis - timestamp
}

/**
 * @author FS
 */
object StopWatch {
  /** Execute code block and return execution result along with time taken */
  def measure[R](block: => R): (R, Long) = {
    val sw = new StopWatch
    val res = block
    (res, sw.peek)
  }

  /** Execute code block and return execution result, calling afterCall function with the measurement results */
  def measureAndCall[R](block: => R)(afterCall: (R, Long) => Unit): R = {
    val sw = new StopWatch
    val res = block
    afterCall(res, sw.peek)
    res
  }
}
