package net.surguy.junkyard.pathfinder

import net.surguy.junkyard._
import net.surguy.junkyard.mapping.MapGenerator
import org.specs2.mutable.Specification
import utils.Logging
import zoning.ZoneFinder

class EdgesPathFinderTest extends Specification with Logging {

  private val size = 20
  private val map = new MapGenerator(0).createMap(size)
  private val zonedMap = new ZoneFinder(size).addZones(map)

  val pathRules = new JunkyardPathRules(map, Robot("glob"))
  val zonedPathFinder = new EdgesPathFinder(zonedMap.zones.get, pathRules)

  "finding path" should {
    "work normally within a zone" in {
      zonedPathFinder.path(Coord(10, 4), Coord(15,13)).headOption mustEqual Some(Coord(11, 4))
    }

    "should not get stuck" in {
      var start = Coord(6, 13)
      val nextCoord = zonedPathFinder.path(start, Coord(11,4)).head
      log.debug("---")
      zonedPathFinder.path(nextCoord, Coord(11,4)).headOption mustNotEqual Some(start)
    }
    "still should not get stuck" in {
      var start = Coord(9, 11)
      val nextCoord = zonedPathFinder.path(start, Coord(10,7)).head
      log.debug("---")
      zonedPathFinder.path(nextCoord, Coord(10,7)).headOption mustNotEqual Some(start)
    }
/*
    "work normally within a zone" in {
      zonedPathFinder.nextStep(Coord(5, 5), Coord(1,1)) must be equalTo(Some(Coord(4,5)))
    }
    "navigate towards the next zone on a longer trip" in {
      zonedPathFinder.nextStep(Coord(2, 3), Coord(16,16)) must be equalTo(Some(Coord(3,3)))
    }
    "should not go onto impassible areas" in {
      zonedPathFinder.nextStep(Coord(15, 11), Coord(15,12)) must be equalTo(None)
    }
    "should go off impassible areas" in {
      zonedPathFinder.nextStep(Coord(15, 12), Coord(15,11)) must be equalTo(Some(Coord(15,11)))
    }
    */
//    "navigate towards the next zone in a small trip" in {
//      zonedPathFinder.nextStep(Coord(5, 14), Coord(3,13)) must be equalTo(Some(Coord(5,13)))
//      zonedPathFinder.nextStep(Coord(5, 15), Coord(3,13)) must not be equalTo(Some(Coord(5,14)))
//    }
  }

}