package org.fs.utility.collection.table

import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner
import org.fs.utility.collection.RichCollectionImplicits._
import scala.collection.immutable.ListMap
import scala.reflect.ClassTag

@RunWith(classOf[JUnitRunner])
class IndexedSeqTableSpec extends Spec {
  import TableEquality._

  type IAEx = IllegalArgumentException

  val IST = IndexedSeqTable
  val ISeq = IndexedSeq

  def `empty table` = {
    val table = IST.empty[Int]
    assert(table.isEmpty)
    assert(table.sizes === (0, 0))
    assert(table.count === 0)
    assert(table.rowKeys.isEmpty)
    assert(table.colKeys.isEmpty)
    assert(table.isDefinedAt(0, 0) === false)
    assert(table.isDefinedAt((0, 0)) === false)
    assert(table.isDefinedAt(-1, -1) === false)
    assert(table.get(0, 0) === None)
    assert(table.get((0, 0)) === None)
    assert(table.get(-1, -1) === None)
    intercept[IAEx]{ table(0, 0) }
    intercept[IAEx]{ table((0, 0)) }
    intercept[IAEx]{ table(-1, -1) }
    assert(table.row(0) === ListMap.empty)
    assert(table.col(0) === ListMap.empty)
    assert(table.rowAsSeq(0) === IndexedSeq.empty)
    assert(table.colAsSeq(0) === IndexedSeq.empty)
    assert(table.row(-1) === ListMap.empty)
    assert(table.col(-1) === ListMap.empty)
    assert(table.rowAsSeq(-1) === IndexedSeq.empty)
    assert(table.colAsSeq(-1) === IndexedSeq.empty)
    assert(table.toString() === "+")
  }

  def `empty untrimmed table` = {
    val table = fromAnything[Int](
      Seq(
        Seq(null, null),
        Seq(null, null),
        Seq(null, null)
      )
    )
    assert(table.isEmpty)
    assert(table.sizes === (3, 2))
    assert(table.count === 0)
    assert(table.isDefinedAt(0, 0) === false)
    assert(table.isDefinedAt((0, 0)) === false)
    assert(table.isDefinedAt(-1, -1) === false)
    assert(table.get(0, 0) === None)
    assert(table.get((0, 0)) === None)
    assert(table.get(-1, -1) === None)
    intercept[IAEx]{ table(0, 0) }
    intercept[IAEx]{ table((0, 0)) }
    intercept[IAEx]{ table(-1, -1) }
    assert(table.row(0) === ListMap.empty)
    assert(table.col(0) === ListMap.empty)
    assert(table.rowAsSeq(0) === Seq(None, None))
    assert(table.colAsSeq(0) === Seq(None, None, None))
    assert(table.row(-1) === ListMap.empty)
    assert(table.col(-1) === ListMap.empty)
    assert(table.rowAsSeq(-1) === IndexedSeq.empty)
    assert(table.colAsSeq(-1) === IndexedSeq.empty)
  }

  object `construction from rows and values - ` {
    def `comparing both` = {
      assert(
        IST.fromRows(Seq(
          SeqOfSome(0, 1, 2),
          SeqOfSome(10, 11, 12)
        )) === IST.fromValues[Int](Seq(
          Seq(0, 1, 2),
          Seq(10, 11, 12)
        ))
      )
    }

    def `trailing empty values are not skipped` = {
      assert(
        fromAnything[Int](Seq(
          Seq(0, 1, 2, null),
          Seq(10, 11, 12),
          Seq(null),
          Seq()
        )).sizes === (4, 4)
      )
      assert(
        fromAnything[Int](Seq(
          Seq(0, 1, 2),
          Seq(10, 11, 12),
          Seq(),
          Seq()
        )).sizes === (4, 3)
      )
    }
  }

  object `2x3 dense table -` {
    val table = fromAnything[Int](Seq(
      Seq(0, 1, 2),
      Seq(10, 11, 12)
    ))

    def `size and element getters` = {
      assert(!table.isEmpty)
      assert(table.sizes === (2, 3))
      assert(table.count === 6)
      for {
        r <- 0 to 1
        c <- 0 to 2
      } {
        assert(table.isDefinedAt(r, c))
        val expectedValue = r * 10 + c
        assert(table(r, c) === expectedValue)
        assert(table.get(r, c) === Some(expectedValue))
        assert(table.find(_ == expectedValue) === Some(expectedValue))
        assert(table.findCell(_ == expectedValue) === Some((r, c, expectedValue)))
      }
      assert(table.get(0, 3) === None)
      assert(table.get(2, 0) === None)
      intercept[IAEx]{
        table(0, 3)
      }
      intercept[IAEx]{
        table(2, 0)
      }
    }

