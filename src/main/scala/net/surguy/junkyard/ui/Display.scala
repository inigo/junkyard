package net.surguy.junkyard.ui

import net.surguy.junkyard.Coord
import net.surguy.junkyard.mapping.MapSection

trait Display {
  def display(section: MapSection, changedAreas: Set[Coord])
  def display(section: MapSection)
}