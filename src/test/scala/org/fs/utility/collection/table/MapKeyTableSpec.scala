package org.fs.utility.collection.table

import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner
import org.fs.utility.collection.RichCollectionImplicits._
import scala.collection.immutable.ListMap
import scala.reflect.ClassTag

@RunWith(classOf[JUnitRunner])
class MapKeyTableSpec extends Spec {
  import TableEquality._

  type IAEx = IllegalArgumentException

  val MKT = MapKeyTable
  val LM = ListMap

  def `empty table` = {
    val table = MKT.empty[Int, String, Nothing]
    assert(table.isEmpty)
    assert(table.sizes === (0, 0))
    assert(table.count === 0)
    assert(table.rowKeys.isEmpty)
    assert(table.colKeys.isEmpty)
    assert(table.isDefinedAt(0, "a") === false)
    assert(table.isDefinedAt((0, "a")) === false)
    assert(table.get(0, "a") === None)
    assert(table.get((0, "a")) === None)
    intercept[IAEx]{ table(0, "a") }
    intercept[IAEx]{ table((0, "a")) }
    assert(table.row(0) === LM.empty)
    assert(table.col("a") === LM.empty)
    assert(table.toString() === "+")
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
    assert(table.sizes === (3, 2))
    assert(table.count === 0)
    assert(table.isDefinedAt(0, "a") === false)
    assert(table.isDefinedAt((0, "a")) === false)
    assert(table.isDefinedAt(-1, "z") === false)
    assert(table.get(0, "a") === None)
    assert(table.get((0, "a")) === None)
    assert(table.get(-1, "z") === None)
    intercept[IAEx]{ table(0, "a") }
    intercept[IAEx]{ table((0, "a")) }
    intercept[IAEx]{ table(-1, "z") }
    assert(table.row(0) === LM.empty)
    assert(table.col("a") === LM.empty)
    assert(table.row(-1) === LM.empty)
    assert(table.col("z") === LM.empty)
  }

  object `construction - ` {
    def `trailing empty values are not skipped` = {
      assert(
        MKT.fromRows[Int, String, Boolean](LM(
          0 -> LM("a" -> true, "b" -> true, "c" -> true),
          1 -> LM("a" -> false, "b" -> false, "c" -> false),
          2 -> LM(),
          3 -> LM()
        )).sizes === (4, 3)
      )
    }
  }

  object `2x3 dense table -` {
    val table = intStringMKT[Int](Seq(
      Seq(0, 1, 2),
      Seq(10, 11, 12)
    ))

    def `size and element getters` = {
      assert(!table.isEmpty)
      assert(table.sizes === (2, 3))
      assert(table.count === 6)
      for {
        r <- 0 to 1
        ci <- 0 to 2
        val c = Seq("a", "b", "c")(ci)
      } {
        assert(table.isDefinedAt(r, c))
        val expectedValue = r * 10 + ci
        assert(table(r, c) === expectedValue)
        assert(table.get(r, c) === Some(expectedValue))
        assert(table.find(_ == expectedValue) === Some(expectedValue))
        assert(table.findCell(_ == expectedValue) === Some((r, c, expectedValue)))
      }
      assert(table.get(0, "d") === None)
      assert(table.get(2, "a") === None)
      intercept[IAEx]{
        table(0, "d")
      }
      intercept[IAEx]{
        table(2, "a")
      }
    }

    def `elements sequence` = {
      assert(table.elements === Seq(0, 1, 2, 10, 11, 12))
      assert(table.elementsWithIndices === Seq(
        (0, "a", 0),
        (0, "b", 1),
        (0, "c", 2),
        (1, "a", 10),
        (1, "b", 11),
        (1, "c", 12)
      ))
    }

    def `transposition ` = {
      assert(table.transpose === MKT.fromRows[String, Int, Int](LM(
        "a" -> LM(0 -> 0, 1 -> 10),
        "b" -> LM(0 -> 1, 1 -> 11),
        "c" -> LM(0 -> 2, 1 -> 12)
      )))
    }

    def `adding/replacing elements` = {
      val table2 = table + (0, "d", "x")
      assert(table2 === intStringMKT[Any](Seq(
        Seq(0, 1, 2, "x"),
        Seq(10, 11, 12)
      )))
      assert(table2.sizes === (2, 4))

      val table3 = table2 + (2, "a", "y")
      assert(table3 === intStringMKT[Any](Seq(
        Seq(0, 1, 2, "x"),
        Seq(10, 11, 12),
        Seq("y")
      )))
      assert(table3.sizes === (3, 4))

      val table4 = table3 + (0, "a", "z")
      assert(table4 === intStringMKT[Any](Seq(
        Seq("z", 1, 2, "x"),
        Seq(10, 11, 12),
        Seq("y")
      )))
      assert(table4.sizes === (3, 4))

      val table5 = table4 + (3, "c", "!")
      assert(table5 === intStringMKT[Any](Seq(
        Seq("z", 1, 2, "x"),
        Seq(10, 11, 12),
        Seq("y"),
        Seq(null, null, "!")
      )))
      assert(table5.sizes === (4, 4))

      // Alternate signature
      assert(table + (0, "d", "x") === table + ((0, "d"), "x"))
    }

