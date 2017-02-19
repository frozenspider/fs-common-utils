package org.fs.utility.collection.table

import org.fs.utility.collection.RichCollectionImplicits._

class IndexedSeqTable[+A] private (rows: IndexedSeq[IndexedSeq[Option[A]]])
    extends IndexedTable[A]
    with GenTableLike[Int, Int, A, IndexedSeqTable, IndexedSeqTable] {
  import IndexedSeqTable._

  def this() = {
    this(IndexedSeq.empty)
  }

  override lazy val sizes: (Int, Int) = {
    (rows.size, rows.map(_.size).fold(0)(_ max _))
  }

  override lazy val count: Int = {
    rows.map(_.count(_.isDefined)).sum
  }

  override def isRowEmpty(r: Int): Boolean = {
    rows.getOrElse(r, IndexedSeq.empty).isEmpty
  }

  override def isColEmpty(c: Int): Boolean = {
    if (c < 0) true
    else rows.forall(_.getFlat(c).isEmpty)
  }

  override def get(r: Int, c: Int): Option[A] = {
    if (r < 0 || c < 0 || r >= sizes._1 || c >= sizes._2) None
    else for {
      row <- rows.get(r)
      cell <- row.getFlat(c)
    } yield cell
  }

  @throws[IllegalArgumentException]("if indices were negative")
  override def +[B >: A](r: Int, c: Int, v: B): IndexedSeqTable[B] = {
    withValueOptionAt(r, c, Some(v))
  }

  override def ++[B >: A](that: GenTableLike[Int, Int, B, ST forSome { type ST[+A2] }, TT forSome { type TT[+A2] }]): IndexedSeqTable[B] = {
    val thatRowCount = that.sizes._1
    val maxRowCount = thatRowCount max this.sizes._1
    val paddedRows = rows padTo (maxRowCount, IndexedSeq.empty[Option[B]])
    val thatColKeys = that.colKeys
    val rows2: IndexedSeq[IndexedSeq[Option[B]]] =
      paddedRows mapWithIndex {
        case (thisRow, i) if i >= thatRowCount =>
          thisRow
        case (thisRow, i) =>
          val thatRow = that.row(i)
          val row2 = thatRow.foldLeft(thisRow) {
            case (row, (i, el)) => row.padTo(i + 1, None).updated(i, Some(el))
          }
          row2
      }
    fromRows(rows2)
  }

  @throws[IllegalArgumentException]("if indices were negative")
  override def -(r: Int, c: Int): IndexedSeqTable[A] = {
    checkBounds(r >= 0 && c >= 0, s"Indices should be non-negative")
    if (r >= sizes._1 && c >= sizes._2) this
    else withValueOptionAt(r, c, None)
  }

  override def rowAsSeq(r: Int): IndexedSeq[Option[A]] =
    if (r < 0 || r >= sizes._1)
      IndexedSeq.empty
    else
      rows(r) padTo (sizes._2, None)

  override def colAsSeq(c: Int): IndexedSeq[Option[A]] =
    if (c < 0 || c >= sizes._2)
      IndexedSeq.empty
    else
      rows map (_.getFlat(c)) padTo (sizes._1, None)

  override def swapRows(r1: Int, r2: Int): IndexedSeqTable[A] = {
    checkBounds(r1 >= 0 && r2 >= 0, "Indices should be non-negative")
    checkBounds(r1 < sizes._1 && r2 < sizes._1, "Indices should be less than row count")
    val newRows = rows.updated(r1, rows(r2)).updated(r2, rows(r1))
    new IndexedSeqTable(newRows)
  }

  override def swapCols(c1: Int, c2: Int): IndexedSeqTable[A] = {
    checkBounds(c1 >= 0 && c2 >= 0, "Indices should be non-negative")
    checkBounds(c1 < sizes._2 && c2 < sizes._2, "Indices should be less than column count")
    val newRows = rows map (col => {
      val padded = col.padTo((c1 max c2) + 1, None)
      padded.updated(c1, padded(c2)).updated(c2, padded(c1))
    })
    new IndexedSeqTable(newRows)
  }

  // Type system hack needed because of self-type method double override
  override def withRow[B >: A](r: Int, row: RowType[B]): IndexedSeqTable[B] =
    super.withRow(r, row).asInstanceOf[IndexedSeqTable[B]]

  // Type system hack needed because of self-type method double override
  override def withCol[B >: A](c: Int, col: ColType[B]): IndexedSeqTable[B] =
    super.withCol(c, col).asInstanceOf[IndexedSeqTable[B]]

  @throws[IllegalArgumentException]("if the index was negative")
  override def withRow[B >: A](r: Int, row: IndexedSeq[Option[B]]): IndexedSeqTable[B] = {
    checkBounds(r >= 0, "Index should be non-negative")
    val newRows = rows.padTo(r + 1, IndexedSeq.empty).updated(r, row)
    new IndexedSeqTable(newRows)
  }

  @throws[IllegalArgumentException]("if the index was negative")
  override def withCol[B >: A](c: Int, col: IndexedSeq[Option[B]]): IndexedSeqTable[B] = {
    checkBounds(c >= 0, "Index should be non-negative")
    val newRows = rows zipAll (col, IndexedSeq.empty, None) map {
      case (row, cellOption) => row.padTo(c + 1, None).updated(c, cellOption)
    }
    new IndexedSeqTable(newRows)
  }

  override def withInsertedRow[B >: A](r: Int, row: IndexedSeq[Option[B]]): IndexedSeqTable[B] = {
    checkBounds(r >= 0, "Index should be non-negative")
    val paddedRows = rows.padTo(r, IndexedSeq.empty)
    val newRows = (paddedRows.take(r) :+ row) ++ paddedRows.drop(r)
    new IndexedSeqTable(newRows)
  }

  override def withInsertedCol[B >: A](c: Int, col: IndexedSeq[Option[B]]): IndexedSeqTable[B] = {
    checkBounds(c >= 0, "Index should be non-negative")
    val newRows = rows zipAll (col, IndexedSeq.empty, None) map {
      case (row, cellOption) =>
        val paddedRow = row.padTo(c, None)
        (paddedRow.take(c) :+ cellOption) ++ paddedRow.drop(c)
    }
    new IndexedSeqTable(newRows)
  }

  @throws[IllegalArgumentException]("if the index was negative")
  override def withoutRow(r: Int): IndexedSeqTable[A] = {
    checkBounds(r >= 0, "Index should be non-negative")
    val newRows = rows.take(r) ++ rows.drop(r + 1)
    new IndexedSeqTable(newRows)
  }

  @throws[IllegalArgumentException]("if the index was negative")
  override def withoutCol(c: Int): IndexedSeqTable[A] = {
    checkBounds(c >= 0, "Index should be non-negative")
    val newRows = rows map (row =>
      row.take(c) ++ row.drop(c + 1)
    )
    new IndexedSeqTable(newRows)
  }

  override def elementsWithIndices: Seq[(Int, Int, A)] = {
    for {
      (row, r) <- rows.zipWithIndex
      (Some(v), c) <- (row.zipWithIndex)
    } yield (r, c, v)
  }

  override def transpose: IndexedSeqTable[A] = {
    val newRows = rows.map(_ padTo (sizes._2, None)).transpose
    new IndexedSeqTable(newRows)
  }

  override def trim: IndexedSeqTable[A] = {
    val newRows = rows map trimTrailingVoid dropRightWhile (_.isEmpty)
    new IndexedSeqTable(newRows)
  }

  //
  // Collection methods
  //

  override def contains[B >: A](el: B): Boolean = {
    rows exists (_ contains Some(el))
  }

  override def filter(f: A => Boolean): IndexedSeqTable[A] = {
    val newRows = rows map (_ map {
      case Some(v) if f(v) => Some(v)
      case _               => None
    })
    new IndexedSeqTable(newRows)
  }

  override def mapWithIndex[B](f: (Int, Int, A) => B): IndexedSeqTable[B] = {
    val newRows = rows mapWithIndex ((row, r) =>
      row mapWithIndex ((cellOption, c) =>
        cellOption map (f(r, c, _))
      )
    )
    new IndexedSeqTable(newRows)
  }

  //
  // Helper methods
  //

  @throws[IllegalArgumentException]("if indices were negative")
  private def checkBounds(cond: Boolean, text: => Any): Unit = {
    if (!cond) throw new IllegalArgumentException(text.toString)
  }

  @throws[IllegalArgumentException]("if indices were negative")
  private def withValueOptionAt[B >: A](r: Int, c: Int, v: Option[B]): IndexedSeqTable[B] = {
    checkBounds(r >= 0 && c >= 0, s"Indices should be non-negative")
    val row = rows.getOrElse(r, Seq.empty)
    val newRows = rows padTo (r + 1, IndexedSeq.empty) updated (r,
      (row padTo (c + 1, None) updated (c, v)).toIndexedSeq
    )
    new IndexedSeqTable(newRows)
  }
}

object IndexedSeqTable {
  def empty[A] = new IndexedSeqTable[A]()

  def fromValues[A](vals: Seq[Seq[A]]): IndexedSeqTable[A] =
    new IndexedSeqTable[A](vals.toIndexedSeq map (_.toIndexedSeq map Some.apply))

  def fromRows[A](rows: Seq[Seq[Option[A]]]): IndexedSeqTable[A] =
    new IndexedSeqTable[A](rows.toIndexedSeq map (_.toIndexedSeq))

  //
  // Helpers
  //

  private[IndexedSeqTable] def trimTrailingVoid[T](s: Seq[Option[T]]): IndexedSeq[Option[T]] = {
    s.dropRightWhile(_.isEmpty).toIndexedSeq
  }

  private implicit class RichOptionsSeq[T](is: IndexedSeq[Option[T]]) {
    def getFlat(i: Int): Option[T] = {
      if (is.length <= i) None
      else is(i)
    }
  }
}
