package net.surguy.junkyard.pathfinder

import net.surguy.junkyard._
import zoning.ZoneLookup

private[pathfinder] class PathRulesWrapper(zones: ZoneLookup, pathRules: PathRules) extends PathRules {
  import AstarPathFinder._

  override def getAdjacent(c: Coord): List[Coord] =
    zones.zoneAt(c).map( z => pathRules.getAdjacent(c).filter( z.contains )).getOrElse( pathRules.getAdjacent(c) )
  override def isImpassible(c: Coord): Boolean = pathRules.isImpassible(c)
  override def getCost(c: Coord): Cost = pathRules.getCost(c)
  override def getScore(n: PathNode, destination: Coord): Cost = pathRules.getScore(n, destination)
}