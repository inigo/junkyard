package net.surguy.junkyard.zoning

import collection.mutable.{HashSet, ListBuffer}
import net.surguy.junkyard.utils.Logging
import net.surguy.junkyard.Coord

import scala.util.control.Breaks._
import net.surguy.junkyard.mapping.MapSection

import scala.collection.mutable

/**
 * Uses the "Gateway decomposition algorithm" described in "Improved Heuristics for Optimal Pathfinding on Game Maps"
 * (Bjornsson and Halldorsson) to describe the map into zones.
 */
class GatewayZoneFinder(size: Int) extends Logging {

  def addZones(initialMap: MapSection) = {
    val allPassableTilesInMap = for (x <- 0 until size;
                                     y <- 0 until size;
                                     c = Coord(x,y)
                                     if initialMap.terrainAt(c).getDifficulty < 500) yield c
    val unzonedTiles = mutable.HashSet[Coord]()
    unzonedTiles ++= allPassableTilesInMap

    var currentMap = initialMap
    var zoneBuilder = new ZoneBuilder()

    log.info("Adding zones : currently "+unzonedTiles.size+" unzoned tiles")

    while (unzonedTiles.nonEmpty) {
      // Start on the top left unzoned tile
      val startingTile = topLeft(unzonedTiles.toSeq)
      log.debug("Starting at "+startingTile)
      var y = startingTile.y

      val currentZone = new ListBuffer[List[Coord]]()

      def obstructed(x: Int, y: Int) = initialMap.terrainAt(Coord(x,y)).getDifficulty >= 500
      def outsideArea(x: Int, y: Int) = x < 0 || x > size || y < 0 || y > size
      def furtherRightThanPreviousLine(x: Int, y: Int) = {
        if (currentZone.isEmpty) false // No previous line, so can't be further right than it
        x > currentZone.last.last.x
      }
      def unobstructedUnzonedSpaceAbove(x: Int, y: Int) = {
        val above = Coord(x, y-1)
        (!obstructed(above.x,above.y) && (unzonedTiles.contains(above) && !currentZone.flatten.contains(above)))
      }

      var isShrinkingLeft = false
      var isShrinkingRight = false
      var finishedZone = false

      // Starting the green one at 7,10
      // will dupe 8,11 and 7,11

      while (!finishedZone) {
        breakable {
          var x = startingTile.x
          log.debug("Starting line - coords are %s, %s".format(x,y))
          if (outsideArea(x,y)) {
            finishedZone = true
            break
          }

          // If it's obstructed, then go right until it is no longer obstructed,
          //      provided we're not going further right than the previous line did
          //      and we're not going out of the area
          while (obstructed(x,y) && !furtherRightThanPreviousLine(x,y) && !outsideArea(x,y) && unzonedTiles.contains(Coord(x,y))) x = x + 1
          if (obstructed(x,y) || !unzonedTiles.contains(Coord(x,y))) {
            log.debug("Cannot find an unobstructed tile - stopping")
            finishedZone = true
            break // Might not need this- everything else checks for obstruction?
          }

          val startX = x
          // Go left until we either encounter an obstacle, encounter an already zoned area, or reach the end of the map
          val line = new ListBuffer[Coord]()
          while (!obstructed(x-1,y) && !outsideArea(x-1,y) && unzonedTiles.contains(Coord(x-1, y))) {
            x = x - 1
            line += Coord(x,y)
          }
          log.debug("Leftmost point is %s,%s".format(x,y))

          // Go right while collecting tiles, until we hit an obstacle, reach the end of the map, or there is an unobstructed unzoned space above
          x = startX
          line += Coord(x,y)
          while (!obstructed(x+1,y) && !outsideArea(x+1,y) && !unobstructedUnzonedSpaceAbove(x+1,y) && unzonedTiles.contains(Coord(x+1,y))) {
            x = x + 1
            line += Coord(x,y)
          }
          log.debug("Rightmost point is %s,%s".format(x,y))

          if (line.isEmpty) {
            log.debug("Current line is empty - stopping")
            finishedZone = true
            break
          }

          if (line.nonEmpty && currentZone.nonEmpty) {
            // Check whether the line is shrinking, growing, or staying the same to the right
            val rightMost = line.last.x
            val previousRightMost = currentZone.last.last.x
            // If it's shrinking, set a flag to show that it has shrunk on the right
            if (rightMost < previousRightMost) isShrinkingRight = true
            // If it's growing, and the "shrinking right" flag is set, then finish the zone and don't add our current line
            if (rightMost > previousRightMost && isShrinkingRight) {
              log.debug("Starting to regrow to the right - stopping (currently at %s, but the previous rightmost was %s)".format(rightMost, previousRightMost))
              finishedZone = true
              break
            }

            // Check whether the line is shrinking, growing, or staying the same to the left
            val leftMost = line.head.x
            val previousLeftMost = currentZone.last.head.x
            // If it's shrinking, set a flag to show that it has shrunk on the left
            if (leftMost > previousLeftMost) isShrinkingLeft = true
            // If it's growing, and the "shrinking left" flag is set, then finish the zone and don't add our current line
            if (leftMost < previousLeftMost && isShrinkingLeft) {
              log.debug("Starting to regrow to the left - stopping (currently at %s, but the previous leftmost was %s)".format(leftMost, previousLeftMost))
              finishedZone = true
              break
            }
          }

          // Otherwise, add our current line to the zone
          currentZone += line.toList
          // move down a line - new start is on the same X position as the initial start, but a line further down than our current position
          y = y + 1
          log.debug("Moving down to line "+y)
        }
      }
      unzonedTiles --= currentZone.flatten

      zoneBuilder = zoneBuilder.createZone(currentZone.flatten)
      // Useful for debugging - shows the state just before the failed zone was added:      
//      try {
//        zoneBuilder = zoneBuilder.createZone(currentZone.flatten.toSeq)
//      } catch {
//        case e => log.error("Failed to create zone "+e.getMessage); unzonedTiles.clear()
//      }

      log.info("Adding zone containing %s tiles on %s lines - now %s tiles left".format(currentZone.flatten.size, currentZone.size, unzonedTiles.size ))
    }
    initialMap.addZones(zoneBuilder.toLookup)
  }


  def topLeft(tiles: Seq[Coord]): Coord = tiles.min(new Ordering[Coord] {
      def compare(a: Coord, b: Coord) = if (a.y.compare(b.y)!=0) a.y.compare(b.y) else a.x.compare(b.x)
  })

}
