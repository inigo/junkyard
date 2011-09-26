package net.surguy.junkyard.pathfinder

import collection.mutable.{HashSet, PriorityQueue, ListBuffer, Queue}
import net.surguy.junkyard.utils.WithLog
import net.surguy.junkyard._
import mapping.MapSection

/**
 * Implement the A* algorithm for path finding.
 * <p>
 * From O'Reilly's AI for Game Developers:
 *
 * <pre>
 *  current node=node from open list with the lowest cost
 *  if current node = goal node then
 *    path complete
 *  else
 *    move current node to the closed list
 *    examine each node adjacent to the current node
 *    for each adjacent node
 *      if it isn't on the open list
 *        and isn't on the closed list
 *          and it isn't an obstacle then
 *            move it to open list and calculate score, based on cost + heuristic
 * </pre>
 *
 * @author Inigo Surguy
 * @created Mar 21, 2010 8:28:56 AM
 */

abstract class PathRules {
  import AstarPathFinder._
  
  def getAdjacent(c: Coord) : List[Coord]
  def isImpassible(c: Coord): Boolean
  def getCost(c: Coord): AstarPathFinder.Cost
  def getScore(n: Node, destination: Coord): Int
}

class JunkyardPathRules(val section: MapSection, val creature: Creature) extends PathRules {
  import AstarPathFinder._

  override def getAdjacent(c: Coord) : List[Coord] = List(Coord(c.x+1, c.y), Coord(c.x-1, c.y), Coord(c.x, c.y+1), Coord(c.x, c.y-1))
  override def isImpassible(c: Coord): Boolean = section.terrainAt(c).getDifficulty > 500
  override def getCost(c: Coord): Cost = section.terrainAt(c).getDifficulty
  override def getScore(n: Node, destination: Coord): Int = {
    def heuristic(a: Coord, b: Coord): Cost = Math.abs(a.x - b.x) + Math.abs(a.y - b.y)
    n.totalCost + 3 * heuristic(n.coord, destination)
  }
}

object AstarPathFinder {
  type Cost = Int

  case class Node(coord: Coord, inherentCost: Cost, parent: Option[Node]) {
    val path = toPath().reverse
    val totalCost = path.foldLeft(0)(_+_.inherentCost)
    private def toPath(): List[Node] = parent match {
      case None => List(this)
      case Some(n) => this :: n.toPath
    }
  }
}

class AstarPathFinder(rules: PathRules, maxSearchedNodes: Int = 900) extends PathFinder with WithLog {
  import AstarPathFinder._

  override def path(start: Coord, destination: Coord) : List[Coord] = {
    if (start==destination) return List[Coord]()
    if (rules.isImpassible(destination)) return List[Coord]()

    val considered = new HashSet[Coord]()
    val open = new PriorityQueue[Node]()(new Ordering[Node] {
      def compare(a: Node, b: Node) = - ( rules.getScore(a, destination).compare(rules.getScore(b, destination)) )
    })

    open += new Node(start, 0, None)
    considered += start
    var lastNodeInPath: Option[Node] = None

    while (open.size > 0 && lastNodeInPath.isEmpty && considered.size<maxSearchedNodes) {
      val current : Node = open.dequeue
      if (current.coord == destination) {
        lastNodeInPath = Some(current)
      } else {
        considered += current.coord
        val adjacentCoords = rules.getAdjacent(current.coord)
        adjacentCoords.filterNot( rules.isImpassible )
                      .filterNot( considered.contains )
                      .foreach( n => { open+=new Node(n, rules.getCost(n), Some(current)); considered+=n } )
      }
    }

    if (considered.size>=maxSearchedNodes) log.warn("Reached search limit of %s nodes going from %s to %s - blocked?".format(maxSearchedNodes, start, destination))
    if (lastNodeInPath.isDefined && log.isDebugEnabled) {
      log.debug("Reached destination with "+lastNodeInPath + " - path is "+lastNodeInPath.get.path.map(_.coord))
    }
    lastNodeInPath.map( _.path.map(_.coord).tail ).getOrElse(List[Coord]())
  }

}
