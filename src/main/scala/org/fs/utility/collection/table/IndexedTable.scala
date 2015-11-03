package org.fs.utility.collection.table

/**
 * Indexed table is an immutable two-dimensional sequence
 *
 * @see GenTableLike
 */
trait IndexedTable[+A]
    extends GenTableLike[Int, Int, A, IndexedTable, IndexedTable] {

  override type RowType[+A2] = IndexedSeq[Option[A2]]
  override type ColType[+A2] = IndexedSeq[Option[A2]]

  /** @return whether or not element with given index can be obtained from this table */
  override def isDefinedAt(r: Int, c: Int): Boolean = {
    r >= 0 && c >= 0 && super.isDefinedAt(r, c)
  }

  override def rowKeys =
    0 until sizes._1

  override def colKeys =
    0 until sizes._2

  override def emptyRow: RowType[A] =
    IndexedSeq.empty

  override def emptyCol: ColType[A] =
    IndexedSeq.empty

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
