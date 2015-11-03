package org.fs.utility.collection.table

import org.fs.utility.Implicits._

class IndexedSeqTable[+A] private (rows: IndexedSeq[IndexedSeq[Option[A]]])
    extends IndexedTable[A]
    with GenTableLike[Int, Int, A, IndexedSeqTable, IndexedSeqTable]{
  import IndexedSeqTable._

  def this() = {
    this(IndexedSeq.empty)
  }

  override lazy val sizes: (Int, Int) =
    (rows.size, (rows.map(_.size) :+ 0).max)

  override lazy val count: Int =
    rows.map(_.count(_.isDefined)).sum

  override lazy val isEmpty: Boolean =
    rows.isEmpty

  override def isRowEmpty(r: Int): Boolean = {
    checkBounds(r >= 0, "Index should be non-negative")
    rows.getOrElse(r, IndexedSeq.empty).isEmpty
  }

  override def isColEmpty(c: Int): Boolean = {
    checkBounds(c >= 0, "Index should be non-negative")
    rows.forall(_.getFlat(c).isEmpty)
  }

  override def get(r: Int, c: Int): Option[A] = {
    checkBounds(r >= 0 && c >= 0, s"Indices should be non-negative, were ${(r, c)}")
    for {
      row <- rows.get(r)
      cell <- row.getFlat(c)
    } yield cell
  }

  override def +[B >: A](r: Int, c: Int, v: B): IndexedSeqTable[B] =
    withValueOptionAt(r, c, Some(v))

  override def ++[B >: A, ST[+A2] <: GenTableLike[Int, Int, A2, ST, TT], TT[+A2] <: GenTableLike[Int, Int, A2, TT, ST]](that: GenTableLike[Int, Int, B, ST, TT]): IndexedSeqTable[B] = {
    // TODO: Optimize?
    val result = that.elementsWithIndices.foldLeft[IndexedSeqTable[B]](this) {
      case (acc, (r2, c2, v2)) => acc + (r2, c2, v2)
    }
    result
  }

  override def -(r: Int, c: Int): IndexedSeqTable[A] = {
    checkBounds(r >= 0 && c >= 0, s"Indices should be non-negative, were ${(r, c)}")
    if (r >= sizes._1 && c >= sizes._2) this
    else withValueOptionAt(r, c, None)
  }

  override def row(r: Int): IndexedSeq[Option[A]] = {
    checkBounds(r >= 0, "Index should be non-negative")
    checkBounds(r < sizes._1, "Index is too big")
    rows(r) padTo (sizes._2, None)
  }

  override def col(c: Int): IndexedSeq[Option[A]] = {
    checkBounds(c >= 0, "Index should be non-negative")
    checkBounds(c < sizes._2, "Index is too big")
    rows map (_.getFlat(c)) padTo (sizes._1, None)
  }

  override def swapRows(r1: Int, r2: Int): IndexedSeqTable[A] = {
    checkBounds(r1 >= 0 && r2 >= 0, "Indices should be non-negative")
    checkBounds(r1 < sizes._1 && r2 < sizes._1, "Index is too big")
    val newRows = rows.updated(r1, rows(r2)).updated(r2, rows(r1))
    new IndexedSeqTable(newRows)
  }

  override def swapCols(c1: Int, c2: Int): IndexedSeqTable[A] = {
    checkBounds(c1 >= 0 && c2 >= 0, "Indices should be non-negative")
    checkBounds(c1 < sizes._2 && c2 < sizes._2, "Index is too big")
    val newRows = rows map (col => {
      val padded = col.padTo((c1 max c2) + 1, None)
      padded.updated(c1, padded(c2)).updated(c2, padded(c1))
    })
    new IndexedSeqTable(newRows)
  }

  override def withoutRow(r: Int): IndexedSeqTable[A] = {
    checkBounds(r >= 0, "Index should be non-negative")
    val newRows = rows.take(r) ++ rows.drop(r + 1)
    new IndexedSeqTable(newRows)
  }

  override def withoutCol(c: Int): IndexedSeqTable[A] = {
    checkBounds(c >= 0, "Index should be non-negative")
    val newRows = rows map (row =>
      row.take(c) ++ row.drop(c + 1)
    )
    new IndexedSeqTable(newRows)
  }

  override def withRow[B >: A](r: Int, row: IndexedSeq[Option[B]]): IndexedSeqTable[B] = {
    checkBounds(r >= 0, "Index should be non-negative")
    val replacementRow = row.toIndexedSeq
    val newRows = rows.padTo(r + 1, IndexedSeq.empty).updated(r, replacementRow)
    new IndexedSeqTable(newRows)
  }

  override def withCol[B >: A](c: Int, col: IndexedSeq[Option[B]]): IndexedSeqTable[B] = {
    checkBounds(c >= 0, "Index should be non-negative")
    val newRows = rows zipAll (col, IndexedSeq.empty, None) map {
      case (row, cellOption) => row.padTo(c + 1, None).updated(c, cellOption)
    }
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

  private def checkBounds(cond: Boolean, text: => Any): Unit = {
    if (!cond) throw new IndexOutOfBoundsException(text.toString)
  }

  private def withValueOptionAt[B >: A](r: Int, c: Int, v: Option[B]): IndexedSeqTable[B] = {
    checkBounds(r >= 0 && c >= 0, s"Indices should be non-negative, were ${(r, c)}")
    val row = rows.getOrElse(r, Seq.empty)
    val newRows = rows padTo (r + 1, IndexedSeq.empty) updated (r,
      (row padTo (c + 1, None) updated (c, v)).toIndexedSeq
    )
    new IndexedSeqTable(newRows)
  }
}

object IndexedSeqTable {
  def empty[A] = new IndexedSeqTable[A]()

  def fromValues[A](vals: Seq[Seq[A]]) =
    new IndexedSeqTable[A](vals.toIndexedSeq map (_.toIndexedSeq map Some.apply))

  def fromRows[A](rows: Seq[Seq[Option[A]]]) =
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

    def getFlatOrElse(i: Int, default: => T): T =
      getFlat(i).getOrElse(default)
  }
}
