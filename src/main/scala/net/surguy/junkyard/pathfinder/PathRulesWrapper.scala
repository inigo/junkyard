package net.surguy.junkyard.pathfinder

import net.surguy.junkyard._
import zoning.ZoneLookup

private[pathfinder] class PathRulesWrapper(zones: ZoneLookup, pathRules: PathRules) extends PathRules {
  import AstarPathFinder._

  override def getAdjacent(c: Coord) =
    zones.zoneAt(c).map( z => pathRules.getAdjacent(c).filter( z.contains(_) )).getOrElse( pathRules.getAdjacent(c) )
  override def isImpassible(c: Coord) = pathRules.isImpassible(c)
  override def getCost(c: Coord) = pathRules.getCost(c)
  override def getScore(n: Node, destination: Coord) = pathRules.getScore(n, destination)
}