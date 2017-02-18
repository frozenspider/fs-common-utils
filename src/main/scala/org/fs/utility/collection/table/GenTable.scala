package org.fs.utility.collection.table

/**
 * Base trait for immutable table-like structures are two-dimensional sequences/maps.
 *
 * Provides proper self-recursive type bounds, and containts implementation of methods which are hard to
 * implement with unbounded types.
 *
 * @author FS
 * @param RKT row key type
 * @param CKT column key type
 * @param A stored value type
 * @see GenTableLike
 * @see IndexedTable
 * @see KeyTable
 */
trait GenTable[RKT, CKT, +A]
    extends GenTableLike[RKT, CKT, A, GenTable.Curried[RKT, CKT]#Self, GenTable.Curried[CKT, RKT]#Self] {

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
    if (isEmpty) {
      "+"
    } else {
      val stringTable = this map {
        case null => "null"
        case v    => v.toString
      }
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
            case (key, len) => toPaddedString(key getOrElse " ", len)
          } mkString ("|", "|", "|")
        lineOne +: stringTable.rowKeys.map(r =>
          maxColumnWidths map {
            case (key, len) =>
              val stringValue = key map (c => stringTable get (r, c) getOrElse " ") getOrElse r
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
  }
}

object GenTable {
  /** Type helper for defining self-recursive type */
  private type Curried[RKT, CKT] = {
    type Self[+A] = GenTable[RKT, CKT, A]
  }
}
