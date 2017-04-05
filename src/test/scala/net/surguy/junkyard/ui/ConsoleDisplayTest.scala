package net.surguy.junkyard.ui

import net.surguy.junkyard.mapping.MapGenerator
import org.specs2.mutable.Specification

class ConsoleDisplayTest extends Specification {

  private val map = new MapGenerator().createMap(5)

  "displaying a map" should {
     "do something sensible" in {
       new ConsoleDisplay(10).display(map) must not(throwAn[Exception])
     }
  }

}
