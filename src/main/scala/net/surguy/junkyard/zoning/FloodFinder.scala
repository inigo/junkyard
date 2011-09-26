package net.surguy.junkyard.zoning

import collection.mutable.{Queue, HashSet}
import net.surguy.junkyard.Coord
import net.surguy.junkyard.utils.WithLog

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 27, 2010 2:42:23 PM
 */

abstract class ZoningRules {
  def getAdjacent(c: Coord) : List[Coord]
  def isImpassible(c: Coord): Boolean
  // Should change these to be immutable, so the isDone method can't alter them
  def isDone(closed: HashSet[Coord], open: Queue[Coord]): Boolean
}

class FloodFinder(maxNodes: Int = 100) extends WithLog {
  def zone(start: Coord, rules: ZoningRules): FloodResults = {
    val closed = new HashSet[Coord]()
    val open = new Queue[Coord]()

    open += start

    while (open.size > 0 && !rules.isDone(closed, open) && closed.size<maxNodes) {
      val current = open.dequeue
      log.debug("Open "+open.size+", Closed "+closed.size+", current item is "+current)
      closed += current
      val adjacentCoords = rules.getAdjacent(current)
      adjacentCoords.filterNot( n => open.exists( c => c == n ) )
                    .filterNot( closed.contains )
                    .filterNot( rules.isImpassible )
                    .foreach( c => open+=c )
    }

    FloodResults(open, closed.toList)
  }

}

case class FloodResults(open: Queue[Coord], closed: List[Coord])
