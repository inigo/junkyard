package net.surguy.junkyard.pathfinder

import net.surguy.junkyard.Coord
import collection.mutable.{SynchronizedSet, HashSet}

/**
 * Decorate another {@link PathFinder} with a cache of failed routes.
 * <p>
 * The most expensive pathfinding operation is when a route is impassible, because the {@link AstarPathFinder}
 * will flood the map in an attempt to find a route. Caching these failures trades off memory against time.
 *
 * @author Inigo Surguy
 * @created Apr 4, 2010 2:16:54 PM
 */
class CachingPathFinder(delegate: PathFinder) extends PathFinder {
  override def path(start: Coord, destination: Coord) = {
    if (CachingPathFinder.pathFailures.contains((start, destination))) List[Coord]() else {
      val step = delegate.path(start, destination)
      if (step.isEmpty) CachingPathFinder.pathFailures.add((start,destination))
      step
    }
  }
}

object CachingPathFinder {
  val pathFailures = new HashSet[Pair[Coord,Coord]]() with SynchronizedSet[Pair[Coord,Coord]]
}
