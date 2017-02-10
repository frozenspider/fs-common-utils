package org.fs.utility.collection.table

import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner
import org.fs.utility.Implicits._
import scala.collection.immutable.ListMap

@RunWith(classOf[JUnitRunner])
class MapKeyTableSpec extends Spec {
  type IAEx = IllegalArgumentException

  val MKT = MapKeyTable
  val LM = ListMap

  def `empty table` = {
    val table = MKT.empty[Int, String, Nothing]
    assert(table.isEmpty)
    assert(table.sizes == (0, 0))
    assert(table.count == 0)
    assert(table.rowKeys.isEmpty)
    assert(table.colKeys.isEmpty)
    assert(table.isDefinedAt(0, "a") == false)
    assert(table.isDefinedAt((0, "a")) == false)
    assert(table.get(0, "a") == None)
    assert(table.get((0, "a")) == None)
    intercept[IAEx]{ table(0, "a") }
    intercept[IAEx]{ table((0, "a")) }
    assert(table.row(0) == LM.empty)
    assert(table.col("a") == LM.empty)
  }

  def `empty untrimmed table` = {
    val table = MKT.fromRows[Int, String, Boolean](
      Map(
        0 -> Map("a" -> true, "b" -> false),
        1 -> Map("a" -> true, "b" -> false),
        2 -> Map("a" -> true, "b" -> false)
      )
    ).filter(_ => false)
    assert(table.isEmpty)
    assert(table.sizes == (3, 2))
    assert(table.count == 0)
    assert(table.isDefinedAt(0, "a") == false)
    assert(table.isDefinedAt((0, "a")) == false)
    assert(table.isDefinedAt(-1, "z") == false)
    assert(table.get(0, "a") == None)
    assert(table.get((0, "a")) == None)
    assert(table.get(-1, "z") == None)
    intercept[IAEx]{ table(0, "a") }
    intercept[IAEx]{ table((0, "a")) }
    intercept[IAEx]{ table(-1, "z") }
    assert(table.row(0) == LM.empty)
    assert(table.col("a") == LM.empty)
    assert(table.row(-1) == LM.empty)
    assert(table.col("z") == LM.empty)
  }