    def `elements sequence` = {
      assert(table.elements === Seq(0, 1, 2, 10, 11, 12))
      assert(table.elementsWithIndices === Seq(
        (0, 0, 0),
        (0, 1, 1),
        (0, 2, 2),
        (1, 0, 10),
        (1, 1, 11),
        (1, 2, 12)
      ))
    }

    def `transposition ` = {
      assert(table.transpose === fromAnything[Int](Seq(
        Seq(0, 10),
        Seq(1, 11),
        Seq(2, 12)
      )))
    }

    def `adding/replacing elements` = {
      val table2 = table + (0, 3, "x")
      assert(table2 === fromAnything[Any](Seq(
        Seq(0, 1, 2, "x"),
        Seq(10, 11, 12)
      )))
      assert(table2.sizes === (2, 4))

      val table3 = table2 + (3, 0, "y")
      assert(table3 === fromAnything[Any](Seq(
        Seq(0, 1, 2, "x"),
        Seq(10, 11, 12),
        Seq(),
        Seq("y")
      )))
      assert(table3.sizes === (4, 4))

      val table4 = table3 + (0, 0, "z")
      assert(table4 === fromAnything[Any](Seq(
        Seq("z", 1, 2, "x"),
        Seq(10, 11, 12),
        Seq(),
        Seq("y")
      )))
      assert(table4.sizes === (4, 4))

      val table5 = table4 + (2, 2, "!")
      assert(table5 === fromAnything[Any](Seq(
        Seq("z", 1, 2, "x"),
        Seq(10, 11, 12),
        Seq(null, null, "!"),
        Seq("y")
      )))
      assert(table5.sizes === (4, 4))

      // Alternate signature
      assert(table + (0, 3, "x") === table + ((0, 3), "x"))

      intercept[IAEx] { table + (-1, 0, "x") }
      intercept[IAEx] { table + (0, -1, "x") }
    }

    def `removing elements` = {
      val table2 = table - (0, 0)
      assert(table2 === fromAnything[Int](Seq(
        Seq(null, 1, 2),
        Seq(10, 11, 12)
      )))
      assert(table2.sizes === (2, 3))

      val table3 = table2 - (0, 2)
      assert(table3 === fromAnything[Int](Seq(
        Seq(null, 1),
        Seq(10, 11, 12)
      )))
      assert(table3.sizes === (2, 3))

      val table4 = table3 - (1, 2)
      assert(table4 === fromAnything[Int](Seq(
        Seq(null, 1, null),
        Seq(10, 11, null)
      )))
      assert(table4.sizes === (2, 3))
      assert(table4.trim.sizes === (2, 2))

      val table5 = table4 - (1, 0) - (1, 1)
      assert(table5 === fromAnything[Int](Seq(
        Seq(null, 1, null),
        Seq(null, null, null)
      )))
      assert(table5.sizes === (2, 3))
      assert(table5.trim.sizes === (1, 2))

      // Alternate signature
      assert(table - (0, 0) === table - ((0, 0)))
    }

    def `removing non-existing element` = {
      assert((table - (5, 9)) === table)
      assert((table - (0, 0) - (0, 0) - (0, 0)) === (table - (0, 0)))
      intercept[IAEx] { table - (0, -1) }
      intercept[IAEx] { table - (-1, 0) }
    }

    def `concatenating tables` = {
      assert(
        table
          ++ fromAnything[String](Seq(
            Seq(null, "x")
          ))
          === fromAnything[Any](Seq(
            Seq(0, "x", 2),
            Seq(10, 11, 12)
          ))
      )
      assert(
        table
          ++ fromAnything[String](Seq(
            Seq(),
            Seq(),
            Seq(),
            Seq(null, "x")
          ))
          === fromAnything[Any](Seq(
            Seq(0, 1, 2),
            Seq(10, 11, 12),
            Seq(),
            Seq(null, "x")
          ))
      )
    }

