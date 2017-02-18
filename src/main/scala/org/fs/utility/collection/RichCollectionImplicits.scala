package org.fs.utility.collection

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

import scala.collection.{ GenIterableLike => GIL }
import scala.collection.{ GenTraversableLike => GTL }
import scala.collection.IndexedSeqLike
import scala.collection.generic.{ CanBuildFrom => CBF }

import org.fs.utility.internal.Helpers

/**
 * Implicit helpers for collections
 *
 * @author FS
 */
trait RichCollectionImplicits {

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
  implicit class RichOptionsIterable[A](iter: Iterable[Option[A]]) {
    /** @return map from indices to defined values */
    def toDefinedMap: Map[Int, A] = {
      val indexed = iter.zipWithIndex
      val collected = indexed.collect({
        case (Some(v), i) => (i, v)
      })
      collected.toMap
    }
  }

  implicit class RichByteSeq(bs: Seq[Byte]) {
    /** @return lowercase hex string of 2 characters per byte */
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

    /** @return content compressed using GZIP */
    def gzip: Array[Byte] = {
      val baos = new ByteArrayOutputStream()
      val gzOs = new GZIPOutputStream(baos)
      try {
        gzOs.write(bs.toArray)
      } finally {
        gzOs.close()
      }
      baos.toByteArray
    }

    /** @return content decompressed using GZIP */
    def gunzip: Array[Byte] = {
      Helpers.readFully(new GZIPInputStream(new ByteArrayInputStream(bs.toArray)))
    }
  }

  implicit class RichByteArray(bs: Array[Byte]) {
    /** @return lowercase hex string of 2 characters per byte */
    def toHexString: String = RichByteSeq(bs).toHexString

    /** @return content compressed using GZIP */
    def gzip: Array[Byte] = RichByteSeq(bs).gzip

    /** @return content decompressed using GZIP */
    def gunzip: Array[Byte] = RichByteSeq(bs).gunzip
  }
}

object RichCollectionImplicits extends RichCollectionImplicits
