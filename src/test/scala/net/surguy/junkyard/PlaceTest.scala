package net.surguy.junkyard

import org.specs2.mutable.Specification


class PlaceTest extends Specification {

  "retrieving terrain costs" should {
     "give the right value directly" in { Heap().difficulty mustEqual 4 }
     "give the right value via Terrain" in { val d = Heap() match { case t: Terrain => t.difficulty }; d mustEqual 4 }
  }

}
