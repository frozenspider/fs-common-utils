package org.fs.utility.collection.table

import scala.collection.immutable.ListMap
import MapKeyTable.Curried

class MapKeyTable[RKT, CKT, +A] private (
  rows: Map[RKT, Map[CKT, A]],
  override val rowKeys: IndexedSeq[RKT],
  override val colKeys: IndexedSeq[CKT])
    extends KeyTable[RKT, CKT, A]
    with GenTableLike[RKT, CKT, A, Curried[RKT, CKT]#Self, Curried[CKT, RKT]#Self] {
  import MapKeyTable.asListMap

  //
  // Retrieve
  //

  override lazy val sizes: (Int, Int) =
    (rowKeys.size, colKeys.size)

  override lazy val count: Int =
    rows.map(_._2.size).sum

  override def isRowEmpty(r: RKT): Boolean =
    rows.get(r) map (_.isEmpty) getOrElse true

  override def isColEmpty(c: CKT): Boolean = {
    rows forall {
      case (_, row) => !(row contains c)
    }
  }

  override def isDefinedAt(r: RKT, c: CKT): Boolean =
    rows.contains(r) && rows(r).contains(c)

  override def get(r: RKT, c: CKT): Option[A] =
    for {
      row <- rows.get(r)
      el <- row.get(c)
    } yield el

  override def row(r: RKT): ListMap[CKT, A] = {
    val row = rows.get(r) getOrElse emptyRow
    colKeys.collect {
      case c if row contains c => (c -> row(c))
    }
  }

  override def col(c: CKT): ListMap[RKT, A] = {
    val col = rows collect {
      case (r, row) if row contains c => (r, row(c))
    }
    rowKeys.collect {
      case r if col contains r => (r -> col(r))
    }
  }

  override def withRow[B >: A](r: RKT, row: Map[CKT, B]): MapKeyTable[RKT, CKT, B] = {
    val rows2 = rows + (r -> row)
    new MapKeyTable(rows2, (rowKeys :+ r).distinct, (colKeys ++ row.keySet).distinct)
  }

  override def withCol[B >: A](c: CKT, col: Map[RKT, B]): MapKeyTable[RKT, CKT, B] = {
    val newEls = col filterKeys (r => !rows.contains(r))
    val newRows = newEls mapValues (el => Map(c -> el))
    val updatedRows = rows map {
      case (r, row) if col contains r => (r, row + (c -> col(r)))
      case (r, row)                   => (r, row - c)
    }
    val rows2 = updatedRows ++ newRows
    new MapKeyTable(rows2, (rowKeys ++ col.keySet).distinct, (colKeys :+ c).distinct)
  }

  override def withoutRow(r: RKT): MapKeyTable[RKT, CKT, A] =
    if (rowKeys contains r)
      new MapKeyTable(rows - r, rowKeys filter (_ != r), colKeys)
    else
      this

  override def withoutCol(c: CKT): MapKeyTable[RKT, CKT, A] =
    if (colKeys contains c) {
      val rows2 = rows mapValues (row => row - c)
      new MapKeyTable(rows2, rowKeys, colKeys filter (_ != c))
    } else {
      this
    }

  override def elementsWithIndices: Seq[(RKT, CKT, A)] =
    for {
      r <- rowKeys if rows contains r
      val row = rows(r)
      c <- colKeys if row contains c
    } yield (r, c, row(c))

  //
  // Copy-update
  //

  override def +[B >: A](r: RKT, c: CKT, v: B): MapKeyTable[RKT, CKT, B] = {
    val rows2 = rows + (r -> (rows.getOrElse(r, emptyRow) + (c -> v)))
    new MapKeyTable(rows2, (rowKeys :+ r).distinct, (colKeys :+ c).distinct)
  }

  override def ++[B >: A](that: GenTableLike[RKT, CKT, B, ST forSome { type ST[+A2] }, TT forSome { type TT[+A2] }]): MapKeyTable[RKT, CKT, B] = {
    val thatElements = that.elementsWithIndices
    val thatRows = thatElements.groupBy(_._1) map {
      case (r, rowEls) => r -> rowEls.map(tuple => tuple._2 -> tuple._3).toMap
    }
    val newRows = thatRows filterKeys (r => !rows.contains(r))
    val updatedRows = rows map {
      case (r, row) if thatRows contains r => (r, row ++ thatRows(r))
      case (r, row)                        => (r, row)
    }
    val rows2 = updatedRows ++ newRows
    val rowKeys2 = (rowKeys ++ that.rowKeys).distinct
    val colKeys2 = (colKeys ++ that.colKeys).distinct
    new MapKeyTable(rows2, rowKeys2, colKeys2)
  }

  override def -(r: RKT, c: CKT): MapKeyTable[RKT, CKT, A] =
    if ((rowKeys contains r) && (colKeys contains c)) {
      val rows2 = rows map {
        case (r2, row) if r2 == r => (r2, row - c)
        case (r2, row)            => (r2, row)
      }
      new MapKeyTable(rows2, rowKeys, colKeys)
    } else {
      this
    }

  override def transpose: MapKeyTable[CKT, RKT, A] = {
    val emptyTransposedMap: ListMap[CKT, ListMap[RKT, A]] = ListMap.empty
    val cols = elementsWithIndices.foldLeft(emptyTransposedMap) {
      case (map, (r, c, el)) =>
        val col = map.getOrElse(c, emptyCol) + (r -> el)
        map + (c -> col)
    }
    new MapKeyTable(cols, colKeys, rowKeys)
  }

  override def swapRows(r1: RKT, r2: RKT): MapKeyTable[RKT, CKT, A] = {
    require(rowKeys.contains(r1) && rowKeys.contains(r2), "Both keys should be defined")
    val row1 = rows.getOrElse(r1, emptyRow)
    val row2 = rows.getOrElse(r2, emptyRow)
    val rows2 = rows.updated(r1, row2).updated(r2, row1)
    new MapKeyTable(rows2, rowKeys, colKeys)
  }

  override def swapCols(c1: CKT, c2: CKT): MapKeyTable[RKT, CKT, A] = {
    require(colKeys.contains(c1) && colKeys.contains(c2), "Both keys should be defined")
    val rows2 = rows map {
      case (r, row) =>
        val el1 = row.get(c1)
        val el2 = row.get(c2)
        val row2 = if (row contains c1) row.updated(c2, row(c1)) else row - c2
        val row3 = if (row contains c2) row2.updated(c1, row(c2)) else row2 - c1
        r -> row3
    }
    new MapKeyTable(rows2, rowKeys, colKeys)
  }

  override def switchRows(r1: RKT, r2: RKT): MapKeyTable[RKT, CKT, A] = {
    require(rowKeys.contains(r1) && rowKeys.contains(r2), "Both keys should be defined")
    val rowKeys2 = rowKeys
      .updated(rowKeys indexOf r1, r2)
      .updated(rowKeys indexOf r2, r1)
    new MapKeyTable(rows, rowKeys2, colKeys)
  }

  override def switchCols(c1: CKT, c2: CKT): MapKeyTable[RKT, CKT, A] = {
    require(colKeys.contains(c1) && colKeys.contains(c2), "Both keys should be defined")
    val colKeys2 = colKeys
      .updated(colKeys indexOf c1, c2)
      .updated(colKeys indexOf c2, c1)
    new MapKeyTable(rows, rowKeys, colKeys2)
  }

  override def trim: MapKeyTable[RKT, CKT, A] = {
    val rows2 = rows filter (_._2.nonEmpty)
    val colKeySet = rows.map(_._2.keySet).flatten.toSet
    val rowKeys2 = rowKeys filter (rows2.contains)
    val colKeys2 = colKeys filter (colKeySet.contains)
    new MapKeyTable(rows2, rowKeys2, colKeys2)
  }

  //
  // Sorting and rearrangement
  //

  override def sortedRows[B >: RKT](implicit ord: Ordering[B]): KeyTable[RKT, CKT, A] =
    new MapKeyTable(rows, rowKeys.sorted(ord), colKeys)

  override def sortedCols[B >: CKT](implicit ord: Ordering[B]): KeyTable[RKT, CKT, A] =
    new MapKeyTable(rows, rowKeys, colKeys.sorted(ord))

  //
  // Collection methods
  //

  override def contains[B](el: B): Boolean =
    rows exists (_._2 exists (_._2 == el))

  override def filter(f: A => Boolean): MapKeyTable[RKT, CKT, A] = {
    val rows2 = rows map {
      case (r, row) => (r, row filter (cv => f(cv._2)))
    }
    new MapKeyTable(rows2, rowKeys, colKeys)
  }

  override def mapWithIndex[B](f: (RKT, CKT, A) => B): MapKeyTable[RKT, CKT, B] = {
    val rows2 = rows map {
      case (r, row) => (r, row map {
        case (c, el) => (c, f(r, c, el))
      })
    }
    new MapKeyTable(rows2, rowKeys, colKeys)
  }

  //
  // Helpers
  //

  override protected def emptyRow: ListMap[CKT, A] =
    ListMap.empty

  override protected def emptyCol: ListMap[RKT, A] =
    ListMap.empty
}

object MapKeyTable {

  def empty[RKT, CKT, A]: MapKeyTable[RKT, CKT, A] =
    new MapKeyTable(ListMap.empty, IndexedSeq.empty, IndexedSeq.empty)

  def fromRows[RKT, CKT, A](rows: Map[RKT, Map[CKT, A]]): MapKeyTable[RKT, CKT, A] =
    new MapKeyTable(rows, rows.keys.toIndexedSeq, rows.flatMap(_._2.keys).toIndexedSeq.distinct)

  //
  // Helpers
  //

  /** Type helper for defining self-recursive type */
  private type Curried[RKT, CKT] = {
    type Self[+A] = MapKeyTable[RKT, CKT, A]
  }

  protected implicit def asListMap[A, B](i: Iterable[(A, B)]): ListMap[A, B] =
    ListMap(i.toSeq: _*)
}
