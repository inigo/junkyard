package net.surguy.junkyard.pathfinder

import java.util.concurrent.ConcurrentHashMap

import net.surguy.junkyard.Coord

/**
 * Decorate another {{{PathFinder}}} with a cache of failed routes.
 *
 * The most expensive pathfinding operation is when a route is impassible, because the {{{AstarPathFinder}}}
 * will flood the map in an attempt to find a route. Caching these failures trades off memory against time.
 */
class CachingPathFinder(delegate: PathFinder) extends PathFinder {
  override def path(start: Coord, destination: Coord): List[Coord] = {
    if (CachingPathFinder.pathFailures.contains((start, destination))) List[Coord]() else {
      val step = delegate.path(start, destination)
      if (step.isEmpty) CachingPathFinder.pathFailures.put((start,destination), Unit)
      step
    }
  }
}

object CachingPathFinder {
  private val pathFailures = new ConcurrentHashMap[(Coord,Coord), Unit]
}
