package net.surguy.junkyard.ui

import net.surguy.junkyard._
import java.awt.Color
import mapping.MapSection

class ConsoleDisplay(size: Int) extends Display {

  def display(section: MapSection) = display(section, Set())
  def display(section: MapSection, changedAreas: Set[Coord]) {
    val chars = for (x <- 0 until size;
                     y <- 0 until size;
                     items = section.itemsAt(Coord(x, y));
                     terrainChar = toDisplayTerrain(section.terrainAt(Coord(x,y)));
                     currentChar = toDisplay(items)
        ) yield currentChar.getOrElse(terrainChar)

    chars.grouped(size).foreach( row => println(row.map(_.char).mkString) )
    println(" ")
    println(" ")
  }

  private[ui] def toDisplay(things: List[Thing]): Option[Glyph] = toDisplayRobot(things).orElse(
                                        toDisplayPlace(things).orElse(None))

  private[ui] def toDisplayTerrain(t: Terrain): Glyph = t match {
    case r: Glass => Glyph(".",new Color(90,90,90))
    case r: Heap => Glyph("^",new Color(90,90,90))
    case r: Rubble => Glyph("~",new Color(90,90,90))
    case r: Wall => Glyph("x",new Color(90,90,90))
  }

  private[ui] def toDisplayRobot(things: List[Thing]) : Option[Glyph] =
    things.collect { case x: Creature => x }.headOption match {
      case Some(r: Robot) => Some(Glyph("R",new Color(100,0,0)))
      case Some(r: JunkSquid) => Some(Glyph("S",new Color(100,0,0)))
      case _ => None
    }

  private[ui] def toDisplayPlace(things: List[Thing]) : Option[Glyph] =
    things.collect { case x: Place => x }.headOption match {
      case Some(r: PowerSocket) => Some(Glyph("P",new Color(0,0,200)))
      case Some(r: Terminal) => Some(Glyph("T",new Color(0,0,200)))
      case _ => None
    }


}


case class Glyph(char: String, color: Color)