    def `equality ` = {
      assert(table != null)
      assert(table != "bang!")
      assert(table === table)
      assert(table === (table + (0, 0, 0)))
      assert(table === (table + (1, 1, 11)))
      assert(table != (table - (0, 0)))
      assert(table != table.transpose)
      assert(table === table.transpose.transpose)
      assert(table != table.withoutRow(0))
      assert(table === table.withoutRow(2))
      assert(table != table.withoutCol(0))
      assert(table === table.withoutCol(3))
      assert(table === table.withoutRow(0).withInsertedRow(0, table.rowAsSeq(0)))
      assert(table === table.withoutCol(0).withInsertedCol(0, table.colAsSeq(0)))
      assert(table != table.swapRows(0, 1))
      assert(table != table.swapCols(0, 2))
    }

    def `string representation` = {
      assert(table.toString === """|+-+--+--+--+
                                   || |0 |1 |2 |
                                   |+-+--+--+--+
                                   ||0|0 |1 |2 |
                                   |+-+--+--+--+
                                   ||1|10|11|12|
                                   |+-+--+--+--+""".stripMargin)
    }

    object `collection methods -` {
      def `contains ` = {
        assert(table.contains(0))
        assert(table.contains(1))
        assert(table.contains(2))
        assert(table.contains(10))
        assert(table.contains(11))
        assert(table.contains(12))
        assert(!table.contains(9))
        assert(!table.contains(13))
        assert(!table.contains(-1))
      }

      def `foreach ` = {
        var seq = Seq.empty[Int]
        table.foreach(v => seq = seq :+ v)
        assert(seq === Seq(0, 1, 2, 10, 11, 12))
      }

      def `filter ` = {
        val tableOfOdds = table.filter(_ % 2 != 0)
        assert(tableOfOdds === fromAnything[Int](Seq(
          Seq(null, 1, null),
          Seq(null, 11, null)
        )))
        assert(tableOfOdds.filter(_ < 10) === fromAnything[Int](Seq(
          Seq(null, 1, null),
          Seq(null, null, null)
        )))
        assert(tableOfOdds.filter(_ > 10) === fromAnything[Int](Seq(
          Seq(null, null, null),
          Seq(null, 11, null)
        )))
      }

      def `map and mapWithIndex` = {
        assert(table.map(_ * 10) === fromAnything[Int](Seq(
          Seq(0, 10, 20),
          Seq(100, 110, 120)
        )))
        assert(table.mapWithIndex{
          case (r, c, v) => (r * 1000) + (c * 100) + v
        } === fromAnything[Int](Seq(
          Seq(0, 101, 202),
          Seq(1010, 1111, 1212)
        )))
      }
    }

    object `row/col -` {
      def `getters (as map)` = {
        assert(table.row(0) === ListMap(0 -> 0, 1 -> 1, 2 -> 2))
        assert(table.row(1) === ListMap(0 -> 10, 1 -> 11, 2 -> 12))
        assert(table.col(0) === ListMap(0 -> 0, 1 -> 10))
        assert(table.col(1) === ListMap(0 -> 1, 1 -> 11))
        assert(table.col(2) === ListMap(0 -> 2, 1 -> 12))
        assert((table + (0, 3, "x")).row(0) === ListMap(0 -> 0, 1 -> 1, 2 -> 2, 3 -> "x"))
        assert((table + (0, 3, "x")).row(1) === ListMap(0 -> 10, 1 -> 11, 2 -> 12))
        assert((table + (2, 0, "y")).col(0) === ListMap(0 -> 0, 1 -> 10, 2 -> "y"))
        assert((table + (2, 0, "y")).col(1) === ListMap(0 -> 1, 1 -> 11))
      }

      def `getters (as seq)` = {
        assert(table.rowAsSeq(0) === SeqOfSome(0, 1, 2))
        assert(table.rowAsSeq(1) === SeqOfSome(10, 11, 12))
        assert(table.colAsSeq(0) === SeqOfSome(0, 10))
        assert(table.colAsSeq(1) === SeqOfSome(1, 11))
        assert(table.colAsSeq(2) === SeqOfSome(2, 12))
        assert((table + (0, 3, "x")).rowAsSeq(0) === SeqOfSome(0, 1, 2, "x"))
        assert((table + (0, 3, "x")).rowAsSeq(1) === Seq(Some(10), Some(11), Some(12), None))
        assert((table + (2, 0, "y")).colAsSeq(0) === SeqOfSome(0, 10, "y"))
        assert((table + (2, 0, "y")).colAsSeq(1) === Seq(Some(1), Some(11), None))
      }

