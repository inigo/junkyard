package net.surguy.junkyard.mapping

import collection.mutable.ListBuffer
import util.Random
import net.surguy.junkyard._
import utils.WithLog
import collection.TraversableLike

/**
 * Randomly create a map.
 *
 * @author Inigo Surguy
 * @created Mar 21, 2010 10:02:16 PM
 */
class MapGenerator(private val seed: Long = System.currentTimeMillis) extends WithLog {
  private val rnd = new Random(seed)
  
  def createMap(size: Int, specifiedItems: List[PlacedThing] = List()): MapSection = {
    val terrain: Array[Array[Terrain]] = Array.fill(size,size)(Glass())

    val items = new ListBuffer[Option[PlacedThing]]()
    for (x <- (0 until size);
         y <- (0 until size);
         c = Coord(x,y)) {
      terrain(x)(y) = randomTerrain()
      items += randomPlace().collect{ case t => new PlacedThing(t,c,List[Coord]()) }
      items += randomCreature().collect{ case t => new PlacedThing(t,c,List[Coord]()) }
    }

    for (x <- (0 until size by size/3);
         y <- (0 until size);
         c = Coord(x,y)) {
      if (rnd.nextInt(5)<4) terrain(x)(y) = Wall()
    }

    for (x <- (0 until size);
         y <- (0 until size by size/3);
         c = Coord(x,y)) {
      if (rnd.nextInt(5)<4) terrain(x)(y) = Wall()
    }

    val mapSection = if (specifiedItems.isEmpty)
                        new MapSection(items.flatMap(_.toList).toList, terrain) else
                        new MapSection(specifiedItems, terrain)
    log.info("Created %s creatures on map with %s tiles".format(mapSection.allCreatures.size, size * size))
    mapSection
  }

  private def randomTerrain() : Terrain = {
    rnd.nextInt(100) match {
//      case i if i<5 => Wall()
      case i if i<15 => Heap()
      case i if i<45 => Rubble()
      case _ => Glass()
    }
  }

  private def randomPlace() : Option[Place] = {
    rnd.nextInt(100) match {
      case i if i<5 => Some(PowerSocket())
//      case i if i<10 => Some(Terminal())
      case _ => None
    }
  }

  private def randomCreature(): Option[Creature] = {
    rnd.nextInt(100) match {
      case i if i<5 => Some(Robot(randomName()))
//      case i if i<7 => Some(JunkSquid(randomName()))
      case _ => None
    }
  }


  val nameCharacters = "abcaeiouaedefghilmnoprstuvw"
  private def randomName() : String = {
    val name = for (i <- (1 to 4+rnd.nextInt(6))) yield nameCharacters(rnd.nextInt(nameCharacters.size))
    name.mkString("")
  }

}
