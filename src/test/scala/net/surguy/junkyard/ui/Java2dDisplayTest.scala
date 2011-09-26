package net.surguy.junkyard.ui

import org.specs.SpecificationWithJUnit
import net.surguy.junkyard._
import net.surguy.junkyard.mapping.MapGenerator

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 21, 2010 4:46:11 PM
 */

class Java2dDisplayTest extends SpecificationWithJUnit {

  val map = new MapGenerator().createMap(5)

  "initializing" should {
     "show a frame" in {
       val d = new Java2dDisplay(600,600,10)
       d.display(map)
     }
  }


}
