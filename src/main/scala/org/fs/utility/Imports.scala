package org.fs.utility

import org.fs.utility.collection.RichCollectionImplicits

trait Imports
  extends RichGeneralImplicits
  with RichCollectionImplicits

object Imports extends Imports
