package net.surguy.junkyard

import net.surguy.junkyard.mapping.MapGenerator
import org.specs.SpecificationWithJUnit

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 21, 2010 8:10:02 AM
 */

class ControllerTest extends SpecificationWithJUnit {
  val size = 50

//  val items = Robot("glub").at(5,5) :: Heap().at(3,4) :: Heap().at(3,3) :: PowerSocket().at(1,1) :: Heap().at(3,1) :: Heap().at(4,1) :: Heap().at(3,5) :: Heap().at(3,2) :: PowerSocket().at(9,9) :: List()
//  val map = new MapSection(items)
    val map = new MapGenerator(0).createMap(size)

  "running turns" should {
     "do something sensible" in {
       new Controller(size, 200).runTurn(map)
     }
  }

}
