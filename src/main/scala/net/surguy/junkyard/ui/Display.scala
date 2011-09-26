package net.surguy.junkyard.ui

import net.surguy.junkyard.Coord
import net.surguy.junkyard.mapping.MapSection

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 21, 2010 4:40:34 PM
 */

trait Display {
  def display(section: MapSection, changedAreas: Set[Coord])
  def display(section: MapSection)
}