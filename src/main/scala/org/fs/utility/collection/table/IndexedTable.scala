package org.fs.utility.collection.table

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

  /** @return row with the given key, represented as sequence with None in place of missing elements */
  def rowAsSeq(r: Int): IndexedSeq[Option[A]]

  /** @return column with the given key, represented as map from row keys to values */
  def colAsSeq(c: Int): IndexedSeq[Option[A]]

  /** Adds/replaces the sequence-bassed row in the table. Empty trailing elements are NOT trimmed. */
  def withRow[B >: A](r: Int, row: IndexedSeq[Option[B]]): IndexedTable[B]

  /** Adds/replaces the sequence-bassed column in the table. Empty trailing elements are NOT trimmed. */
  def withCol[B >: A](c: Int, col: IndexedSeq[Option[B]]): IndexedTable[B]

  override def rowKeys: Range = 0 until sizes._1

  override def colKeys: Range = 0 until sizes._2

  override def emptyRow: RowType[A] =
    Map.empty

  override def emptyCol: ColType[A] =
    Map.empty

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
