package net.surguy.junkyard.pathfinder

import net.surguy.junkyard._

/**
 * Provide a (reasonably efficient) path between two co-ordinates.
 *
 * @author Inigo Surguy
 * @created Mar 20, 2010 7:45:25 PM
 */

abstract class PathFinder {
  def path(start: Coord, destination: Coord) : List[Coord]
}