    def `removing elements` = {
      val table2 = table - (0, "a")
      assert(table2 === intStringMKT[Int](Seq(
        Seq(null, 1, 2),
        Seq(10, 11, 12)
      )))
      assert(table2.sizes === (2, 3))

      val table3 = table2 - (0, "c")
      assert(table3 === intStringMKT[Int](Seq(
        Seq(null, 1, null),
        Seq(10, 11, 12)
      )))
      assert(table3.sizes === (2, 3))

      val table4 = table3 - (1, "c")
      assert(table4 === intStringMKT[Int](Seq(
        Seq(null, 1, null),
        Seq(10, 11, null)
      )))
      assert(table4.sizes === (2, 3))
      assert(table4.trim.sizes === (2, 2))

      val table5 = table4 - (1, "a") - (1, "b")
      assert(table5 === intStringMKT[Int](Seq(
        Seq(null, 1, null),
        Seq(null, null, null)
      )))
      assert(table5.sizes === (2, 3))
      assert(table5.trim.sizes === (1, 1))

      // Alternate signature
      assert(table - (0, "a") === table - ((0, "a")))
    }

    def `removing non-existing element` = {
      assert((table - (5, "z")) === table)
      assert((table - (0, "a") - (0, "a") - (0, "a")) === (table - (0, "a")))
    }

    def `concatenating tables` = {
      val t1 = table
      val t2 = intStringMKT[Any](Seq(
        Seq(null, "x")
      ))
      assert(
        table
          ++ intStringMKT[String](Seq(
            Seq(null, "x")
          ))
          === intStringMKT[Any](Seq(
            Seq(0, "x", 2),
            Seq(10, 11, 12)
          ))
      )
      assert(
        table
          ++ intStringMKT[String](Seq(
            Seq(),
            Seq(),
            Seq(),
            Seq(null, "x")
          ))
          === intStringMKT[Any](Seq(
            Seq(0, 1, 2),
            Seq(10, 11, 12),
            Seq(),
            Seq(null, "x")
          ))
      )
    }

    def `equality ` = {
      assert(table !== null)
      assert(table !== "bang!")
      assert(table === table)
      assert(table === (table + (0, "a", 0)))
      assert(table === (table + (1, "b", 11)))
      assert(table !== (table - (0, "a")))
      assert(table !== table.transpose)
      assert(table === table.transpose.transpose)
      assert(table !== table.withoutRow(0))
      assert(table === table.withoutRow(2))
      assert(table !== table.withoutCol("a"))
      assert(table === table.withoutCol("d"))
      assert(table === table.withoutRow(0).withRow(0, table.row(0)).sortedRows)
      assert(table === table.withoutCol("a").withCol("a", table.col("a")).sortedCols)
      assert(table !== table.swapRows(0, 1))
      assert(table !== table.swapCols("a", "c"))
      assert(table !== table.switchRows(0, 1))
      assert(table !== table.switchCols("a", "c"))
    }

