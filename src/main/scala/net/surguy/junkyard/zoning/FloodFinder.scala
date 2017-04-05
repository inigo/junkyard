package net.surguy.junkyard.zoning

import net.surguy.junkyard.Coord
import net.surguy.junkyard.utils.Logging

import scala.collection.mutable

abstract class ZoningRules {
  def getAdjacent(c: Coord) : List[Coord]
  def isImpassible(c: Coord): Boolean
  // Should change these to be immutable, so the isDone method can't alter them
  def isDone(closed: mutable.HashSet[Coord], open: mutable.Queue[Coord]): Boolean
}

class FloodFinder(maxNodes: Int = 100) extends Logging {
  def zone(start: Coord, rules: ZoningRules): FloodResults = {
    val closed = new mutable.HashSet[Coord]()
    val open = new mutable.Queue[Coord]()

    open += start

    while (open.nonEmpty && !rules.isDone(closed, open) && closed.size<maxNodes) {
      val current = open.dequeue
      log.debug("Open "+open.size+", Closed "+closed.size+", current item is "+current)
      closed += current
      val adjacentCoords = rules.getAdjacent(current)
      adjacentCoords.filterNot( n => open.contains(n) )
                    .filterNot( closed.contains )
                    .filterNot( rules.isImpassible )
                    .foreach( c => open+=c )
    }

    FloodResults(open, closed.toList)
  }

}

case class FloodResults(open: mutable.Queue[Coord], closed: List[Coord])
