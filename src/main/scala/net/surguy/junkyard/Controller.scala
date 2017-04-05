package net.surguy.junkyard

import net.surguy.junkyard.mapping.MapSection
import net.surguy.junkyard.pathfinder._
import net.surguy.junkyard.ui.Java2dDisplay
import net.surguy.junkyard.utils.{LogTimeReporter, Logging, Timer}
import net.surguy.junkyard.zoning.GatewayZoneFinder

import scala.collection.LinearSeq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class Controller(val size: Int, val turns: Int = 10) extends Logging {

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

  def runTurn(startingMap: MapSection): Unit = {
    var currentMap = startingMap

    currentMap = new GatewayZoneFinder(size).addZones(currentMap)

    val timer = new Timer(ignoreFirst = 50, stages = 3, out = new LogTimeReporter(log))
    display.display(currentMap)
    for (i <- 1 to turns) {

      // To make this single-threaded again, remove the "future" and remove the Await.result
      val moves: List[MovingThing] = timer.time("pathfinding", () => splitInEight(currentMap.allCreatures).map(
        l => Future {
          l.map((c: PlacedThing) => {
            val creature = c.thing.asInstanceOf[Creature]
            if (c.path.isEmpty) {
              val pathRules = new JunkyardPathRules(currentMap, creature)
              val lowLevelPathFinder = new AstarPathFinder(pathRules)
              //        val highLevelPathFinder = new EdgesPathFinder(currentMap.zones.get, pathRules)
              val highLevelPathFinder = new CachingPathFinder(new EdgesPathFinder(currentMap.zones.get, pathRules))
              val goal = creature.goal(c.coord, currentMap)
              val newPath = (if (useZoningPathfinder) highLevelPathFinder else lowLevelPathFinder).path(c.coord, goal)
              if (newPath.isEmpty) None else Some(new MovingThing(c.thing, c.coord, newPath.head, newPath.tail))
            } else {
              val newPath = c.path
              Some(new MovingThing(c.thing, c.coord, newPath.head, newPath.tail))
            }
          })
        }).flatMap(f => Await.result(f, 10.seconds)).flatMap(_.toList) )

      val changedAreas = moves.flatMap(m => List(m.coord, m.desiredCoord) ).toSet
      currentMap = timer.time("resolving", () => currentMap.resolve(moves))
      timer.time("display", () => display.display(currentMap, changedAreas))
    }
    timer.showAverage() // with forkjoin: 28,31,30 - without: 27,26
  }

  def dispose() { display.dispose() }


  def splitInHalf[T](list: LinearSeq[T]): List[LinearSeq[T]] = { val split = list.splitAt( list.size / 2 ); List(split._1, split._2) }
  def splitInFour[T](list: LinearSeq[T]): List[LinearSeq[T]] = splitInHalf(list).flatMap(splitInHalf)
  def splitInEight[T](list: LinearSeq[T]): List[LinearSeq[T]] = splitInFour(list).flatMap(splitInFour)
  implicit def SplittableList[T](list: List[T]) = new { def splitInQuarters() = splitInHalf(list).flatMap(splitInHalf) }

}