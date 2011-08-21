package net.surguy.junkyard.mapping

import org.specs.SpecificationWithJUnit
import net.surguy.junkyard.ui.ConsoleDisplay

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 21, 2010 10:46:20 PM
 */

class MapGeneratorTest extends SpecificationWithJUnit {

  "generating a map" should {
     "create a small map" in {
       val display = new ConsoleDisplay(5)
       display.display(new MapGenerator().createMap(5)) mustNot throwAnException
     }
     "create a larger map" in {
       val display = new ConsoleDisplay(25)
       display.display(new MapGenerator().createMap(25)) mustNot throwAnException
     }
  }

}
