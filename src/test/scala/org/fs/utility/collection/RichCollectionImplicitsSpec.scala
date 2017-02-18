package org.fs.utility.collection

import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner
import scala.collection.GenIterableLike
import scala.collection.GenIterable
import scala.collection.IndexedSeqLike
import scala.collection.GenTraversableLike

@RunWith(classOf[JUnitRunner])
class RichCollectionImplicitsSpec extends Spec {
  object `RichIterable -` {
    import RichCollectionImplicits.RichIterable

    val coll: GenIterableLike[String, GenIterable[String]] = Seq("a", "b", "c")
    val empty: GenIterableLike[String, GenIterable[String]] = Seq.empty

    def `map with index` = {
      assert(coll.mapWithIndex((el, i) => el + i) === Seq("a0", "b1", "c2"))
      assert(empty.mapWithIndex((el, i) => fail()) === empty)
    }

    def `foreach with index` = {
      var acc = Seq.empty[String]
      coll.foreachWithIndex((el, i) => acc = acc :+ (el + i))
      assert(acc === Seq("a0", "b1", "c2"))
      empty.foreachWithIndex((el, i) => fail())
    }

    def `drop right while` = {
      assert(coll.dropRightWhile(_ != "c") === coll)
      assert(coll.dropRightWhile(_ != "b") === Seq("a", "b"))
      assert(coll.dropRightWhile(_ != "a") === Seq("a"))
      assert(empty.dropRightWhile(_ != fail()) === empty)
    }
  }

  object `RichIndexedSeqLike -` {
    import RichCollectionImplicits.RichIndexedSeqLike

    val coll: IndexedSeqLike[String, IndexedSeq[String]] = IndexedSeq("a", "b", "c")
    val empty: IndexedSeqLike[String, IndexedSeq[String]] = IndexedSeq.empty

    def `get ` = {
      assert(coll.get(0) === Some("a"))
      assert(coll.get(-1) === None)
      assert(coll.get(3) === None)
      assert(empty.get(0) === None)
    }

    def `get or else` = {
      assert(coll.getOrElse(0, fail()) === "a")
      assert(coll.getOrElse(-1, "x") === "x")
      assert(coll.getOrElse(3, "x") === "x")
      assert(empty.getOrElse(0, "x") === "x")
    }
  }

  object `RichOptionsTraversable -` {
    import RichCollectionImplicits.RichOptionsTraversable

    val coll: GenTraversableLike[Option[String], Seq[Option[String]]] = Seq(None, Some("a"), None, Some("b"), Some("c"), None)
    val empty: GenTraversableLike[Option[String], Seq[Option[String]]] = Seq.empty

    def `yield defined` = {
      assert(coll.yieldDefined === Seq("a", "b", "c"))
      assert(empty.yieldDefined === Seq.empty)
    }
  }

  object `RichOptionsIterable -` {
    import RichCollectionImplicits.RichOptionsIterable

    val coll: Iterable[Option[String]] = Seq(None, Some("a"), None, Some("b"), Some("c"), None)
    val empty: Iterable[Option[String]] = Seq.empty

    def `to defined map` = {
      assert(coll.toDefinedMap === Map(1 -> "a", 3 -> "b", 4 -> "c"))
      assert(empty.toDefinedMap === Map.empty)
    }
  }

  object `RichByte* - ` {
    // GZIP output is OS-dependant, so we won't check it vs original
    val gzipPrefix = Seq(0x1F, 0x8B, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00) map (_.toByte)

    // GZIPped via external resourcs
    val emptyGzip = gzipPrefix ++ Seq(0xFF, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00).map(_.toByte)
    val collGzip = gzipPrefix ++ Seq(0xFF, 0x4B, 0x4C, 0x4A, 0x4E, 0x01, 0x00, 0x11, 0xCD, 0x82, 0xED, 0x04, 0x00, 0x00, 0x00).map(_.toByte)

    object `Seq -` {
      import RichCollectionImplicits.RichByteSeq

      val coll: Seq[Byte] = "abcd" map (_.toByte)
      val empty: Seq[Byte] = Seq.empty

      def `to hex string` = {
        assert(coll.toHexString === "61626364")
        assert(empty.toHexString === "")
        assert(Seq(0x00, 0x01, 0xFF).map(_.toByte).toHexString === "0001ff")
      }

      def `gzip ` = {
        assert(coll.gzip startsWith gzipPrefix)
        assert(empty.gzip startsWith gzipPrefix)
        assert(coll.gzip !== empty.gzip)

        assert(coll.gzip.toSeq.gunzip === coll)
        assert(empty.gzip.toSeq.gunzip === empty)
      }

      def `gunzip ` = {
        assert(emptyGzip.gunzip === empty)
        assert(collGzip.gunzip === coll)
      }
    }

    object `Array -` {
      import RichCollectionImplicits.RichByteArray

      val coll: Array[Byte] = "abcd".map(_.toByte).toArray
      val empty: Array[Byte] = Array.empty

      def `to hex string` = {
        assert(coll.toHexString === "61626364")
        assert(empty.toHexString === "")
        assert(Array(0x00, 0x01, 0xFF).map(_.toByte).toHexString === "0001ff")
      }

      def `gzip ` = {
        assert(coll.gzip startsWith gzipPrefix)
        assert(empty.gzip startsWith gzipPrefix)
        assert(coll.gzip !== empty.gzip)

        assert(coll.gzip.gunzip === coll)
        assert(empty.gzip.gunzip === empty)
      }

      def `gunzip ` = {
        assert(emptyGzip.toArray.gunzip === empty)
        assert(collGzip.toArray.gunzip === coll)
      }
    }
  }
}
