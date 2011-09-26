package net.surguy.junkyard

import org.specs.SpecificationWithJUnit

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 20, 2010 9:31:40 PM
 */

class PlaceTest extends SpecificationWithJUnit {

  "retrieving terrain costs" should {
     "give the right value directly" in { Heap().difficulty must be equalTo(4) }
     "give the right value via Terrain" in { val d = Heap() match { case t: Terrain => t.difficulty }; d must be equalTo(4) }
  }

}