  object `construction - ` {
    def `trailing empty values are not skipped` = {
      assert(
        MKT.fromRows[Int, String, Boolean](LM(
          0 -> LM("a" -> true, "b" -> true, "c" -> true),
          1 -> LM("a" -> false, "b" -> false, "c" -> false),
          2 -> LM(),
          3 -> LM()
        )).sizes == (4, 3)
      )
    }
  }
  /*
  object `2x3 dense table -` {
    val table = IST.fromRows(Seq(
      SeqOfSome(0, 1, 2),
      SeqOfSome(10, 11, 12)
    ))

    def `size and element getters` = {
      assert(!table.isEmpty)
      assert(table.sizes == (2, 3))
      assert(table.count == 6)
      for {
        r <- 0 to 1
        c <- 0 to 2
      } {
        assert(table.isDefinedAt(r, c))
        val expectedValue = r * 10 + c
        assert(table(r, c) == expectedValue)
        assert(table.get(r, c) == Some(expectedValue))
      }
      assert(table.get(0, 3) == None)
      assert(table.get(2, 0) == None)
      intercept[IAEx]{
        table(0, 3)
      }
      intercept[IAEx]{
        table(2, 0)
      }
    }

    def `elements sequence` = {
      assert(table.elements == Seq(0, 1, 2, 10, 11, 12))
      assert(table.elementsWithIndices == Seq(
        (0, 0, 0),
        (0, 1, 1),
        (0, 2, 2),
        (1, 0, 10),
        (1, 1, 11),
        (1, 2, 12)
      ))
    }

    def `transposition ` = {
      assert(table.transpose == IST.fromRows(Seq(
        SeqOfSome(0, 10),
        SeqOfSome(1, 11),
        SeqOfSome(2, 12)
      )))
    }

    def `adding/replacing elements` = {
      val table2 = table + (0, 3, "x")
      assert(table2 == IST.fromRows(Seq(
        SeqOfSome(0, 1, 2, "x"),
        SeqOfSome(10, 11, 12)
      )))
      assert(table2.sizes == (2, 4))

      val table3 = table2 + (3, 0, "y")
      assert(table3 == IST.fromRows(Seq(
        SeqOfSome(0, 1, 2, "x"),
        SeqOfSome(10, 11, 12),
        SeqOfSome(),
        SeqOfSome("y")
      )))
      assert(table3.sizes == (4, 4))

      val table4 = table3 + (0, 0, "z")
      assert(table4 == IST.fromRows(Seq(
        SeqOfSome("z", 1, 2, "x"),
        SeqOfSome(10, 11, 12),
        SeqOfSome(),
        SeqOfSome("y")
      )))
      assert(table4.sizes == (4, 4))

      val table5 = table4 + (2, 2, "!")
      assert(table5 == IST.fromRows(Seq(
        SeqOfSome("z", 1, 2, "x"),
        SeqOfSome(10, 11, 12),
        Seq(None, None, Some("!")),
        SeqOfSome("y")
      )))
      assert(table5.sizes == (4, 4))
    }

    def `removing elements` = {
      val table2 = table - (0, 0)
      assert(table2 == IST.fromRows(Seq(
        Seq(None, Some(1), Some(2)),
        Seq(Some(10), Some(11), Some(12))
      )))
      assert(table2.sizes == (2, 3))

      val table3 = table2 - (0, 2)
      assert(table3 == IST.fromRows(Seq(
        Seq(None, Some(1)),
        Seq(Some(10), Some(11), Some(12))
      )))
      assert(table3.sizes == (2, 3))

      val table4 = table3 - (1, 2)
      assert(table4 == IST.fromRows(Seq(
        Seq(None, Some(1), None),
        Seq(Some(10), Some(11), None)
      )))
      assert(table4.sizes == (2, 3))
      assert(table4.trim.sizes == (2, 2))

      val table5 = table4 - (1, 0) - (1, 1)
      assert(table5 == IST.fromRows(Seq(
        Seq(None, Some(1), None),
        Seq(None, None, None)
      )))
      assert(table5.sizes == (2, 3))
      assert(table5.trim.sizes == (1, 2))
    }

    def `removing non-existing element` = {
      assert((table - (5, 9)) == table)
      assert((table - (0, 0) - (0, 0) - (0, 0)) == (table - (0, 0)))
    }

    def `concatenating tables` = {
      assert(
        table
          ++ IST.fromRows(Seq(
            Seq(None, Some("x"))
          ))
          == IST.fromRows(Seq(
            SeqOfSome(0, "x", 2),
            SeqOfSome(10, 11, 12)
          ))
      )
      assert(
        table
          ++ IST.fromRows(Seq(
            Seq(),
            Seq(),
            Seq(),
            Seq(None, Some("x"))
          ))
          == IST.fromRows(Seq(
            SeqOfSome(0, 1, 2),
            SeqOfSome(10, 11, 12),
            SeqOfSome(),
            Seq(None, Some("x"))
          ))
      )
    }

    def `equality ` = {
      assert(table != null)
      assert(table != "bang!")
      assert(table == table)
      assert(table == (table + (0, 0, 0)))
      assert(table == (table + (1, 1, 11)))
      assert(table != (table - (0, 0)))
      assert(table != table.transpose)
      assert(table == table.transpose.transpose)
      assert(table != table.withoutRow(0))
      assert(table == table.withoutRow(2))
      assert(table != table.withoutCol(0))
      assert(table == table.withoutCol(3))
      assert(table == table.withoutRow(0).withRow(0, table.row(0)))
      assert(table == table.withoutCol(0).withCol(0, table.col(0)))
      assert(table != table.swapRows(0, 1))
      assert(table != table.swapCols(0, 2))
    }

    def `string representation` = {
      assert(table.toString == """|+-+--+--+--+
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
        assert(seq == Seq(0, 1, 2, 10, 11, 12))
      }

      def `filter ` = {
        val tableOfOdds = table.filter(_ % 2 != 0)
        assert(tableOfOdds == IST.fromRows(Seq(
          Seq(None, Some(1), None),
          Seq(None, Some(11), None)
        )))
        assert(tableOfOdds.filter(_ < 10) == IST.fromRows(Seq(
          Seq(None, Some(1), None),
          Seq(None, None, None)
        )))
        assert(tableOfOdds.filter(_ > 10) == IST.fromRows(Seq(
          Seq(None, None, None),
          Seq(None, Some(11), None)
        )))
      }

      def `map and mapWithIndex` = {
        assert(table.map(_ * 10) == IST.fromRows(Seq(
          SeqOfSome(0, 10, 20),
          SeqOfSome(100, 110, 120)
        )))
        assert(table.mapWithIndex{
          case (r, c, v) => (r * 1000) + (c * 100) + v
        } == IST.fromRows(Seq(
          SeqOfSome(0, 101, 202),
          SeqOfSome(1010, 1111, 1212)
        )))
      }
    }

    object `row/col -` {
      def `getters (as map)` = {
        assert(table.row(0) == LM(0 -> 0, 1 -> 1, 2 -> 2))
        assert(table.row(1) == LM(0 -> 10, 1 -> 11, 2 -> 12))
        assert(table.col(0) == LM(0 -> 0, 1 -> 10))
        assert(table.col(1) == LM(0 -> 1, 1 -> 11))
        assert(table.col(2) == LM(0 -> 2, 1 -> 12))
        assert((table + (0, 3, "x")).row(0) == LM(0 -> 0, 1 -> 1, 2 -> 2, 3 -> "x"))
        assert((table + (0, 3, "x")).row(1) == LM(0 -> 10, 1 -> 11, 2 -> 12))
        assert((table + (2, 0, "y")).col(0) == LM(0 -> 0, 1 -> 10, 2 -> "y"))
        assert((table + (2, 0, "y")).col(1) == LM(0 -> 1, 1 -> 11))
      }

      def `getters (as seq)` = {
        assert(table.rowAsSeq(0) == SeqOfSome(0, 1, 2))
        assert(table.rowAsSeq(1) == SeqOfSome(10, 11, 12))
        assert(table.colAsSeq(0) == SeqOfSome(0, 10))
        assert(table.colAsSeq(1) == SeqOfSome(1, 11))
        assert(table.colAsSeq(2) == SeqOfSome(2, 12))
        assert((table + (0, 3, "x")).rowAsSeq(0) == SeqOfSome(0, 1, 2, "x"))
        assert((table + (0, 3, "x")).rowAsSeq(1) == Seq(Some(10), Some(11), Some(12), None))
        assert((table + (2, 0, "y")).colAsSeq(0) == SeqOfSome(0, 10, "y"))
        assert((table + (2, 0, "y")).colAsSeq(1) == Seq(Some(1), Some(11), None))
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
      }

      def `removal ` = {
        def testTable[A](t: IndexedTable[A], sizes: (Int, Int), values: Seq[Int]) = {
          assert(t.sizes == sizes)
          assert(t.elements == values)
        }
        val m = Map(1 -> 2)
        m.map(x => false)
        testTable(table.withoutRow(0), (1, 3), Seq(10, 11, 12))
        testTable(table.withoutRow(1), (1, 3), Seq(0, 1, 2))
        testTable(table.withoutCol(0), (2, 2), Seq(1, 2, 11, 12))
        testTable(table.withoutCol(1), (2, 2), Seq(0, 2, 10, 12))
        testTable(table.withoutCol(2), (2, 2), Seq(0, 1, 10, 11))
        assert(table.withoutRow(1) != table)
        assert(table.withoutRow(2) == table)
        assert(table.withoutRow(3) == table)
        assert(table.withoutCol(2) != table)
        assert(table.withoutCol(3) == table)
        assert(table.withoutCol(4) == table)
      }

      def `replace row (map)` = {
        assert(table.withRow(0, SeqOfSome("x", "y", "z").toDefinedMap) == IST.fromRows(Seq(
          SeqOfSome("x", "y", "z"),
          SeqOfSome(10, 11, 12)
        )))
        assert(table.withRow(0, SeqOfSome("x", "y", "z", "!").toDefinedMap) == IST.fromRows(Seq(
          SeqOfSome("x", "y", "z", "!"),
          SeqOfSome(10, 11, 12)
        )))
        assert(table
          .withRow(0, SeqOfSome("x", "y").toDefinedMap)
          .withRow(1, SeqOfSome("z", "!").toDefinedMap)
          == IST.fromRows(Seq(
            SeqOfSome("x", "y"),
            SeqOfSome("z", "!")
          )))
        assert(table.withRow(0, ISeq(Some("x"), Some("y"), Some("z")).toDefinedMap) == IST.fromRows(Seq(
          Seq(Some("x"), Some("y"), Some("z")),
          Seq(Some(10), Some(11), Some(12))
        )))
      }

      def `replace row (seq)` = {
        assert(table.withRow(0, SeqOfSome("x", "y", "z")) == IST.fromRows(Seq(
          SeqOfSome("x", "y", "z"),
          SeqOfSome(10, 11, 12)
        )))
        assert(table.withRow(0, SeqOfSome("x", "y", "z", "!")) == IST.fromRows(Seq(
          SeqOfSome("x", "y", "z", "!"),
          SeqOfSome(10, 11, 12)
        )))
        assert(table
          .withRow(0, SeqOfSome("x", "y"))
          .withRow(1, SeqOfSome("z", "!"))
          == IST.fromRows(Seq(
            SeqOfSome("x", "y"),
            SeqOfSome("z", "!")
          )))
        assert(table.withRow(0, ISeq(Some("x"), Some("y"), Some("z"), None, None)) == IST.fromRows(Seq(
          Seq(Some("x"), Some("y"), Some("z"), None, None),
          Seq(Some(10), Some(11), Some(12), None, None)
        )))
      }

      def `replace col (map)` = {
        assert(table.withCol(0, SeqOfSome("x", "y").toDefinedMap) == IST.fromValues(Seq(
          Seq("x", 1, 2),
          Seq("y", 11, 12)
        )))
        assert(table.withCol(0, SeqOfSome("x", "y", "z", "!").toDefinedMap) == IST.fromValues(Seq(
          Seq("x", 1, 2),
          Seq("y", 11, 12),
          Seq("z"),
          Seq("!")
        )))
        assert(table.withCol(4, SeqOfSome("x", "y").toDefinedMap)
          == IST.fromRows(Seq(
            Seq(Some(0), Some(1), Some(2), None, Some("x")),
            Seq(Some(10), Some(11), Some(12), None, Some("y"))
          )))
        assert(table.withCol(4, ISeq(Some("x"), Some("y"), None, None, None).toDefinedMap)
          == IST.fromRows(Seq(
            Seq(Some(0), Some(1), Some(2), None, Some("x")),
            Seq(Some(10), Some(11), Some(12), None, Some("y"))
          )))
      }

      def `replace col (seq)` = {
        assert(table.withCol(0, SeqOfSome("x", "y")) == IST.fromValues(Seq(
          Seq("x", 1, 2),
          Seq("y", 11, 12)
        )))
        assert(table.withCol(0, SeqOfSome("x", "y", "z", "!")) == IST.fromValues(Seq(
          Seq("x", 1, 2),
          Seq("y", 11, 12),
          Seq("z"),
          Seq("!")
        )))
        assert(table.withCol(4, SeqOfSome("x", "y"))
          == IST.fromRows(Seq(
            Seq(Some(0), Some(1), Some(2), None, Some("x")),
            Seq(Some(10), Some(11), Some(12), None, Some("y"))
          )))
        assert(table.withCol(4, ISeq(Some("x"), Some("y"), None, None, None))
          == IST.fromRows(Seq(
            Seq(Some(0), Some(1), Some(2), None, Some("x")),
            Seq(Some(10), Some(11), Some(12), None, Some("y")),
            Seq(None, None, None, None, None),
            Seq(None, None, None, None, None),
            Seq(None, None, None, None, None)
          )))
      }

      def `swap ` = {
        assert(table.swapRows(0, 0) == table)
        assert(table.swapRows(1, 1) == table)
        assert(table.swapCols(0, 0) == table)
        assert(table.swapCols(1, 1) == table)
        assert(table.swapCols(2, 2) == table)
        assert(table.swapRows(0, 1) == IST.fromRows(Seq(
          SeqOfSome(10, 11, 12),
          SeqOfSome(0, 1, 2)
        )))
        assert(table.swapRows(0, 1) == table.swapRows(1, 0))
        assert(table.swapCols(0, 1) == IST.fromRows(Seq(
          SeqOfSome(1, 0, 2),
          SeqOfSome(11, 10, 12)
        )))
        assert(table.swapCols(0, 2) == IST.fromRows(Seq(
          SeqOfSome(2, 1, 0),
          SeqOfSome(12, 11, 10)
        )))
        intercept[IAEx] {
          table.swapRows(0, 2)
        }
        intercept[IAEx] {
          table.swapCols(0, 3)
        }
      }

      def `sort rows` = {
        val table = IST.fromValues(Seq(
          Seq("d", "1"),
          Seq("c", "2"),
          Seq("a", "4"),
          Seq("b", "3")
        ))
        assert(table.sortRowsBy(k => table.row(k)(0)) == IST.fromValues(Seq(
          Seq("a", "4"),
          Seq("b", "3"),
          Seq("c", "2"),
          Seq("d", "1")
        )))
        assert(table.sortRowsBy(k => table.row(k)(1)) == IST.fromValues(Seq(
          Seq("d", "1"),
          Seq("c", "2"),
          Seq("b", "3"),
          Seq("a", "4")
        )))
      }

      def `sort cols` = {
        val table = IST.fromValues(Seq(
          Seq("d", "b", "a", "c"),
          Seq("1", "3", "4", "2")
        ))
        assert(table.sortColsBy(k => table.col(k)(0)) == IST.fromValues(Seq(
          Seq("a", "b", "c", "d"),
          Seq("4", "3", "2", "1")
        )))
        assert(table.sortColsBy(k => table.col(k)(1)) == IST.fromValues(Seq(
          Seq("d", "c", "b", "a"),
          Seq("1", "2", "3", "4")
        )))
      }
    }
  }*/