      def `is empty` = {
        assert(!table.isRowEmpty(0))
        assert(!table.isRowEmpty(1))
        assert(table.isRowEmpty(2))
        assert(table.isRowEmpty(100500))
        assert(!table.isColEmpty(0))
        assert(!table.isColEmpty(1))
        assert(!table.isColEmpty(2))
        assert(table.isColEmpty(3))
        assert(table.isColEmpty(100500))
        assert(table.isRowEmpty(-1))
        assert(table.isColEmpty(-1))
      }

      def `removal ` = {
        def testTable[A](t: IndexedSeqTable[A], sizes: (Int, Int), values: Seq[Int]) = {
          assert(t.sizes === sizes)
          assert(t.elements === values)
        }
        val m = Map(1 -> 2)
        m.map(x => false)
        testTable(table.withoutRow(0), (1, 3), Seq(10, 11, 12))
        testTable(table.withoutRow(1), (1, 3), Seq(0, 1, 2))
        testTable(table.withoutCol(0), (2, 2), Seq(1, 2, 11, 12))
        testTable(table.withoutCol(1), (2, 2), Seq(0, 2, 10, 12))
        testTable(table.withoutCol(2), (2, 2), Seq(0, 1, 10, 11))
        assert(table.withoutRow(1) != table)
        assert(table.withoutRow(2) === table)
        assert(table.withoutRow(3) === table)
        assert(table.withoutCol(2) != table)
        assert(table.withoutCol(3) === table)
        assert(table.withoutCol(4) === table)
        intercept[IAEx]{ table.withoutRow(-1) }
        intercept[IAEx]{ table.withoutCol(-1) }
      }

      def `replace row (map)` = {
        assert(table.withRow(0, SeqOfSome("x", "y", "z").toDefinedMap) === fromAnything[Any](Seq(
          Seq("x", "y", "z"),
          Seq(10, 11, 12)
        )))
        assert(table.withRow(0, SeqOfSome("x", "y", "z", "!").toDefinedMap) === fromAnything[Any](Seq(
          Seq("x", "y", "z", "!"),
          Seq(10, 11, 12)
        )))
        assert(table
          .withRow(0, SeqOfSome("x", "y").toDefinedMap)
          .withRow(1, SeqOfSome("z", "!").toDefinedMap)
          === fromAnything[String](Seq(
            Seq("x", "y"),
            Seq("z", "!")
          )))
        assert(table.withRow(0, ISeq(Some("x"), Some("y"), Some("z")).toDefinedMap) === fromAnything[Any](Seq(
          Seq("x", "y", "z"),
          Seq(10, 11, 12)
        )))
        intercept[IAEx] {
          table.withRow(-1, SeqOfSome("x", "y").toDefinedMap)
        }
      }

      def `replace row (seq)` = {
        assert(table.withRow(0, SeqOfSome("x", "y", "z")) === fromAnything[Any](Seq(
          Seq("x", "y", "z"),
          Seq(10, 11, 12)
        )))
        assert(table.withRow(0, SeqOfSome("x", "y", "z", "!")) === fromAnything[Any](Seq(
          Seq("x", "y", "z", "!"),
          Seq(10, 11, 12)
        )))
        assert(table
          .withRow(0, SeqOfSome("x", "y"))
          .withRow(1, SeqOfSome("z", "!"))
          === fromAnything[String](Seq(
            Seq("x", "y"),
            Seq("z", "!")
          )))
        assert(table.withRow(0, ISeq(Some("x"), Some("y"), Some("z"), None, None)) === fromAnything[Any](Seq(
          Seq("x", "y", "z", null, null),
          Seq(10, 11, 12, null, null)
        )))
        intercept[IAEx] {
          table.withRow(-1, SeqOfSome("x", "y"))
        }
      }

