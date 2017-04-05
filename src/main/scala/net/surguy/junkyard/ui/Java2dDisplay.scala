package net.surguy.junkyard.ui

import java.awt.{Color, Font}
import javax.swing.JFrame

import net.surguy.junkyard.Coord
import net.surguy.junkyard.mapping.MapSection

/**
  * Display a [[net.surguy.junkyard.mapping.MapSection]] on a Java2D graphics display, updating the display with only
  * the parts that have changed from the last MapSection (although this depends on the caller telling the display
  * what has changed - the caller should already know this).
  */
class Java2dDisplay(totalWidth: Int, totalHeight: Int, size: Int) extends Display {
  import DarkenColor._

  val xDimension: Int = totalWidth / size
  val yDimension: Int = totalHeight / size

  val frame = new Frame()
  val d = new ConsoleDisplay(size)
  frame.init()

  class Frame extends JFrame("Junkyard") {
    def init() {
      setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE )
      setVisible( true )
      setSize(totalWidth, totalHeight)
    }
  }

  def dispose() {
    frame.dispose()
  }

  override def display(section: MapSection) { display(section, None) }
  override def display(section: MapSection, changedAreas: Set[Coord]) { display(section, Some(changedAreas)) }

  private def display(section: MapSection, changedAreas: Option[Set[Coord]]) {
    val g = frame.getGraphics
    g.setFont(new Font("Sans-serif", Font.PLAIN, yDimension))

    val toUpdate: Iterable[Coord] = changedAreas.getOrElse( for (x <- 0 until size; y <- 0 until size) yield Coord(x,y) )
    
    for (c <- toUpdate
         if changedAreas.isEmpty || changedAreas.get.contains(c);
         items = section.itemsAt(c);
         terrainChar = d.toDisplayTerrain(section.terrainAt(c));
         currentChar = d.toDisplay(items).getOrElse(terrainChar) ) {
      val top = yDimension + (c.y * yDimension)
      val left = c.x * xDimension
      val cellWidth = xDimension
      val cellHeight = yDimension
      if (section.zones.isDefined) {
        val zone = section.zones.get.zoneAt(c)
        var color = zone.map(_.getColor).getOrElse(new Color(255, 255, 255))
        if (zone.isDefined) {
          if (zone.get.edgeCoords.contains(c)) color = darken(color)
          if (zone.get.filteredEdgeCoords.contains(c)) color = darken(color)
        }
        g.setColor( color )
        g.fillRect(left, top - yDimension, cellWidth, cellHeight)
      } else {
        g.clearRect(left, top - yDimension, cellWidth, cellHeight )
      }
      g.setColor(currentChar.color)
      g.drawString(currentChar.char, left, top)
    }
//    Thread.sleep(300)
  }

}

object DarkenColor {
  def darken(i: Int): Int = if (i>=30) i - 30 else 0
  def darken(color: Color): Color = new Color( darken(color.getRed), darken(color.getGreen), darken(color.getBlue) )
}