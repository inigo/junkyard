package net.surguy.junkyard.pathfinder

import net.surguy.junkyard._

/**
 * Provide a (reasonably efficient) path between two co-ordinates.
 */

abstract class PathFinder {
  def path(start: Coord, destination: Coord) : List[Coord]
}