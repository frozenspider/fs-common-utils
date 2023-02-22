# fs-common-utils

Common utility classes and implicit helpers library for Scala.

Has no runtime dependencies and is cross-compiled for Scala 2.11 and 2.12,
published via [JitPack](https://jitpack.io/#frozenspider/fs-common-utils).

[![JitPack](https://jitpack.io/v/frozenspider/fs-common-utils.svg)](https://jitpack.io/#frozenspider/fs-common-utils)
[![Build Status](https://travis-ci.org/frozenspider/fs-common-utils.svg?branch=master)](https://travis-ci.org/frozenspider/fs-common-utils)
[![codecov.io](http://codecov.io/github/frozenspider/fs-common-utils/coverage.svg?branch=master)](http://codecov.io/github/frozenspider/fs-common-utils?branch=master)


## Overview

This library contains a few various general classes and helpers.
Namely, those are implicit helpers and stopwatch.


### Implicit helpers

Contains implicit wrappers, mainly for collections, with conveniece methods such as
`Seq[Option[A]].yieldDefined: Seq[A]` or `Seq[A].mapWithIndex(f: (A, Int) => B): Seq[B]`.

Also provides `Long.hhMmSsString` and `Throwable.stackTraceString`.

To import them all, use `import org.fs.utility.Imports._`.


### Stopwatch

Very simple class used to measure execution time. Usage:

```scala
def code: () => String = ???

// Plain
val sw = new StopWatch
val result = code()
println(s"$result evaluated in ${sw.peek} ms")

// With helper functions
val (result, time) = StopWatch.measure {
  code()
}
println(s"$result evaluated in $time ms")}

val result = StopWatch.measure {
  code()
} { (r, time) => println(s"$r evaluated in $time ms")}
```
