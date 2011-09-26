package net.surguy.junkyard

import zoning.ZoneId

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 20, 2010 5:15:41 PM
 */

abstract class Thing {
  def at(x: Int, y: Int) = new PlacedThing(this, Coord(x,y), List[Coord]())
}

class PlacedThing(val thing: Thing, val coord: Coord, val path: List[Coord]) {
  override def toString = "PlacedThing(%s at %s with path length %s)".format(thing, coord, path.size)
}
class MovingThing(val thing: Thing, val coord: Coord, val desiredCoord: Coord, val path: List[Coord]) {
  override def toString = "MovingThing(%s moving from %s to %s with a path of %s)".format(thing, coord, desiredCoord, path.size)
}


case class Coord(x: Int, y: Int)

case class ZonedTile(id: ZoneId) extends Thing

abstract class Item

case class Oil() extends Item
case class CrudeOil() extends Item
case class Metal() extends Item
case class Drum() extends Item