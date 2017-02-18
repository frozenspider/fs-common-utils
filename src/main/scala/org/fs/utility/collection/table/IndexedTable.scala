package org.fs.utility.collection.table

import scala.collection.immutable.ListMap

/**
 * Immutable two-dimensional sequence.
 *
 * Setter methods throws {@code IllegalArgumentException} when supplied with negative indices.
 *
 * Accessor methods, when supplied indices are out of bounds, return an empty monad if possible,
 * throwing {@code IllegalArgumentException} otherwise.
 *
 * @see GenTableLike
 */
trait IndexedTable[+A]
    extends GenTable[Int, Int, A]
    with GenTableLike[Int, Int, A, IndexedTable, IndexedTable] {

  /** @return whether or not element with given index can be obtained from this table */
  override def isDefinedAt(r: Int, c: Int): Boolean = {
    r >= 0 && c >= 0 && r < sizes._1 && c < sizes._2 && super.isDefinedAt(r, c)
  }

  override def row(r: Int): ListMap[Int, A] = {
    if (r < 0 || r >= sizes._1)
      ListMap.empty
    else
      ListMap(rowAsSeq(r).zipWithIndex.collect {
        case (Some(v), i) => (i -> v)
      }: _*)
  }

  override def col(c: Int): ListMap[Int, A] = {
    if (c < 0 || c >= sizes._2)
      ListMap.empty
    else
      ListMap(colAsSeq(c).zipWithIndex.collect {
        case (Some(v), i) => (i -> v)
      }: _*)
  }

  /** @return row with the given key, represented as sequence with None in place of missing elements */
  def rowAsSeq(r: Int): IndexedSeq[Option[A]]

  /** @throws IllegalArgumentException if c is negative or is greater than columns count */
  def colAsSeq(c: Int): IndexedSeq[Option[A]]

  /** Adds/replaces the row in the table, padding table if necessary. */
  @throws[IllegalArgumentException]("if the index was negative")
  override def withRow[B >: A](r: Int, row: RowType[B]): IndexedTable[B] = {
    val rowSeq = row.foldLeft(IndexedSeq.fill[Option[B]](row.keys.max + 1)(None)) {
      case (seq, (c, v)) => seq.updated(c, Some(v))
    }
    withRow(r, rowSeq)
  }

  /** Adds/replaces the column in the table, padding table if necessary. */
  @throws[IllegalArgumentException]("if the index was negative")
  override def withCol[B >: A](c: Int, col: ColType[B]): IndexedTable[B] = {
    val colSeq = col.foldLeft(IndexedSeq.fill[Option[B]](col.keys.max + 1)(None)) {
      case (seq, (c, v)) => seq.updated(c, Some(v))
    }
    withCol(c, colSeq)
  }

  /**
   * Adds/replaces the sequence-bassed row in the table, padding table if necessary.
   * Empty trailing elements are NOT trimmed.
   */
  @throws[IllegalArgumentException]("if the index was negative")
  def withRow[B >: A](r: Int, row: IndexedSeq[Option[B]]): IndexedTable[B]

  /**
   * Adds/replaces the sequence-bassed column in the table, padding table if necessary.
   * Empty trailing elements are NOT trimmed.
   */
  @throws[IllegalArgumentException]("if the index was negative")
  def withCol[B >: A](c: Int, col: IndexedSeq[Option[B]]): IndexedTable[B]

  /**
   * Inserts the sequence-bassed row in the table, causing rows shift and padding table if necessary.
   * Empty trailing elements are NOT trimmed.
   */
  @throws[IllegalArgumentException]("if the index was negative")
  def withInsertedRow[B >: A](r: Int, row: RowType[B]): IndexedTable[B] = {
    val rowSeq = row.foldLeft(IndexedSeq.fill[Option[B]](row.keys.max + 1)(None)) {
      case (seq, (c, v)) => seq.updated(c, Some(v))
    }
    withInsertedRow(r, rowSeq)
  }

  /**
   * Inserts the sequence-bassed column in the table, causing columns shift and padding table if necessary.
   * Empty trailing elements are NOT trimmed.
   */
  @throws[IllegalArgumentException]("if the index was negative")
  def withInsertedCol[B >: A](c: Int, col: ColType[B]): IndexedTable[B] = {
    val colSeq = col.foldLeft(IndexedSeq.fill[Option[B]](col.keys.max + 1)(None)) {
      case (seq, (c, v)) => seq.updated(c, Some(v))
    }
    withInsertedCol(c, colSeq)
  }

  /**
   * Inserts the sequence-bassed row in the table, causing rows shift and padding table if necessary.
   * Empty trailing elements are NOT trimmed.
   */
  @throws[IllegalArgumentException]("if the index was negative")
  def withInsertedRow[B >: A](r: Int, row: IndexedSeq[Option[B]]): IndexedTable[B]

  /**
   * Inserts the sequence-bassed column in the table, causing columns shift and padding table if necessary.
   * Empty trailing elements are NOT trimmed.
   */
  @throws[IllegalArgumentException]("if the index was negative")
  def withInsertedCol[B >: A](c: Int, col: IndexedSeq[Option[B]]): IndexedTable[B]

  /** @return table without the given row, whether it was present or not, shifting subsequent rows */
  def withoutRow(r: Int): IndexedTable[A]

  /** @return table without the given column, whether it was present or not, shifting subsequent columns */
  def withoutCol(c: Int): IndexedTable[A]

  override def rowKeys: Range = 0 until sizes._1

  override def colKeys: Range = 0 until sizes._2

  /** @return this table without trailing empty rows/column */
  def trim: IndexedTable[A]

  //
  // Sorting and rearrangement
  //

  def sortRowsBy[B](f: Int => B)(implicit ord: Ordering[B]): IndexedTable[A] = {
    def exchange(t: IndexedTable[A], k1: Int, k2: Int): IndexedTable[A] =
      t.swapRows(k1, k2)
    GenTableLike.sortLinesBy[Int, Int, IndexedTable[A]](this, rowKeys, exchange)(ord on f)
  }

  def sortColsBy[B](f: Int => B)(implicit ord: Ordering[B]): IndexedTable[A] = {
    def exchange(t: IndexedTable[A], k1: Int, k2: Int): IndexedTable[A] =
      t.swapCols(k1, k2)
    GenTableLike.sortLinesBy[Int, Int, IndexedTable[A]](this, colKeys, exchange)(ord on f)
  }

  //
  // Standard
  //

  /** Two tables are considered equal when they have the same sizes, ordered keys and elements */
  override def equals(o: Any): Boolean = o match {
    case that: IndexedTable[_] =>
      this.sizes == that.sizes &&
        this.rowKeys == that.rowKeys &&
        this.colKeys == that.colKeys &&
        this.elementsWithIndices == that.elementsWithIndices
    case _ =>
      false
  }

  override lazy val hashCode: Int = {
    7 * rowKeys.hashCode + 11 * colKeys.hashCode + 13 * elements.hashCode
  }
}

object IndexedTable {

  def empty[A]: IndexedTable[A] =
    IndexedSeqTable.empty[A]

  def fromValues[A](vals: Seq[Seq[A]]): IndexedTable[A] =
    IndexedSeqTable.fromValues(vals)

  def fromRows[A](rows: Seq[Seq[Option[A]]]): IndexedTable[A] =
    IndexedSeqTable.fromRows(rows)
}