  object `2x3 sparse table -` {
    val table = MKT.fromRows[Int, String, Int](LM(
      0 -> LM("a" -> 0, "b" -> 1),
      1 -> LM("b" -> 11),
      2 -> LM()
    )) withCol ("c", LM.empty) withCol ("d", LM.empty)

    def `not equals when constructed with different order` = {
      assert(table != MKT.fromRows[Int, String, Int](LM(
        0 -> LM("a" -> 0, "b" -> 1),
        2 -> LM(),
        1 -> LM("b" -> 11)
      )))
      assert(table != MKT.fromRows[Int, String, Int](LM(
        0 -> LM("b" -> 1, "a" -> 0),
        1 -> LM("b" -> 11),
        2 -> LM()
      )))
    }

    def `transposition ` = {
      val table2 = MKT.fromRows[String, Int, Int](LM(
        "a" -> LM(0 -> 0),
        "b" -> LM(0 -> 1, 1 -> 11),
        "c" -> LM(),
        "d" -> LM()
      )) withCol (2, LM.empty)
      assert(table.transpose == table2)
    }

    def `swap ` = {
      assert(table.swapRows(0, 1) == MKT.fromRows[Int, String, Int](LM(
        0 -> LM("b" -> 11),
        1 -> LM("a" -> 0, "b" -> 1),
        2 -> LM()
      )).withCol("c", LM.empty).withCol("d", LM.empty))
      assert(table.swapRows(0, 1) == table.swapRows(1, 0))
      assert(table.swapCols("a", "b") == MKT.fromRows[Int, String, Int](LM(
        0 -> LM("a" -> 11),
        1 -> LM("a" -> 1, "b" -> 0),
        2 -> LM()
      )).withCol("c", LM.empty).withCol("d", Map.empty))
      assert(
        table.swapCols("a", "d") ==
          (MKT.fromRows[Int, String, Int](
            LM(0 -> LM(), 1 -> LM(), 2 -> LM()))
            .withCol("a", LM())
            .withCol("b", LM(0 -> 1, 1 -> 11))
            .withCol("c", LM())
            .withCol("d", LM(0 -> 0)))
      )
      assert(table.swapCols("a", "b") == table.swapCols("b", "a"))
    }
  }

  //
  // Helpers
  //

  private def SeqOfSome[A](as: A*): IndexedSeq[Option[A]] = {
    IndexedSeq(as: _*).map(Some.apply)
  }
}
