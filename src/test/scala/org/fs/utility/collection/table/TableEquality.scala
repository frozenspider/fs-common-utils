package org.fs.utility.collection.table

import org.scalactic.Equality

class TableEquality[A <: GenTable[_, _, _]] extends Equality[A] {
  def areEqual(a: A, b: Any): Boolean = b match {
    case b: GenTable[_, _, _] => (
      a == b
      && b == a
      && a.hashCode == b.hashCode
      && a.elementsWithIndices == b.elementsWithIndices
      && a.elements == b.elements
    )
    case _ =>
      false
  }
}

object TableEquality {
  implicit def equality[A <: GenTable[_, _, _]] = new TableEquality[A]
}