      def `insert row (map)` = {
        assert(table.withInsertedRow(0, SeqOfSome("x", "y", "z").toDefinedMap) === fromAnything[Any](Seq(
          Seq("x", "y", "z"),
          Seq(0, 1, 2),
          Seq(10, 11, 12)
        )))
        assert(table.withInsertedRow(2, SeqOfSome("x", "y", "z").toDefinedMap) === fromAnything[Any](Seq(
          Seq(0, 1, 2),
          Seq(10, 11, 12),
          Seq("x", "y", "z")
        )))
        assert(table.withInsertedRow(3, SeqOfSome("x", "y", "z").toDefinedMap) === fromAnything[Any](Seq(
          Seq(0, 1, 2),
          Seq(10, 11, 12),
          Seq(),
          Seq("x", "y", "z")
        )))
        assert(table.withInsertedRow(0, SeqOfSome("x", "y", "z", "!").toDefinedMap) === fromAnything[Any](Seq(
          Seq("x", "y", "z", "!"),
          Seq(0, 1, 2),
          Seq(10, 11, 12)
        )))
        assert(table
          .withInsertedRow(0, SeqOfSome("x", "y").toDefinedMap)
          .withInsertedRow(1, SeqOfSome("z", "!").toDefinedMap)
          === fromAnything[Any](Seq(
            Seq("x", "y"),
            Seq("z", "!"),
            Seq(0, 1, 2),
            Seq(10, 11, 12)
          )))
        assert(table.withInsertedRow(0, ISeq(Some("x"), Some("y"), None, None, Some("z")).toDefinedMap) === fromAnything[Any](Seq(
          Seq("x", "y", null, null, "z"),
          Seq(0, 1, 2, null, null),
          Seq(10, 11, 12, null, null)
        )))
        intercept[IAEx] { table.withInsertedRow(-1, SeqOfSome("x", "y").toDefinedMap) }
      }

      def `insert row (seq)` = {
        assert(table.withInsertedRow(0, SeqOfSome("x", "y", "z")) === fromAnything[Any](Seq(
          Seq("x", "y", "z"),
          Seq(0, 1, 2),
          Seq(10, 11, 12)
        )))
        assert(table.withInsertedRow(2, SeqOfSome("x", "y", "z")) === fromAnything[Any](Seq(
          Seq(0, 1, 2),
          Seq(10, 11, 12),
          Seq("x", "y", "z")
        )))
        assert(table.withInsertedRow(3, SeqOfSome("x", "y", "z")) === fromAnything[Any](Seq(
          Seq(0, 1, 2),
          Seq(10, 11, 12),
          Seq(),
          Seq("x", "y", "z")
        )))
        assert(table.withInsertedRow(0, SeqOfSome("x", "y", "z", "!")) === fromAnything[Any](Seq(
          Seq("x", "y", "z", "!"),
          Seq(0, 1, 2),
          Seq(10, 11, 12)
        )))
        assert(table
          .withInsertedRow(0, SeqOfSome("x", "y"))
          .withInsertedRow(1, SeqOfSome("z", "!"))
          === fromAnything[Any](Seq(
            Seq("x", "y"),
            Seq("z", "!"),
            Seq(0, 1, 2),
            Seq(10, 11, 12)
          )))
        assert(table.withInsertedRow(0, ISeq(Some("x"), Some("y"), Some("z"), None, None)) === fromAnything[Any](Seq(
          Seq("x", "y", "z", null, null),
          Seq(0, 1, 2, null, null),
          Seq(10, 11, 12, null, null)
        )))
        intercept[IAEx] { table.withInsertedRow(-1, SeqOfSome("x", "y")) }
      }

      def `replace col (map)` = {
        assert(table.withCol(0, SeqOfSome("x", "y").toDefinedMap) === fromAnything[Any](Seq(
          Seq("x", 1, 2),
          Seq("y", 11, 12)
        )))
        assert(table.withCol(0, SeqOfSome("x", "y", "z", "!").toDefinedMap) === fromAnything[Any](Seq(
          Seq("x", 1, 2),
          Seq("y", 11, 12),
          Seq("z"),
          Seq("!")
        )))
        assert(table.withCol(4, SeqOfSome("x", "y").toDefinedMap)
          === fromAnything[Any](Seq(
            Seq(0, 1, 2, null, "x"),
            Seq(10, 11, 12, null, "y")
          )))
        assert(table.withCol(4, ISeq(Some("x"), Some("y"), None, None, None).toDefinedMap)
          === fromAnything[Any](Seq(
            Seq(0, 1, 2, null, "x"),
            Seq(10, 11, 12, null, "y")
          )))
        intercept[IAEx] {
          table.withCol(-1, SeqOfSome("x", "y").toDefinedMap)
        }
      }

