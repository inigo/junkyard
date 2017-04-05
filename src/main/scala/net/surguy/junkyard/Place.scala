package net.surguy.junkyard

abstract class Place  extends Thing
case class PowerSocket() extends Place
case class Terminal() extends Place

abstract class Terrain extends Thing { self : { val difficulty : Int } =>
  def getDifficulty: Int = self.difficulty
}
case class Glass(difficulty: Int = 1) extends Terrain
case class Rubble(difficulty: Int = 2) extends Terrain
case class Heap(difficulty: Int = 4) extends Terrain
case class Wall(difficulty: Int = 1000) extends Terrain
case class WellShaft(difficulty: Int = 4) extends Terrain