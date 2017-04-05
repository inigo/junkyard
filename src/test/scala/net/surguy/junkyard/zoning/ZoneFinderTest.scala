package net.surguy.junkyard.zoning

import net.surguy.junkyard.mapping.MapGenerator
import net.surguy.junkyard.utils.Logging
import org.specs2.mutable.Specification


class ZoneFinderTest extends Specification with Logging {
  private val size = 20
  private val map = new MapGenerator(0).createMap(size)
  "generating zones with ZoneFinder" should {
    val zonedMap = new ZoneFinder(size).addZones(map)
    var zones = zonedMap.zones.get.zones
    "create a small number of zones for a small map with ZoneFinder" in {
//      new Java2dDisplay(600,600,size).display(zonedMap)
      zones.size must beLessThan(30)
      zones.size must beGreaterThan(5)
    }
  }

  "generating zones with GatewayZoneFinder" should {
     val zonedMap = new GatewayZoneFinder(size).addZones(map)
     val zones = zonedMap.zones.get.zones
     "create a small number of zones for a small map with GatewayZoneFinder" in {
       zones.size must beLessThan(30)
       zones.size must beGreaterThan(5)
     }
//     "have items that are on the edge" in {
//       zones.foreach(z => {
//         log.info("Edge coords "+z.edgeCoords.size+" of "+z.tiles.size)
//         z.edgeCoords.size must beGreaterThan(0)
//         z.tiles.size must beGreaterThanOrEqualTo( z.edgeCoords.size )
//       })
//     }
     "have some items that are not on the edge" in {
       zones.find(z => z.edgeCoords.size < z.tiles.size) must beSome
     }
//     "have fewer filtered edge items than items" in {
//       zones.foreach(z => {
//         log.info("Edge coords "+z.edgeCoords.size+" filtered to "+z.filteredEdgeCoords.size)
//         z.filteredEdgeCoords.size must beGreaterThan(0)
//         z.edgeCoords.size must beGreaterThanOrEqualTo( z.filteredEdgeCoords.size )
//       })
//     }
  }

}