      def `replace col (seq)` = {
        assert(table.withCol(0, SeqOfSome("x", "y")) === fromAnything[Any](Seq(
          Seq("x", 1, 2),
          Seq("y", 11, 12)
        )))
        assert(table.withCol(0, SeqOfSome("x", "y", "z", "!")) === fromAnything[Any](Seq(
          Seq("x", 1, 2),
          Seq("y", 11, 12),
          Seq("z"),
          Seq("!")
        )))
        assert(table.withCol(4, SeqOfSome("x", "y"))
          === fromAnything[Any](Seq(
            Seq(0, 1, 2, null, "x"),
            Seq(10, 11, 12, null, "y")
          )))
        assert(table.withCol(4, ISeq(Some("x"), Some("y"), None, None, None))
          === fromAnything[Any](Seq(
            Seq(0, 1, 2, null, "x"),
            Seq(10, 11, 12, null, "y"),
            Seq(null, null, null, null, null),
            Seq(null, null, null, null, null),
            Seq(null, null, null, null, null)
          )))
        intercept[IAEx] {
          table.withCol(-1, SeqOfSome("x", "y"))
        }
      }

      def `insert col (map)` = {
        assert(table.withInsertedCol(0, SeqOfSome("x", "y").toDefinedMap) === fromAnything[Any](Seq(
          Seq("x", 0, 1, 2),
          Seq("y", 10, 11, 12)
        )))
        assert(table.withInsertedCol(0, SeqOfSome("x", "y", "z", "!").toDefinedMap) === fromAnything[Any](Seq(
          Seq("x", 0, 1, 2),
          Seq("y", 10, 11, 12),
          Seq("z"),
          Seq("!")
        )))
        assert(table.withInsertedCol(4, SeqOfSome("x", "y").toDefinedMap)
          === fromAnything[Any](Seq(
            Seq(0, 1, 2, null, "x"),
            Seq(10, 11, 12, null, "y")
          )))
        assert(table.withInsertedCol(4, ISeq(Some("x"), None, None, None, Some("y")).toDefinedMap)
          === fromAnything[Any](Seq(
            Seq(0, 1, 2, null, "x"),
            Seq(10, 11, 12, null, null),
            Seq(null, null, null, null, null),
            Seq(null, null, null, null, null),
            Seq(null, null, null, null, "y")
          )))
        intercept[IAEx] { table.withInsertedCol(-1, SeqOfSome("x", "y").toDefinedMap) }
      }

      def `insert col (seq)` = {
        assert(table.withInsertedCol(0, SeqOfSome("x", "y")) === fromAnything[Any](Seq(
          Seq("x", 0, 1, 2),
          Seq("y", 10, 11, 12)
        )))
        assert(table.withInsertedCol(0, SeqOfSome("x", "y", "z", "!")) === fromAnything[Any](Seq(
          Seq("x", 0, 1, 2),
          Seq("y", 10, 11, 12),
          Seq("z"),
          Seq("!")
        )))
        assert(table.withInsertedCol(4, SeqOfSome("x", "y"))
          === fromAnything[Any](Seq(
            Seq(0, 1, 2, null, "x"),
            Seq(10, 11, 12, null, "y")
          )))
        assert(table.withInsertedCol(4, ISeq(Some("x"), Some("y"), None, None, None))
          === fromAnything[Any](Seq(
            Seq(0, 1, 2, null, "x"),
            Seq(10, 11, 12, null, "y"),
            Seq(null, null, null, null, null),
            Seq(null, null, null, null, null),
            Seq(null, null, null, null, null)
          )))
        intercept[IAEx] { table.withInsertedCol(-1, SeqOfSome("x", "y")) }
      }

      def `swap ` = {
        assert(table.swapRows(0, 0) === table)
        assert(table.swapRows(1, 1) === table)
        assert(table.swapCols(0, 0) === table)
        assert(table.swapCols(1, 1) === table)
        assert(table.swapCols(2, 2) === table)
        assert(table.swapRows(0, 1) === fromAnything[Int](Seq(
          Seq(10, 11, 12),
          Seq(0, 1, 2)
        )))
        assert(table.swapRows(0, 1) === table.swapRows(1, 0))
        assert(table.swapCols(0, 1) === fromAnything[Int](Seq(
          Seq(1, 0, 2),
          Seq(11, 10, 12)
        )))
        assert(table.swapCols(0, 2) === fromAnything[Int](Seq(
          Seq(2, 1, 0),
          Seq(12, 11, 10)
        )))
        intercept[IAEx] { table.swapRows(0, 2) }
        intercept[IAEx] { table.swapCols(0, 3) }
        intercept[IAEx] { table.swapRows(-1, 0) }
        intercept[IAEx] { table.swapCols(0, -1) }
      }

