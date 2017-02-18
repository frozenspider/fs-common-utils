# fs-common-utils

Common utility classes and implicit helpers library for Scala.

[![Build Status](https://travis-ci.org/frozenspider/fs-common-utils.svg?branch=master)](https://travis-ci.org/frozenspider/fs-common-utils)
[![JitPack](https://jitpack.io/v/frozenspider/fs-common-utils.svg)](https://jitpack.io/#frozenspider/fs-common-utils)
[![codecov.io](http://codecov.io/github/frozenspider/fs-common-utils/coverage.svg?branch=master)](http://codecov.io/github/frozenspider/fs-common-utils?branch=master)


## Overview

This library contains a few various general classes and helpers.
Namely, those are implicit helpers, stopwatch and tables.


### Implicit helpers

Contains implicit wrappers, mainly for collections, with conveniece methods such as
`Seq[Option[A]].yieldDefined: Seq[A]` or `Seq[A].mapWithIndex(f: (A, Int) => B): Seq[B]`.

Also provides `Long.hhMmSsString` and `Throwable.stackTraceString`.

To import them all, either extend `org.fs.utility.Imports` or import
`org.fs.utility.Imports._` - whichever you deem fit.


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


### Tables

Represented by two main traits `IndexedTable` and `KeyTable`, which are designed
to represents a two-dimensional `Seq` and `Map` like structures.
(Their respective default implementations are `IndexedSeqTable` and `KeyTable`).

Some details of their behaviour differs greatly between them - namely, sorting and adding/removing
rows/columns.

Tables are also supplied with `toString` implementation producing a pretty-print like this:

```
IndexedTable  MapKeyTable
+-+-+--+-+-+  +-+-+--+-+-+
| |0|1 |2|3|  | |a|b |c|d|
+-+-+--+-+-+  +-+-+--+-+-+
|0|A|B | | |  |5|A|B | | |
+-+-+--+-+-+  +-+-+--+-+-+
|1| |CD| | |  |4| |CD| | |
+-+-+--+-+-+  +-+-+--+-+-+
|2| |  | | |  |3| |  | | |
+-+-+--+-+-+  +-+-+--+-+-+
```

`IndexedTable` and `KeyTable` are considered to be relatively independant and never equal to each other.
