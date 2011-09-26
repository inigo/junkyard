package net.surguy.junkyard.zoning

import util.Random
import net.surguy.junkyard.{ZonedTile, Coord}
import net.surguy.junkyard.utils.WithLog
import collection.immutable.HashSet

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 28, 2010 3:41:55 PM
 */
class ZoneBuilder(zones : Map[Int, Set[Coord]]= Map()) {
  private def nextZoneId(): Int = new Random().nextInt(16777000) // we can use this directly as a color

  def createZone(zoneCoords: Seq[Coord] ): ZoneBuilder = {
    val zoneId = nextZoneId()
    assert(allZonedCoords.intersect(zoneCoords.toSet).isEmpty, "Tried to add duplicate coords for "+allZonedCoords.intersect(zoneCoords.toSet))
    val newZones: Map[Int, Set[Coord]] = zones+((zoneId, zoneCoords.toSet))
    new ZoneBuilder( newZones )
  }

  def isInZone(coord: Coord): Boolean = allZonedCoords.toSet.contains(coord)

  private def allZonedCoords = zones.values.flatten.toSet

  def toLookup() : ZoneLookup = {
    new ZoneLookup( zones.map( x => new Zone(ZoneId(x._1), x._2, findNeighbours(x._1), edgeCoords(x._1), filterCoords(edgeCoords(x._1)) ) ).toList )
  }

  private def findNeighbours(id: Int): Set[ZoneId] = {
    val coordLookup = zones.map(_.swap)
    val allNeighbouringCoords = zones(id).flatMap( neighbouringCoords(_) )
    // A neighbouring zone is a zone that contains one of the neighbouring co-ordinates
    allNeighbouringCoords.map(c => zones.find( entry => zones(entry._1).contains(c) )).flatMap( _.toList ).map( entry => entry._1).map(ZoneId(_))
  }

  private def edgeCoords(id: Int): Set[Coord] = {
    def zoneAt(c: Coord): Option[Int] = zones.keySet.find( k => zones(k).contains(c) )
    val edges = zones(id).filter( c => {
      // An edge is a tile with neighbours in zones other than the current one
      val neighbours: List[Int] = neighbouringCoords(c).map(n => zoneAt(n)).flatMap( _.toList )
      if (neighbours.size==0) false else neighbours.find( _ != id ).isDefined
    }).toSet
    if (edges.isEmpty) zones(id) else edges
  }

  private def filterCoords(edgeCoords: Set[Coord]): Set[Coord] =
    edgeCoords.filterNot(c => ((c.x % 2 == 0) && edgeCoords.containsAny(List(Coord(c.x+1,c.y), Coord(c.x-1,c.y)))) ||
            ((c.y % 2 == 0) && edgeCoords.containsAny(List(Coord(c.x,c.y+1), Coord(c.x,c.y-1)))) )

  implicit def richSet[T](set: Set[T]) = new {
    def containsAny(items: List[T]): Boolean = !items.forall( !set.contains(_) )
  }

  private def neighbouringCoords(c: Coord) = List( Coord(c.x+1,c.y), Coord(c.x-1,c.y), Coord(c.x,c.y+1), Coord(c.x,c.y-1) )
}

class ZoneLookup(val zones: List[Zone]) {
  // @todo Getting the iterator used to take 12% of the time in pathfinding, perhaps partly because HashSet.iterator makes a defensive copy
  def zoneAt(coord: Coord): Option[Zone] = zones.find( _.tiles.contains(coord) )
  def lookup(id: ZoneId):Zone = zones.find(_.id == id).get
}

import java.awt.Color

class Zone(val id: ZoneId, val tiles: Set[Coord], val neighbours: Set[ZoneId], val edgeCoords: Set[Coord], val filteredEdgeCoords: Set[Coord]) extends WithLog {
  def center(): Coord = {
    val x: Int = ( tiles.map(_.x).min + tiles.map(_.x).max ) / 2
    val y: Int = ( tiles.map(_.y).min + tiles.map(_.y).max ) / 2
    val center = Coord(x,y)
    center match {
      case _ if tiles.contains(center) => center
      case _ if tiles.contains(Coord(x+1,y)) => Coord(x+1,y)
      case _ if tiles.contains(Coord(x-1,y)) => Coord(x-1,y)
      case _ if tiles.contains(Coord(x,y+1)) => Coord(x,y+1)
      case _ if tiles.contains(Coord(x,y-1)) => Coord(x,y-1)
      case _ =>
        log.debug("Cannot find center tile inside zone "+id+" at "+center+"- using first tile of "+tiles.head+"instead")
        tiles.head
    }
  }

  def contains(c: Coord): Boolean = tiles.contains(c)
  def getColor(): Color = new Color(id.value)
}
case class ZoneId(value: Int)
