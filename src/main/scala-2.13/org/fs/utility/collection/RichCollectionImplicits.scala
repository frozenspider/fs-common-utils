package org.fs.utility.collection

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

import scala.collection.IterableOnceOps
import scala.collection.IterableOps
import scala.collection.IndexedSeqOps

import org.fs.utility.internal.Helpers

/**
 * Implicit helpers for collections
 *
 * @author FS
 */
private[utility] trait RichCollectionImplicits {

  /** IterableOps[X] enriched with some of most general support methods */
  implicit class RichIterable[A, CC[A2] <: IterableOps[A2, CC, _], C](iter: IterableOps[A, CC, C]) {

    /**
     * Map an iterable with its index.
     *
     * Roughly equivalent to `.zipWithIndex.map`, but allows inlining two-argument functions
     * without pattern matching.
     */
    def mapWithIndex[B](f: (A, Int) => B): CC[B] = {
      val zipped: CC[(A, Int)] = iter.zipWithIndex
      val res:    CC[B] = zipped.map((x: (A, Int)) => f(x._1, x._2))
      res
    }

    /**
     * Applies a two-argument function to every element of an iterable along with its index.
     *
     * Roughly equivalent to `.zipWithIndex.foreach`, but allows inlining two-argument functions
     * without pattern matching.
     */
    def foreachWithIndex[U](f: (A, Int) => U): Unit = {
      iter.zipWithIndex foreach (x => f(x._1, x._2))
    }

    /** Same as `dropWhile`, just reversed */
    def dropRightWhile(pred: A => Boolean): CC[A] = {
      val content = iter.toSeq.reverse.dropWhile(pred).reverse
      iter.iterableFactory.from(content)
    }
  }

  /** IndexedSeqLike[X] enriched with some of most general support methods */
  implicit class RichIndexedSeqOps[A, CC[_], C](is: IndexedSeqOps[A, CC, C]) {

    /** Retrieve an element by its index if it's present, returning `None` otherwise */
    def get(i: Int): Option[A] = {
      if (i < 0 || i >= is.length) None
      else Some(is(i))
    }

    /** Retrieve an element by its index if it's present, returning result of `default` evaluation otherwise */
    def getOrElse[B >: A](i: Int, default: => B): B =
      get(i).getOrElse(default)
  }

  /** Iterable[Option[X]] enriched with some of most general support methods */
  implicit class RichOptionsIterable[A, CC[A2] <: IterableOnceOps[A2, CC, _], C <: IterableOnceOps[Option[A], CC, C]](
      iter: IterableOnceOps[Option[A], CC, C]
  ) {

    /** @return collection of defined values */
    def yieldDefined: CC[A] = {
      iter filter (_.isDefined) map (_.get)
    }

    /** @return map from indices to defined values */
    def toDefinedMap: Map[Int, A] = {
      val indexed: CC[(Option[A], Int)] = iter.zipWithIndex
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
