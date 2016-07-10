package org.fs.utility.collection.table

import org.fs.utility.Implicits._

/**
 * Indexed table is an immutable two-dimensional sequence
 *
 * @see GenTableLike
 */
trait IndexedTable[+A]
    extends GenTableLike[Int, Int, A, IndexedTable, IndexedTable] {

  /** @return whether or not element with given index can be obtained from this table */
  override def isDefinedAt(r: Int, c: Int): Boolean = {
    r >= 0 && c >= 0 && super.isDefinedAt(r, c)
  }

  override def row(r: Int): Map[Int, A] = {
    if (r < 0 || r >= sizes._1)
      Map.empty
    else
      rowAsSeq(r).toDefinedMap
  }

  override def col(c: Int): Map[Int, A] = {
    if (c < 0 || c >= sizes._2)
      Map.empty
    else
      colAsSeq(c).toDefinedMap
  }

  /**
   * @return row with the given key, represented as sequence with None in place of missing elements
   * @throws IndexOutOfBoundsException if r is negative or is greater than rows count
   */
  def rowAsSeq(r: Int): IndexedSeq[Option[A]]

  /**
   * @return column with the given key, represented as map from row keys to values
   * @throws IndexOutOfBoundsException if c is negative or is greater than columns count
   */
  def colAsSeq(c: Int): IndexedSeq[Option[A]]

  /** Adds/replaces the sequence-bassed row in the table. Empty trailing elements are NOT trimmed. */
  def withRow[B >: A](r: Int, row: IndexedSeq[Option[B]]): IndexedTable[B]

  /** Adds/replaces the sequence-bassed column in the table. Empty trailing elements are NOT trimmed. */
  def withCol[B >: A](c: Int, col: IndexedSeq[Option[B]]): IndexedTable[B]

  override def rowKeys: Range = 0 until sizes._1

  override def colKeys: Range = 0 until sizes._2

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
