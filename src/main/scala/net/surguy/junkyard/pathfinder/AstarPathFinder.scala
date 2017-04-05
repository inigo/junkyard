package net.surguy.junkyard.pathfinder

import net.surguy.junkyard._
import net.surguy.junkyard.pathfinder.AstarPathFinder.Cost
import net.surguy.junkyard.utils.Logging

import scala.collection.mutable

/**
  * Implement the A* algorithm for path finding.
  *
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
  */
class AstarPathFinder(rules: PathRules, maxSearchedNodes: Int = 900) extends PathFinder with Logging {
  import AstarPathFinder._

  override def path(start: Coord, destination: Coord) : List[Coord] = {
    if (start==destination) return List[Coord]()
    if (rules.isImpassible(destination)) return List[Coord]()

    val considered = new mutable.HashSet[Coord]()
    val open = new mutable.PriorityQueue[PathNode]()((a: PathNode, b: PathNode) => -rules.getScore(a, destination).compare(rules.getScore(b, destination)))

    open += PathNode(start, 0, None)
    considered += start
    var lastNodeInPath: Option[PathNode] = None

    while (open.nonEmpty && lastNodeInPath.isEmpty && considered.size<maxSearchedNodes) {
      val current : PathNode = open.dequeue
      if (current.coord == destination) {
        lastNodeInPath = Some(current)
      } else {
        considered += current.coord
        val adjacentCoords = rules.getAdjacent(current.coord)
        adjacentCoords.filterNot( rules.isImpassible )
                      .filterNot( considered.contains )
                      .foreach( n => { open+=PathNode(n, rules.getCost(n), Some(current)); considered+=n } )
      }
    }

    if (considered.size>=maxSearchedNodes) log.warn("Reached search limit of %s nodes going from %s to %s - blocked?".format(maxSearchedNodes, start, destination))
    if (lastNodeInPath.isDefined && log.isDebugEnabled) {
      log.debug("Reached destination with "+lastNodeInPath + " - path is "+lastNodeInPath.get.path.map(_.coord))
    }
    lastNodeInPath.map( _.path.map(_.coord).tail ).getOrElse(List[Coord]())
  }

}

object AstarPathFinder {
  type Cost = Int
}

case class PathNode(coord: Coord, inherentCost: Cost, parent: Option[PathNode]) {
  val path: List[PathNode] = toPath().reverse
  val totalCost: Cost = path.foldLeft(0)(_+_.inherentCost)
  private def toPath(): List[PathNode] = parent match {
    case None => List(this)
    case Some(n) => this :: n.toPath
  }
}