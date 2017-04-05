package net.surguy.junkyard.pathfinder

import net.surguy.junkyard.{Coord, Creature}
import net.surguy.junkyard.mapping.MapSection

/** The rules that a map must implement such that a [[net.surguy.junkyard.pathfinder.PathFinder]] can navigate across it. */
abstract class PathRules {
  def getAdjacent(c: Coord) : List[Coord]
  def isImpassible(c: Coord): Boolean
  def getCost(c: Coord): AstarPathFinder.Cost
  def getScore(n: PathNode, destination: Coord): Int
}

/** [[PathRules]] specific to a Junkyard [[net.surguy.junkyard.mapping.MapSection MapSection]]. */
class JunkyardPathRules(section: MapSection, creature: Creature) extends PathRules {
  import AstarPathFinder.Cost

  override def getAdjacent(c: Coord) : List[Coord] = List(Coord(c.x+1, c.y), Coord(c.x-1, c.y), Coord(c.x, c.y+1), Coord(c.x, c.y-1))
  override def isImpassible(c: Coord): Boolean = section.terrainAt(c).getDifficulty > 500
  override def getCost(c: Coord): Cost = section.terrainAt(c).getDifficulty
  override def getScore(n: PathNode, destination: Coord): Int = {
    def heuristic(a: Coord, b: Coord): Cost = Math.abs(a.x - b.x) + Math.abs(a.y - b.y)
    n.totalCost + 3 * heuristic(n.coord, destination)
  }
}
