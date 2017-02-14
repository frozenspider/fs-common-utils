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
    extends GenTableLike[Int, Int, A, IndexedTable, IndexedTable] {

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

  /** Adds/replaces the sequence-bassed row in the table. Empty trailing elements are NOT trimmed. */
  @throws[IllegalArgumentException]("if the index was negative")
  def withRow[B >: A](r: Int, row: IndexedSeq[Option[B]]): IndexedTable[B]

  /** Adds/replaces the sequence-bassed column in the table. Empty trailing elements are NOT trimmed. */
  @throws[IllegalArgumentException]("if the index was negative")
  def withCol[B >: A](c: Int, col: IndexedSeq[Option[B]]): IndexedTable[B]

  /** Inserts the sequence-bassed row in the table, causing rows shift. Empty trailing elements are NOT trimmed. */
  @throws[IllegalArgumentException]("if the index was negative")
  def withInsertedRow[B >: A](r: Int, row: IndexedSeq[Option[B]]): IndexedTable[B]

  /** Inserts the sequence-bassed column in the table, causing columns shift. Empty trailing elements are NOT trimmed. */
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
  // Standard
  //

  override def equals(o: Any): Boolean = o match {
    case that: IndexedTable[_] =>
      super.equals(that)
    case _ =>
      false
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
