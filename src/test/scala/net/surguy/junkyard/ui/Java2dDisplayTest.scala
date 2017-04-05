package net.surguy.junkyard.ui

import net.surguy.junkyard.mapping.MapGenerator
import org.specs2.mutable.Specification

class Java2dDisplayTest extends Specification {

  private val map = new MapGenerator().createMap(5)
  private val d = new Java2dDisplay(600,600,10)

  "initializing" should {
     "show a frame" in {
       d.display(map) must not(throwAn[Exception])
     }
  }

  step { d.dispose() }

}
