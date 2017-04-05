package net.surguy.junkyard

import goals.PowerUser
import mapping.MapSection
import util.Random
import utils.Logging

sealed abstract class Creature extends Thing with Logging {
  def goal(current: Coord, section: MapSection) : Coord = Coord(1,1)
}
case class Robot(name: String) extends Creature with PowerUser {
  private var target: Option[Coord] = None
  override def goal(current: Coord, section: MapSection): Coord = {
    if (target.isEmpty || section.itemsAt(current).exists(_.isInstanceOf[PowerSocket])) {
      target = Some( Random.shuffle(section.allItems.filter(_.thing.isInstanceOf[PowerSocket])).head.coord )
      log.debug("Choosing new target - "+target)
    }
    log.debug("Retrieving current target of "+target)
    target.get
//    val target = section.nearbyItems(current).find(_.thing.isInstanceOf[PowerSocket])
//    log.debug(toString+" is heading towards "+target)
//    if (target.isDefined) target.get.coord else Coord(1,1)
  }
}

case class JunkSquid(name: String) extends Creature {
  override def goal(current: Coord, section: MapSection): Coord =
    section.nearbyItems(current).find(_.thing.isInstanceOf[Robot]).map(_.coord).getOrElse(Coord(1,1))
}

case class Robodog(name: String) extends Creature
