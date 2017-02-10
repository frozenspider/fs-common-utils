package org.fs.utility.collection.table

import KeyTable.Curried

trait KeyTable[RKT, CKT, +A]
    extends GenTableLike[RKT, CKT, A, Curried[RKT, CKT]#Self, Curried[CKT, RKT]#Self] {

  override def withEmptyRow(r: RKT): KeyTable[RKT, CKT, A] =
    withRow(r, Map.empty)

  override def withEmptyCol(c: CKT): KeyTable[RKT, CKT, A] =
    withCol(c, Map.empty)

  override protected def emptyCol: Map[RKT, A] = Map.empty

  override protected def emptyRow: Map[CKT, A] = Map.empty

  /** @return table without the given row, whether it was present or nots */
  def withoutRow(r: RKT): KeyTable[RKT, CKT, A]

  /** @return table without the given column, whether it was present or not */
  def withoutCol(c: CKT): KeyTable[RKT, CKT, A]

  /** @return this table without unused keys */
  def trim: KeyTable[RKT, CKT, A]

  //
  // Standard
  //

  override def equals(o: Any): Boolean = o match {
    case that: KeyTable[_, _, _] =>
      (this.sizes == that.sizes
        && this.rowKeys == that.rowKeys
        && this.colKeys == that.colKeys
        && this.elementsWithIndices == that.elementsWithIndices)
    case _ =>
      false
  }

  override lazy val hashCode: Int = {
    this.elementsWithIndices.hashCode * 13
  }
}

object KeyTable {
  /** Type helper for defining self-recursive type */
  private type Curried[RKT, CKT] = {
    type Self[+A] = KeyTable[RKT, CKT, A]
  }

  def empty[RKT, CKT, A]: KeyTable[RKT, CKT, A] =
    MapKeyTable.empty[RKT, CKT, A]

  def fromRows[RKT, CKT, A](rows: Map[RKT, Map[CKT, A]]): KeyTable[RKT, CKT, A] =
    MapKeyTable.fromRows(rows)
}
