package net.surguy.junkyard.mapping

import net.surguy.junkyard.ui.ConsoleDisplay
import org.specs2.mutable.Specification

class MapGeneratorTest extends Specification {

  "generating a map" should {
     "create a small map" in {
       val display = new ConsoleDisplay(5)
       display.display(new MapGenerator().createMap(5)) must not(throwAn[Exception])
     }
     "create a larger map" in {
       val display = new ConsoleDisplay(25)
       display.display(new MapGenerator().createMap(25)) must not(throwAn[Exception])
     }
  }

}
