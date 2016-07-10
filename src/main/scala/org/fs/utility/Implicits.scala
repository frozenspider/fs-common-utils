package org.fs.utility

import java.io.PrintWriter
import java.io.StringWriter

import scala.collection.GenIterableLike
import scala.collection.GenTraversableLike
import scala.collection.IndexedSeqLike
import scala.collection.generic.CanBuildFrom

object Implicits {
  // Type aliases to make signatures shorter
  private type GTL[+A, +Repr] = GenTraversableLike[A, Repr]
  private type GIL[+A, +Repr] = GenIterableLike[A, Repr]
  private type CBF[-From, -Elem, +To] = CanBuildFrom[From, Elem, To]

  /** Iterable[X] enriched with some of most general support methods */
  implicit class RichIterable[A, Repr](iter: GIL[A, Repr]) {

    def mapWithIndex[B, Repr2 <: GTL[(A, Int), Repr2], Repr3](f: (A, Int) => B)(
      implicit bf1: CBF[Repr, (A, Int), Repr2], bf2: CBF[Repr2, B, Repr3]): Repr3 =
      {
        iter.zipWithIndex map (x => f(x._1, x._2))
      }

    def eachWithIndex[U, That <: GTL[(A, Int), _]](f: (A, Int) => U)(
      implicit bf1: CBF[Repr, (A, Int), That]): Unit =
      {
        iter.zipWithIndex foreach (x => f(x._1, x._2))
      }

    def toOption: Option[Repr] = {
      if (iter.isEmpty) None
      else Some(iter.repr)
    }

    def dropRightWhile(pred: A => Boolean)(implicit bf: CBF[Repr, A, Repr]): Repr = {
      val content = iter.toSeq.reverse.dropWhile(pred).reverse
      val builder = bf()
      content.foreach(builder += _)
      builder.result()
    }
  }

  /** IndexedSeqLike[X] enriched with some of most general support methods */
  implicit class RichIndexedSeqLike[A, Repr](is: IndexedSeqLike[A, Repr]) {
    def get(i: Int): Option[A] = {
      if (i < 0 || is.length <= i) None
      else Some(is(i))
    }

    def getOrElse[B >: A](i: Int, default: => B): B =
      get(i).getOrElse(default)
  }

  /** Traversable[Option[X]] enriched with some of most general support methods */
  implicit class RichOptionsTraversable[A, Repr <: GTL[Option[A], Repr]](iter: GTL[Option[A], Repr]) {
    /** @return collection of defined values */
    def yieldDefined[Repr2 <: GTL[A, Repr2]](implicit bf: CBF[Repr, A, Repr2]): Repr2 = {
      iter filter (_.isDefined) map (_.get)
    }
  }

  /** Iterable[Option[X]] enriched with some of most general support methods */
  implicit class RichOptionsIterable2[A](iter: Iterable[Option[A]]) {
    /** @return map from indices to defined values */
    def toDefinedMap: Map[Int, A] = {
      val indexed = iter.zipWithIndex
      val collected = indexed.collect({
        case (Some(v), i) => (i, v)
      })
      collected.toMap
    }
  }

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
