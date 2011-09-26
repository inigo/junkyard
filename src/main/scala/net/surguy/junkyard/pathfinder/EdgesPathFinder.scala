package net.surguy.junkyard.pathfinder

import net.surguy.junkyard.zoning.ZoneLookup
import net.surguy.junkyard._
import net.surguy.junkyard.utils.WithLog

/**
 * Navigate between across a {@link MapSection map} by going between the edge nodes of each
 * {@link ZoneLookup zone}, reverting to low level path rules for navigating within a zone.
 *
 * @author Inigo Surguy
 * @created Mar 28, 2010 2:17:50 PM
 */

class EdgesPathFinder(zones: ZoneLookup, lowLevelPathRules: PathRules, maxSearchedNodes: Int = 50) extends PathFinder with WithLog {
  val zoneLevelPathFinder = new AstarPathFinder(new EdgesRules())
  val lowLevelPathFinder = new AstarPathFinder(lowLevelPathRules)
  val withinZonePathFinder = new AstarPathFinder(new PathRulesWrapper(zones, lowLevelPathRules))

  private class EdgesRules extends PathRules {
    import AstarPathFinder._
    override def getScore(n: Node, destination: Coord): Int = lowLevelPathRules.getScore(n, destination)
    override def getCost(c: Coord): Cost = lowLevelPathRules.getCost(c)
    override def isImpassible(c: Coord) = lowLevelPathRules.isImpassible(c)
    override def getAdjacent(coord: Coord) : List[Coord] = {
      val withinZoneNeighbours = zones.zoneAt(coord).map( _.filteredEdgeCoords.toList ).getOrElse(List())
      val standardNeighbours = lowLevelPathRules.getAdjacent(coord)
      standardNeighbours ::: withinZoneNeighbours 
    }
  }

  val noPath = List[Coord]()

  override def path(start: Coord, destination: Coord) : List[Coord] = {
    if (start==destination) return noPath
    if (lowLevelPathRules.isImpassible(destination)) return noPath
    if (inSameZone(start, destination)) return withinZonePathFinder.path(start, destination)

    log.debug("Looking for path between "+start+" and "+destination)
    val zonePath = zoneLevelPathFinder.path(start, destination)
    zonePath.size match {
      case 0 => noPath
      case _ =>
        if (log.isDebugEnabled) log.debug("Zoning pathfinder now finding route to "+zonePath(0)+" - zone path is "+zonePath)
        lowLevelPathFinder.path(start, zonePath(0) )
    }
  }

  private def inSameZone(a: Coord, b: Coord) = zones.zoneAt(a)==zones.zoneAt(b)
}