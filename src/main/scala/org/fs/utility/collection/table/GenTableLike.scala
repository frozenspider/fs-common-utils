package org.fs.utility.collection.table

/**
 * Base trait for immutable table-like structures are two-dimensional sequences/maps.
 * Table structure can be represented as following:
 *
 * <pre>
 *     a   b   c   d   e
 *   +---+---+---+---+---+--> (C)
 * 0 |0x0|0x1|0x2|0x3|0x4
 *   +---+---+---+---+---
 * 1 |1x0|1x1|1x2|1x3|1x4
 *   +---+---+---+---+---
 * 2 |2x0|2x1|2x2|2x3|2x4
 *   +
 *   |
 *   V
 *  (R)
 * </pre>
 *
 * @author FS
 * @param RKT row key type
 * @param CKT column key type
 * @param A stored value type
 * @param SelfType self-recursive type of this table, used as a lower constraint for return types
 * @param TransposedType type of table after transposition
 * @see IndexedTable
 * @see KeyTable
 */
trait GenTableLike[RKT, CKT, +A, +SelfType[+A2] <: GenTableLike[RKT, CKT, A2, SelfType, TransposedType], +TransposedType[+A2] <: GenTableLike[CKT, RKT, A2, TransposedType, SelfType]]
    extends PartialFunction[(RKT, CKT), A] { self: SelfType[A] =>

  type RowType[+A2] = Map[CKT, A2]
  type ColType[+A2] = Map[RKT, A2]

  //
  // State
  //

  /** Row and column count */
  def sizes: (Int, Int)

  /** Number of elements in the table */
  lazy val count: Int =
    elements.size

  /**
   * Whether this table contains no elements.
   * Note that non-trimmed table might still have non-zero row and column count.
   */
  def isEmpty: Boolean =
    count == 0

  /** Whether or not given row is empty or absent */
  def isRowEmpty(r: RKT): Boolean

  /** Whether or not given column is empty or absent */
  def isColEmpty(c: CKT): Boolean

  //
  // Retrieve
  //

  /** Get element by key pair, throwing IllegalArgumentException if it's missing */
  override final def apply(rc: (RKT, CKT)): A = {
    apply(rc._1, rc._2)
  }

  /** Get element by keys, throwing IllegalArgumentException if it's missing */
  def apply(r: RKT, c: CKT): A = {
    get(r, c).getOrElse {
      throw new IllegalArgumentException(s"No element at (${(r, c)})")
    }
  }

  /** @return whether or not element with given key pair can be obtained from this table */
  override final def isDefinedAt(rc: (RKT, CKT)): Boolean = {
    isDefinedAt(rc._1, rc._2)
  }

  /** @return whether or not element with given keys can be obtained from this table */
  def isDefinedAt(r: RKT, c: CKT): Boolean = {
    get(r, c).isDefined
  }

  /** Attempts to get an element by its key pair */
  final def get(rc: (RKT, CKT)): Option[A] = {
    get(rc._1, rc._2)
  }

  /** Attempts to get an element by its keys */
  def get(r: RKT, c: CKT): Option[A]

  def rowKeys: IndexedSeq[RKT]

  def colKeys: IndexedSeq[CKT]

  /** @return row of defined elements with the given key */
  def row(r: RKT): RowType[A]

  /** @return column of defined elements with the given key */
  def col(c: CKT): ColType[A]

  /** Adds/replaces the given row in the table. This might increase row count by more than 1 depending on table implementation. */
  def withRow[B >: A](r: RKT, row: RowType[B]): SelfType[B]

  /** Adds/replaces the given column in the table. This might increase column count by more than 1 depending on table implementation. */
  def withCol[B >: A](c: CKT, col: ColType[B]): SelfType[B]

  def withEmptyRow(r: RKT): SelfType[A] = {
    withRow(r, emptyRow)
  }

  def withEmptyCol(c: CKT): SelfType[A] = {
    withCol(c, emptyCol)
  }

  /** All table elements with their row and column coordinates, iterated by rows */
  def elements: Seq[A] =
    elementsWithIndices map (_._3)

  /** All table elements with their row and column coordinates, iterated by rows */
  def elementsWithIndices: Seq[(RKT, CKT, A)]

  //
  // Copy-update
  //

  /** Adds/replaces the element. This might increase row/column count by more than 1 depending on table implementation */
  final def +[B >: A](rc: (RKT, CKT), v: B): SelfType[B] = {
    this + (rc._1, rc._2, v)
  }

  /** Adds/replaces the element. This might increase row/column count by more than 1 depending on table implementation */
  def +[B >: A](r: RKT, c: CKT, v: B): SelfType[B]

  // These type arguments are a hack for non-parametrizable nature of _
  // See https://issues.scala-lang.org/browse/SI-8039
  /** @return table with added/replaced values */
  def ++[B >: A, ST[+A2] <: GenTableLike[RKT, CKT, A2, ST, TT], TT[+A2] <: GenTableLike[CKT, RKT, A2, TT, ST]](that: GenTableLike[RKT, CKT, B, ST, TT]): SelfType[B]

  /** @return table without value at a given position (NOT dropping empty rows/columns) */
  final def -(rc: (RKT, CKT)): SelfType[A] = {
    this - (rc._1, rc._2)
  }

  /** @return table without value at a given position (NOT dropping empty rows/columns) */
  def -(r: RKT, c: CKT): SelfType[A]

  def transpose: TransposedType[A]

  /** Swaps the content of the given rows, both of which should be defined */
  @throws[IllegalArgumentException]("if any index were undefined")
  def swapRows(r1: RKT, r2: RKT): SelfType[A]

  /** Swaps the content of the given columns, both of which should be defined */
  @throws[IllegalArgumentException]("if any index were undefined")
  def swapCols(c1: CKT, c2: CKT): SelfType[A]

  //
  // Collection methods
  //

  /** @return first cell (element with indices) matching given predicate if any exists, iterated by rows */
  def findCell(predicate: A => Boolean): Option[(RKT, CKT, A)] = {
    elementsWithIndices find (tuple =>
      predicate(tuple._3)
    )
  }

  /** @return first element matching given predicate if any exists, iterated by rows */
  def find(predicate: A => Boolean): Option[A] =
    findCell(predicate) map (_._3)

  def contains[B >: A](el: B): Boolean

  def filter(f: A => Boolean): SelfType[A]

  def foreach(f: A => Unit): Unit =
    elements.foreach(f)

  def map[B](f: A => B): SelfType[B] =
    mapWithIndex((_, _, v) => f(v))

  def mapWithIndex[B](f: (RKT, CKT, A) => B): SelfType[B]

  //
  // Standard
  //

  /**
   * Outputs the table in the as pretty string like this:
   * <pre>
   * +--+-----+---+------+
   * |  |c0   |c1 |c2    |
   * +--+-----+---+------+
   * |r0|first|row|values|
   * +--+-----+---+------+
   * |r1|col  |   |stuff |
   * +--+-----+---+------+
   * </pre>
   */
  override def toString: String = {
    val stringTable = this map (v =>
      if (v != null) v.toString else "null"
    )
    val maxColumnWidths: Seq[(Option[CKT], Int)] = {
      val leftColumnWidth = Map(
        None -> rowKeys.map(_.toString.length).max
      )
      val initialWidths = leftColumnWidth ++ colKeys.map(idx =>
        Some(idx) -> idx.toString.length
      ).toMap
      val unordered = stringTable.elementsWithIndices.foldLeft(initialWidths) {
        case (acc, (_, c, str)) => acc updated (Some(c), acc(Some(c)) max str.length)
      }
      Seq(None -> unordered(None)) ++ (colKeys map Some.apply map (key => key -> unordered(key)))
    }
    val separatorString: String =
      maxColumnWidths map ("-" * _._2) mkString ("+", "+", "+")
    val lines: Seq[String] = {
      def toPaddedString(a: Any, l: Int): String = {
        a.toString.padTo(l, " ").mkString
      }
      val lineOne =
        maxColumnWidths.toSeq map {
          case (key, len) => toPaddedString(key.getOrElse(" "), len)
        } mkString ("|", "|", "|")
      lineOne +: stringTable.rowKeys.map(r =>
        maxColumnWidths map {
          case (key, len) =>
            val stringValue = key.map(stringTable.get(r, _).getOrElse(" ")).getOrElse(r)
            toPaddedString(stringValue, len)
        } mkString ("|", "|", "|")
      )
    }
    lines mkString (
      separatorString + "\n",
      "\n" + separatorString + "\n",
      "\n" + separatorString
    )
  }

  //
  // Helpers
  //

  protected def emptyRow: RowType[A] =
    Map.empty

  protected def emptyCol: ColType[A] =
    Map.empty

}

object GenTableLike {
  import org.fs.utility.collection.table.{ GenTableLike => GTL }

  protected[table] def sortLinesBy[KT, B >: KT, Table](table: Table,
                                                       keys: IndexedSeq[KT],
                                                       swapLines: (Table, KT, KT) => Table)(implicit ord: Ordering[B]): Table = {
    val sortedKeys = keys.sorted(ord)
    val keyPairs = keys zip sortedKeys
    val swapPairs = keyPairs.foldLeft(Map.empty[KT, KT]) {
      case (replacements, (srcKey, destKey)) =>
        def getRealDestKey(k: KT): KT = {
          if (replacements contains k) getRealDestKey(replacements(k)) else k
        }
        val realDestKey = getRealDestKey(destKey)
        replacements.updated(srcKey, realDestKey)
    } filter (p => p._1 != p._2)
    val result = swapPairs.foldLeft(table){
      case (acc, (k1, k2)) =>
        swapLines(acc, k1, k2)
    }
    result
  }
}
