package net.surguy.junkyard

import net.surguy.junkyard.mapping.MapGenerator
import org.specs2.mutable.Specification

class ControllerTest extends Specification {
  private val size = 50
  private val controller: Controller = new Controller(size, 200)

//  val items = Robot("glub").at(5,5) :: Heap().at(3,4) :: Heap().at(3,3) :: PowerSocket().at(1,1) :: Heap().at(3,1) :: Heap().at(4,1) :: Heap().at(3,5) :: Heap().at(3,2) :: PowerSocket().at(9,9) :: List()
//  val map = new MapSection(items)
  private val map = new MapGenerator(0).createMap(size)

  "running turns" should {
     "do something sensible" in {
       controller.runTurn(map) must not(throwAn[Exception])
     }
  }

  step { controller.dispose() }

}
