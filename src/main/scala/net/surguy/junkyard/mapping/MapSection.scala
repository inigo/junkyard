package net.surguy.junkyard.mapping

import net.surguy.junkyard.zoning._
import net.surguy.junkyard.utils.WithLog
import net.surguy.junkyard._

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 20, 2010 4:58:58 PM
 */

class MapSection private[mapping](private val items: List[PlacedThing], private val terrain: Array[Array[Terrain]], val zones: Option[ZoneLookup] = None) extends WithLog {

  val defaultTerrain = Glass()

  def allCreatures(): List[PlacedThing] = items.filter(i => i.thing.isInstanceOf[Creature] )
  def allItems: List[PlacedThing] = items

  private def withinBounds(coord: Coord) = coord.x >=0 && coord.x < terrain.size && coord.y >= 0 && coord.y < terrain.size

  def terrainAt(coord: Coord): Terrain = if (withinBounds(coord)) terrain(coord.x)(coord.y) else defaultTerrain

  def itemsAt(coord: Coord): List[Thing] = items.filter(t => coord == t.coord).map(_.thing)

  def nearbyItems(coord: Coord): List[PlacedThing] = {
    // Ideally. we'd be doing this with a stream, or similar, that would be lazily evaluated
    def distanceTo(p: PlacedThing) = Math.abs(coord.x-p.coord.x) + Math.abs(coord.y-p.coord.y)
    items.sortWith((a: PlacedThing, b: PlacedThing) => distanceTo(a)<distanceTo(b))
  }

  def resolve(moves: List[MovingThing]): MapSection = {
    def isMoving(t: Thing): Option[MovingThing] = moves.find(m => m.thing == t)
    val newItems = items.map(p => isMoving(p.thing).map(m => new PlacedThing(m.thing, m.desiredCoord, m.path) ).getOrElse(p) )
    new MapSection(newItems, terrain, zones)
  }

  def addZones(zones: ZoneLookup) = new MapSection(items, terrain, Some(zones))

}
