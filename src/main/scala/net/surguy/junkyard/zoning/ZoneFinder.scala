package net.surguy.junkyard.zoning

import net.surguy.junkyard.utils.Logging
import net.surguy.junkyard.Coord
import net.surguy.junkyard.mapping.MapSection

import scala.collection.mutable

/**
 * An alternative zoning algorithm to the GatewayZoneFinder.
 *
 * Produces zones that are more ragged but more contiguous. Needs to be changed to:
 *  - choose start points from the list of all unzoned squares (currently we miss zoning unreachable squares) - maybe not too serious
 *  - merge very small zones with their neighbours
 */
class ZoneFinder(val size: Int) extends Logging {

  /**
   * Rules for doing pathfinding while zoning - spread out in all directions, but stay on the map, and don't revisit any
   * areas that are already zoned.
   */
  class ZoningPathRules(val section: MapSection, val zoneBuilder: ZoneBuilder, val size: Int) extends ZoningRules {
    override def getAdjacent(c: Coord) : List[Coord] = List(Coord(c.x+1, c.y), Coord(c.x-1, c.y), Coord(c.x, c.y+1), Coord(c.x, c.y-1))
    override def isImpassible(c: Coord): Boolean = c match {
      case Coord(x,y) if x<0 || y<0 || x>size || y>size => true
      case _ if zoneBuilder.isInZone(c) => true
      case _ if section.terrainAt(c).getDifficulty > 500 => true
      case _ => false
    }
    override def isDone(closed: mutable.HashSet[Coord], open: mutable.Queue[Coord]): Boolean = closed.size > 10 && (open.size < (closed.size-5) / 3)
  }

  def addZones(initialMap: MapSection): MapSection = {
    val maxZoneSize = 500

    val open = new mutable.Queue[Coord]
    open += Coord(3,3) // arbitrary start point - needs to be passible

    var zoneBuilder = new ZoneBuilder()
    while (open.nonEmpty) {
      val results = new FloodFinder(maxZoneSize).zone(open.dequeue, new ZoningPathRules(initialMap, zoneBuilder, size))
      zoneBuilder = zoneBuilder.createZone(results.closed)
      open ++= results.open
      open.dequeueAll(c => results.closed.contains(c))
    }
    initialMap.addZones(zoneBuilder.toLookup)
  }


}