    def `string representation` = {
      assert(table.toString === """|+-+--+--+--+
                                   || |a |b |c |
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
        assert(tableOfOdds === intStringMKT[Int](Seq(
          Seq(null, 1, null),
          Seq(null, 11, null)
        )))
        assert(tableOfOdds.filter(_ < 10) === intStringMKT[Int](Seq(
          Seq(null, 1, null),
          Seq(null, null, null)
        )))
        assert(tableOfOdds.filter(_ > 10) === intStringMKT[Int](Seq(
          Seq(null, null, null),
          Seq(null, 11, null)
        )))
      }

      def `map and mapWithIndex` = {
        assert(table.map(_ * 10) === intStringMKT[Int](Seq(
          Seq(0, 10, 20),
          Seq(100, 110, 120)
        )))
        assert(table.mapWithIndex{
          case (r, c, v) => s"$r $c $v"
        } === intStringMKT[String](Seq(
          Seq("0 a 0", "0 b 1", "0 c 2"),
          Seq("1 a 10", "1 b 11", "1 c 12")
        )))
      }
    }

    object `row/col -` {
      def `getters ` = {
        assert(table.row(0) === LM("a" -> 0, "b" -> 1, "c" -> 2))
        assert(table.row(1) === LM("a" -> 10, "b" -> 11, "c" -> 12))
        assert(table.col("a") === LM(0 -> 0, 1 -> 10))
        assert(table.col("b") === LM(0 -> 1, 1 -> 11))
        assert(table.col("c") === LM(0 -> 2, 1 -> 12))
        assert((table + (0, "d", "x")).row(0) === LM("a" -> 0, "b" -> 1, "c" -> 2, "d" -> "x"))
        assert((table + (0, "d", "x")).row(1) === LM("a" -> 10, "b" -> 11, "c" -> 12))
        assert((table + (2, "a", "y")).col("a") === LM(0 -> 0, 1 -> 10, 2 -> "y"))
        assert((table + (2, "a", "y")).col("b") === LM(0 -> 1, 1 -> 11))
      }

      def `is empty` = {
        assert(!table.isRowEmpty(0))
        assert(!table.isRowEmpty(1))
        assert(table.isRowEmpty(2))
        assert(table.isRowEmpty(-1))
        assert(table.isRowEmpty(100500))
        assert(!table.isColEmpty("a"))
        assert(!table.isColEmpty("b"))
        assert(!table.isColEmpty("c"))
        assert(table.isColEmpty("d"))
      }

      def `removal ` = {
        def testTable[A](t: KeyTable[Int, String, A], sizes: (Int, Int), values: Seq[Int]) = {
          assert(t.sizes === sizes)
          assert(t.elements === values)
        }
        val m = Map(1 -> 2)
        m.map(x => false)
        testTable(table.withoutRow(0), (1, 3), Seq(10, 11, 12))
        testTable(table.withoutRow(1), (1, 3), Seq(0, 1, 2))
        testTable(table.withoutCol("a"), (2, 2), Seq(1, 2, 11, 12))
        testTable(table.withoutCol("b"), (2, 2), Seq(0, 2, 10, 12))
        testTable(table.withoutCol("c"), (2, 2), Seq(0, 1, 10, 11))
        assert(table.withoutRow(1) != table)
        assert(table.withoutRow(2) === table)
        assert(table.withoutRow(3) === table)
        assert(table.withoutCol("c") != table)
        assert(table.withoutCol("d") === table)
      }

      object `with row -` {
        def `replace first row, same length` = {
          assert(table.withRow(0, LM("a" -> "x", "b" -> "y", "c" -> "z")) === intStringMKT[Any](Seq(
            Seq("x", "y", "z"),
            Seq(10, 11, 12)
          )))
        }

        def `replace first row with bigger one` = {
          assert(table.withRow(0, LM("a" -> "x", "b" -> "y", "c" -> "z", "d" -> "!")) === intStringMKT[Any](Seq(
            Seq("x", "y", "z", "!"),
            Seq(10, 11, 12)
          )))
        }

        def `add row after empty` = {
          assert(table.withEmptyRow(2).withRow(3, LM("a" -> "x", "b" -> "y", "c" -> "z"))
            === intStringMKT[Any](Seq(
              Seq(0, 1, 2),
              Seq(10, 11, 12),
              Seq(),
              Seq("x", "y", "z")
            )))
        }

        def `replace all rows leaving empty rows/cols` = {
          assert(table
            .withRow(0, LM("a" -> "x", "b" -> "y"))
            .withRow(1, LM())
            === intStringMKT[String](Seq(
              Seq("x", "y", null),
              Seq()
            )))
        }
      }

      object `with col -` {
        def `replace first col, same length` = {
          assert(table.withCol("a", LM(0 -> "x", 1 -> "y")) === intStringMKT[Any](Seq(
            Seq("x", 1, 2),
            Seq("y", 11, 12)
          )))
        }

        def `replace first col with bigger one` = {
          assert(table.withCol("a", LM(0 -> "x", 1 -> "y", 2 -> "z", 3 -> "!")) === intStringMKT[Any](Seq(
            Seq("x", 1, 2),
            Seq("y", 11, 12),
            Seq("z"),
            Seq("!")
          )))
        }

        def `add col after empty` = {
          assert(table.withEmptyCol("d").withCol("e", LM(0 -> "x", 1 -> "y"))
            === intStringMKT[Any](Seq(
              Seq(0, 1, 2, null, "x"),
              Seq(10, 11, 12, null, "y")
            )))
        }

        def `replace all cols leaving empty rows/cols` = {
          assert(table
            .withCol("a", LM(0 -> "a0"))
            .withCol("b", LM(0 -> "b0"))
            .withCol("c", LM())
            === intStringMKT[String](Seq(
              Seq("a0", "b0", null),
              Seq()
            )))
        }
      }

      def `swap ` = {
        assert(table.swapRows(0, 0) === table)
        assert(table.swapRows(1, 1) === table)
        assert(table.swapCols("a", "a") === table)
        assert(table.swapCols("b", "b") === table)
        assert(table.swapCols("c", "c") === table)
        assert(table.swapRows(0, 1) === intStringMKT[Int](Seq(
          Seq(10, 11, 12),
          Seq(0, 1, 2)
        )))
        assert(table.swapRows(0, 1) === table.swapRows(1, 0))
        assert(table.swapCols("a", "b") === intStringMKT[Int](Seq(
          Seq(1, 0, 2),
          Seq(11, 10, 12)
        )))
        assert(table.swapCols("a", "c") === intStringMKT[Int](Seq(
          Seq(2, 1, 0),
          Seq(12, 11, 10)
        )))
        intercept[IAEx] {
          table.swapRows(0, 2)
        }
        intercept[IAEx] {
          table.swapCols("a", "d")
        }
      }

      def `switch ` = {
        assert(table.switchRows(0, 0) === table)
        assert(table.switchRows(1, 1) === table)
        assert(table.switchCols("a", "a") === table)
        assert(table.switchCols("b", "b") === table)
        assert(table.switchCols("c", "c") === table)
        assert(table.switchRows(0, 1) === MKT.fromRows(LM(
          1 -> LM("a" -> 10, "b" -> 11, "c" -> 12),
          0 -> LM("a" -> 0, "b" -> 1, "c" -> 2)
        )))
        assert(table.switchRows(0, 1) === table.switchRows(1, 0))
        assert(table.switchCols("a", "b") === MKT.fromRows(LM(
          0 -> LM("b" -> 1, "a" -> 0, "c" -> 2),
          1 -> LM("b" -> 11, "a" -> 10, "c" -> 12)
        )))
        assert(table.switchCols("a", "c") === MKT.fromRows(LM(
          0 -> LM("c" -> 2, "b" -> 1, "a" -> 0),
          1 -> LM("c" -> 12, "b" -> 11, "a" -> 10)
        )))
        intercept[IAEx] {
          table.switchRows(0, 2)
        }
        intercept[IAEx] {
          table.switchCols("a", "d")
        }
      }

      object `sort ` {
        def `rows (default)` = {
          val table = MKT.fromRows(LM(
            5 -> LM("b" -> "55", "a" -> "555"),
            1 -> LM("b" -> "11", "a" -> "111"),
            3 -> LM("b" -> "33", "a" -> "333"),
            4 -> LM("b" -> "44", "a" -> "444")
          ))
          assert(table.sortedRows === MKT.fromRows(LM(
            1 -> LM("b" -> "11", "a" -> "111"),
            3 -> LM("b" -> "33", "a" -> "333"),
            4 -> LM("b" -> "44", "a" -> "444"),
            5 -> LM("b" -> "55", "a" -> "555")
          )))
        }

        def `cols (default)` = {
          val table = MKT.fromRows(LM(
            1 -> LM("b" -> "22", "d" -> "44", "a" -> "11", "c" -> "33"),
            0 -> LM("b" -> "222", "d" -> "444", "a" -> "111", "c" -> "333")
          ))
          assert(table.sortedCols === MKT.fromRows(LM(
            1 -> LM("a" -> "11", "b" -> "22", "c" -> "33", "d" -> "44"),
            0 -> LM("a" -> "111", "b" -> "222", "c" -> "333", "d" -> "444")
          )))
        }

        def `rows by` = {
          val table = intStringMKT[String](Seq(
            Seq("d", "1"),
            Seq("c", "2"),
            Seq("a", "4"),
            Seq("b", "3")
          ))
          assert(table.sortRowsBy(k => table.row(k)("a")) === MKT.fromRows(LM(
            2 -> LM("a" -> "a", "b" -> "4"),
            3 -> LM("a" -> "b", "b" -> "3"),
            1 -> LM("a" -> "c", "b" -> "2"),
            0 -> LM("a" -> "d", "b" -> "1")
          )))
          assert(table.sortRowsBy(k => table.row(k)("b")) === MKT.fromRows(LM(
            0 -> LM("a" -> "d", "b" -> "1"),
            1 -> LM("a" -> "c", "b" -> "2"),
            3 -> LM("a" -> "b", "b" -> "3"),
            2 -> LM("a" -> "a", "b" -> "4")
          )))
        }

        def `cols by` = {
          val table = intStringMKT[String](Seq(
            Seq("d", "b", "a", "c"),
            Seq("1", "3", "4", "2")
          ))
          assert(table.sortColsBy(k => table.col(k)(0)) === MKT.fromRows(LM(
            0 -> LM("c" -> "a", "b" -> "b", "d" -> "c", "a" -> "d"),
            1 -> LM("c" -> "4", "b" -> "3", "d" -> "2", "a" -> "1")
          )))
          assert(table.sortColsBy(k => table.col(k)(1)) === MKT.fromRows(LM(
            0 -> LM("a" -> "d", "d" -> "c", "b" -> "b", "c" -> "a"),
            1 -> LM("a" -> "1", "d" -> "2", "b" -> "3", "c" -> "4")
          )))
        }
      }
    }
  }

  object `3x4 sparse table -` {
    val table = intStringMKT[Int](Seq(
      Seq(0, 1, null, null),
      Seq(null, 11),
      Seq()
    ))

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
      assert(table.transpose === table2)
    }

    def `swap ` = {
      assert(table.swapRows(0, 1) === table.swapRows(1, 0))
      assert(table.swapCols("a", "b") === table.swapCols("b", "a"))
      assert(
        table.swapRows(0, 1) ==
          intStringMKT[Int](Seq(
            Seq(null, 11),
            Seq(0, 1, null, null),
            Seq()
          ))
      )
      assert(
        table.swapCols("a", "b") ==
          intStringMKT[Int](Seq(
            Seq(1, 0, null, null),
            Seq(11),
            Seq()
          ))
      )
      assert(
        table.swapCols("a", "d") ==
          intStringMKT[Int](Seq(
            Seq(null, 1, null, 0),
            Seq(null, 11),
            Seq()
          ))
      )
    }

    def `switch ` = {
      assert(table.switchRows(0, 1) === table.switchRows(1, 0))
      assert(table.switchCols("a", "b") === table.switchCols("b", "a"))
      assert(
        table.switchRows(0, 1) ==
          (MKT.fromRows[Int, String, Int](
            LM(1 -> LM(), 0 -> LM(), 2 -> LM()))
            .withCol("a", LM(0 -> 0))
            .withCol("b", LM(0 -> 1, 1 -> 11))
            .withCol("c", LM())
            .withCol("d", LM()))
      )
      assert(
        table.switchCols("a", "b") ==
          (MKT.fromRows[Int, String, Int](
            LM(0 -> LM(), 1 -> LM(), 2 -> LM()))
            .withCol("b", LM(0 -> 1, 1 -> 11))
            .withCol("a", LM(0 -> 0))
            .withCol("c", LM())
            .withCol("d", LM()))
      )
      assert(
        table.switchCols("a", "d") ==
          (MKT.fromRows[Int, String, Int](
            LM(0 -> LM(), 1 -> LM(), 2 -> LM()))
            .withCol("d", LM())
            .withCol("b", LM(0 -> 1, 1 -> 11))
            .withCol("c", LM())
            .withCol("a", LM(0 -> 0)))
      )
    }
  }

  object `table with nulls -` {
    val table = MKT.fromRows(LM(
      "a" -> LM(1 -> "x", 2 -> null),
      "b" -> LM(1 -> null, 2 -> "y")
    ))

    def `string representation` = {
      assert(table.toString === """|+-+----+----+
                                   || |1   |2   |
                                   |+-+----+----+
                                   ||a|x   |null|
                                   |+-+----+----+
                                   ||b|null|y   |
                                   |+-+----+----+""".stripMargin)
    }
  }

  //
  // Helpers
  //

  private def intStringMKT[A: ClassTag](els: Seq[Seq[Any]]): KeyTable[Int, String, A] = {
    val rowIndices = 0 until els.size
    val charStrings = 'a' to 'z' map (_.toString)
    val colIndices = charStrings take els.map(_.size).max
    val table: KeyTable[Int, String, A] = {
      val table1: KeyTable[Int, String, A] = MKT.empty
      val table2 = rowIndices.foldLeft(table1)((t, r) => t.withEmptyRow(r))
      val table3 = colIndices.foldLeft(table2)((t, c) => t.withEmptyCol(c))
      table3
    }
    val result = els.zipWithIndex.foldLeft(table) {
      case (t, (row, r)) =>
        row.zipWithIndex.foldLeft(t) {
          case (_, (el: Option[_], _))    => fail(s"Unexpected option in row $row: $el")
          case (t, (el: A, ci))           => t + (r, colIndices(ci), el)
          case (t, (el, _)) if el == null => t
          case (_, (el, _))               => fail(s"Unexpected element in row $row: $el")
        }
    }
    result
  }
}
