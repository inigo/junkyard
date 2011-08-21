package net.surguy.junkyard

import mapping.MapSection
import ui.{Java2dDisplay, ConsoleDisplay}
import scala.actors.Futures._
import net.surguy.junkyard.pathfinder._
import utils.{LogTimeReporter, WithLog, Timer}
import zoning.{GatewayZoneFinder, ZoneFinder}
import collection.LinearSeq

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 21, 2010 7:49:52 AM
 */

class Controller(val size: Int, val turns: Int = 10) extends WithLog {

//  private val display = new ConsoleDisplay(size)
  private val display = new Java2dDisplay(600,600,size)

  private val useZoningPathfinder = true

  private val availableProcessors = java.lang.Runtime.getRuntime.availableProcessors

  init()

  def init() {
    // Set up thread pool size - this configures the shared DaemonScheduler
    // Creating futures that use your own pool would require copy-and-pasting library code
    System.setProperty("actors.maxPoolSize",""+availableProcessors*2 )
    System.setProperty("actors.corePoolSize",""+availableProcessors*2 )
  }

  def runTurn(startingMap: MapSection) = {
    var currentMap = startingMap

    currentMap = new GatewayZoneFinder(size).addZones(currentMap)

    val timer = new Timer(ignoreFirst = 50, stages = 3, out = new LogTimeReporter(log))
    display.display(currentMap)
    for (i <- (1 to turns)) {

      // To make this single-threaded again, remove the "future" and remove the .map(_()) - no other changes
      val moves: List[MovingThing] = timer.time("pathfinding", () => splitInEight(currentMap.allCreatures).map(
        l => future { l.map( (c: PlacedThing) => {
          val creature = c.thing.asInstanceOf[Creature]
          if (c.path.isEmpty) {
            val pathRules = new JunkyardPathRules(currentMap, creature)
            val lowLevelPathFinder = new AstarPathFinder(pathRules)
    //        val highLevelPathFinder = new EdgesPathFinder(currentMap.zones.get, pathRules)
            val highLevelPathFinder = new CachingPathFinder(new EdgesPathFinder(currentMap.zones.get, pathRules))
            val goal = creature.goal(c.coord, currentMap)
            var newPath = (if (useZoningPathfinder) highLevelPathFinder else lowLevelPathFinder).path(c.coord, goal)
            if (newPath.isEmpty) None else Some(new MovingThing(c.thing, c.coord, newPath.head, newPath.tail))
          } else {
            val newPath = c.path
            Some(new MovingThing(c.thing, c.coord, newPath.head, newPath.tail))
          }
        })
      }).map(_()).flatten.flatMap(_.toList) )
        // .map(_())
      val changedAreas = moves.flatMap(m => List(m.coord, m.desiredCoord) ).toSet
      currentMap = timer.time("resolving", () => currentMap.resolve(moves))
      timer.time("display", () => display.display(currentMap, changedAreas))
    }
    timer.showAverage() // with forkjoin: 28,31,30 - without: 27,26
  }

  def dispose() { display.dispose() }


  def splitInHalf[T](list: LinearSeq[T]) = { val split = list.splitAt( list.size / 2 ); List(split._1, split._2) }
  def splitInFour[T](list: LinearSeq[T]) = splitInHalf(list).map( splitInHalf(_) ).flatten
  def splitInEight[T](list: LinearSeq[T]) = splitInFour(list).map( splitInFour(_) ).flatten
  implicit def SplittableList[T](list: List[T]) = new { def splitInQuarters() = splitInHalf(list).map( splitInHalf(_) ).flatten }

}