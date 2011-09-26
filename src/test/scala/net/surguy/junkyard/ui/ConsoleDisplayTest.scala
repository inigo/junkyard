package net.surguy.junkyard.ui

import org.specs.SpecificationWithJUnit
import net.surguy.junkyard._
import net.surguy.junkyard.mapping.MapGenerator

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 20, 2010 6:27:16 PM
 */

class ConsoleDisplayTest extends SpecificationWithJUnit {

  val map = new MapGenerator().createMap(5)

  "displaying a map" should {
     "do something sensible" in {
       new ConsoleDisplay(10).display(map)
     }
  }

}
