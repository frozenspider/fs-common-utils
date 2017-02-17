package org.fs.utility.collection.table

import KeyTable.Curried

trait KeyTable[RKT, CKT, +A]
    extends GenTable[RKT, CKT, A]
    with GenTableLike[RKT, CKT, A, Curried[RKT, CKT]#Self, Curried[CKT, RKT]#Self] {

  //
  // Copy-update
  //

  /**
   * Adds/replaces the given row in the table, potentically increasing row count by 1.
   *
   * Note that columns order is not guaranteed to be preserved - new columns will be added in the end
   * and existing ones will be reused.
   */
  override def withRow[B >: A](r: RKT, row: RowType[B]): KeyTable[RKT, CKT, B]

  /**
   * Adds/replaces the given column in the table, potentically increasing column count by 1.
   *
   * Note that rows order is not guaranteed to be preserved - new rows will be added in the end
   * and existing ones will be reused.
   */
  override def withCol[B >: A](c: CKT, col: ColType[B]): KeyTable[RKT, CKT, B]

  /** @return table without the given row, whether it was present or nots */
  def withoutRow(r: RKT): KeyTable[RKT, CKT, A]

  /** @return table without the given column, whether it was present or not */
  def withoutCol(c: CKT): KeyTable[RKT, CKT, A]

  /** @return this table without unused keys */
  def trim: KeyTable[RKT, CKT, A]

  //
  // Sorting and rearrangement
  //

  /** Switches the position of given rows, both of which should be defined */
  @throws[IllegalArgumentException]("if any index were undefined")
  def switchRows(r1: RKT, r2: RKT): KeyTable[RKT, CKT, A]

  /** Switches the position of given columns, both of which should be defined */
  @throws[IllegalArgumentException]("if any index were undefined")
  def switchCols(c1: CKT, c2: CKT): KeyTable[RKT, CKT, A]

  def sortRowsBy[B](f: RKT => B)(implicit ord: Ordering[B]): KeyTable[RKT, CKT, A] =
    this.sortedRows(ord on f)

  def sortColsBy[B](f: CKT => B)(implicit ord: Ordering[B]): KeyTable[RKT, CKT, A] =
    this.sortedCols(ord on f)

  /** @return table with rows reordered */
  def sortedRows[B >: RKT](implicit ord: Ordering[B]): KeyTable[RKT, CKT, A]

  /** @return table with columns reordered */
  def sortedCols[B >: CKT](implicit ord: Ordering[B]): KeyTable[RKT, CKT, A]

  //
  // Standard
  //

  /** Two tables are considered equal when they have the same sizes, ordered keys and elements */
  override def equals(o: Any): Boolean = o match {
    case that: KeyTable[_, _, _] =>
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

  //
  // Helpers
  //

  override protected def emptyCol: Map[RKT, A] = Map.empty

  override protected def emptyRow: Map[CKT, A] = Map.empty
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
