package org.fs.utility.collection

import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RichCollectionImplicitsSpec extends Spec {

  object `RichIterable ` {
    import RichCollectionImplicits.RichIterable

    def `asd` = {
      Seq(Option(1)).traverse()
      //
    }
  }
}