      def `sort rows` = {
        val table = fromAnything[String](Seq(
          Seq("d", "1"),
          Seq("c", "2"),
          Seq("a", "4"),
          Seq("b", "3")
        ))
        assert(table.sortRowsBy(k => table.row(k)(0)) === fromAnything[String](Seq(
          Seq("a", "4"),
          Seq("b", "3"),
          Seq("c", "2"),
          Seq("d", "1")
        )))
        assert(table.sortRowsBy(k => table.row(k)(1)) === fromAnything[String](Seq(
          Seq("d", "1"),
          Seq("c", "2"),
          Seq("b", "3"),
          Seq("a", "4")
        )))
      }

      def `sort cols` = {
        val table = fromAnything[String](Seq(
          Seq("d", "b", "a", "c"),
          Seq("1", "3", "4", "2")
        ))
        assert(table.sortColsBy(k => table.col(k)(0)) === fromAnything[String](Seq(
          Seq("a", "b", "c", "d"),
          Seq("4", "3", "2", "1")
        )))
        assert(table.sortColsBy(k => table.col(k)(1)) === fromAnything[String](Seq(
          Seq("d", "c", "b", "a"),
          Seq("1", "2", "3", "4")
        )))
      }
    }
  }

  object `3x4 sparse table -` {
    val table = fromAnything[Int](Seq(
      Seq(0, 1, null, null),
      Seq(null, 11, null),
      Seq(null)
    ))

    def `equals when constructed with padding` = {
      val table2 =
        assert(table === fromAnything[Int](Seq(
          Seq(0, 1, null),
          Seq(null, 11),
          Seq(null, null, null, null)
        )))
    }

    def `transposition ` = {
      assert(table.transpose === fromAnything[Int](Seq(
        Seq(0, null, null),
        Seq(1, 11, null),
        Seq(null, null, null),
        Seq(null, null, null)
      )))
    }

    def `swap ` = {
      assert(table.swapRows(0, 1) === table.swapRows(1, 0))
      assert(table.swapCols(0, 1) === table.swapCols(1, 0))
      assert(table.swapRows(0, 1) === fromAnything[Int](Seq(
        Seq(null, 11, null),
        Seq(0, 1, null, null),
        Seq(null)
      )))
      assert(table.swapCols(0, 1) === fromAnything[Int](Seq(
        Seq(1, 0, null, null),
        Seq(11, null, null),
        Seq(null)
      )))
      assert(table.swapCols(0, 2) === fromAnything[Int](Seq(
        Seq(null, 1, 0, null),
        Seq(null, 11, null),
        Seq(null)
      )))
    }

    def `string representation` = {
      assert(table.toString === """|+-+-+--+-+-+
                                   || |0|1 |2|3|
                                   |+-+-+--+-+-+
                                   ||0|0|1 | | |
                                   |+-+-+--+-+-+
                                   ||1| |11| | |
                                   |+-+-+--+-+-+
                                   ||2| |  | | |
                                   |+-+-+--+-+-+""".stripMargin)
    }
  }

  object `table with nulls -` {
    val table = IST.fromValues(Seq(
      Seq("x", null),
      Seq(null, "y")
    ))

    def `string representation` = {
      assert(table.toString === """|+-+----+----+
                                   || |0   |1   |
                                   |+-+----+----+
                                   ||0|x   |null|
                                   |+-+----+----+
                                   ||1|null|y   |
                                   |+-+----+----+""".stripMargin)
    }
  }

  //
  // Helpers
  //

  private def fromAnything[A: ClassTag](vals: Seq[Seq[Any]]): IndexedSeqTable[A] = {
    IST.fromRows[A](vals.map(_.map {
      case null         => None
      case v: Option[_] => fail(s"Unexpected option: $v")
      case v: A         => Some(v)
      case v            => fail(s"Unexpected element: $v")
    }))
  }

  private def SeqOfSome[A](as: A*): IndexedSeq[Option[A]] = {
    IndexedSeq(as: _*).map(Some.apply)
  }
}
