package net.surguy.junkyard.pathfinder

import net.surguy.junkyard.utils.WithLog
import net.surguy.junkyard.Coord
import net.surguy.junkyard.zoning.ZoneLookup

/**
 * Navigate across a {@link MapSection map} by going between the central points of each
 * {@link ZoneLookup zone}, reverting to low level path rules for navigating within a zone.
 * <p>
 * This pathfinder should be slightly more efficient than the {@link EdgesPathFinder edges pathfinder}
 * because each zone is a single node, whereas the edges pathfinder has multiple nodes per zone.
 * <p>
 * It currently fails in some cases (units get into a perpetual loop) because the low level pathfinder
 * and the zoning pathfinder disagree on the route to take - perhaps because the zoning pathfinder does
 * not properly take account of terrain costs.
 *
 * @author Inigo Surguy
 * @created Mar 28, 2010 2:17:50 PM
 */
class ZoningPathFinder(zones: ZoneLookup, lowLevelPathRules: PathRules, maxSearchedNodes: Int = 50) extends PathFinder with WithLog {
  val zoneLevelPathFinder = new AstarPathFinder(new ZoneRules())
  val lowLevelPathFinder = new AstarPathFinder(lowLevelPathRules)
  val withinZonePathFinder = new AstarPathFinder(new PathRulesWrapper(zones, lowLevelPathRules))

  private class ZoneRules extends PathRules {
    import AstarPathFinder._
    override def getScore(n: Node, destination: Coord): Int = {
      def heuristic(a: Coord, b: Coord): Cost = Math.abs(a.x - b.x) + Math.abs(a.y - b.y)
      n.totalCost + 3 * heuristic(n.coord, destination)
    }
    override def getCost(c: Coord): Cost = 1
    override def isImpassible(c: Coord) = false
    override def getAdjacent(coord: Coord) : List[Coord] =
      zones.zoneAt(coord).map( _.neighbours.map( zones.lookup(_).center ).toList ).getOrElse( List() )
  }

  val noPath = List[Coord]()

  override def path(start: Coord, destination: Coord) : List[Coord] = {
    if (start==destination) return noPath
    if (lowLevelPathRules.isImpassible(destination)) return noPath
    if (inSameZone(start, destination)) return withinZonePathFinder.path(start, destination)

    log.debug("Looking for path between "+start+" and "+destination)
    val startZone = zones.zoneAt(start)
    val destinationZone = zones.zoneAt(destination)
    if (startZone.isEmpty || destinationZone.isEmpty) return lowLevelPathFinder.path(start, destination)
    val zonePath = zoneLevelPathFinder.path(start, destinationZone.get.center )
    log.debug("Finding path to centre of zone "+destinationZone.get.id +" at "+destinationZone.get.center)
    zonePath.size match {
      case 0 => noPath
      case 1 => lowLevelPathFinder.path(start, destination)
      case 2 => lowLevelPathFinder.path(start, destination)
      // Path to the next-but-one zone - gives a smoother path, but still efficient (and avoids annoying getting stuck) (GPG1)
      case _ =>
        if (log.isDebugEnabled) log.debug("Zoning pathfinder returns path of "+zonePath)
        lowLevelPathFinder.path(start, zonePath(1) )
    }
  }

  private def inSameZone(a: Coord, b: Coord) = zones.zoneAt(a)==zones.zoneAt(b)
}
