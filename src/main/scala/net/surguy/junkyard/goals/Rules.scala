package net.surguy.junkyard.goals

import net.surguy.junkyard.mapping.MapSection
import net.surguy.junkyard._
import scala.reflect.Manifest

/**
 *
 *
 * @author Inigo Surguy
 * @created Apr 9, 2010 3:08:36 PM
 */

object RulesMatcher {
  def applicableRules(placedThing: PlacedThing, mapSection: MapSection): List[Rule] = {
    val state = new State(placedThing, mapSection)
    placedThing.thing.asInstanceOf[Robot].rules.filter(r => r.test(state))
  }
}

class Rules(rules: List[Rule])

class Rule(val name: String, conditions: List[Condition], action: Action) {
  override def toString = name
  def test(state: State): Boolean = {
    var matches = null
    for (c <- conditions) {
      val result = c.isTrue(state)
      if (!result) return false
    }
    true
  }

  class Matches {

  }

}

case class MatchResult(success: Boolean, matches: List[Object])

abstract class Action {
  def act(): Unit
}
abstract class Condition {
  def isTrue(state: State): Boolean
}
class State(val creature: PlacedThing, val mapSection: MapSection)

case class At(place: Place) extends Condition {
  def isTrue(state: State) = state.mapSection.itemsAt(state.creature.coord).contains(place)
}
case class On[T <: Terrain](implicit m: Manifest[T]) extends Condition {
  def isTrue(state: State) = isRightClass( state.mapSection.terrainAt(state.creature.coord) )
  private def isRightClass(o: Object) = m >:> Manifest.classType(o.getClass)
}
case class Always() extends Condition {
  def isTrue(state: State) = true
}

case class Has[T](implicit m: Manifest[T]) extends Condition {
  def isTrue(state: State): Boolean = isRightClass(state.creature)
  private def isRightClass(o: Object) = m >:> Manifest.classType(o.getClass)
}

case class NextTo[T <: Creature](implicit m: Manifest[T]) extends Condition {
  def isTrue(state: State) = state.mapSection.nearbyItems(state.creature.coord).find( item => isRightClass(item.thing) ).isDefined
  private def isRightClass(o: Object) = m >:> Manifest.classType(o.getClass)
}

case class Create[T](implicit m: Manifest[T]) extends Action {
  def act() = null 
}

/**
 * Syntactic sugar to change:
 *  new Rule("Recharge", new At(PowerSocket()), new Charge() )
 * into
 *  "Recharge" when At(PowerSocket()) can Charge()
 */
object BuildRules {
  // Replace with mechanism using types? See Church Numerals example?
  implicit def BuildRule(name: String) = new RulesBuilder(name)
  class RulesBuilder(name: String) {
    def when(con: Condition) = new RulesBuilder2(name, List(con))
  }
  class RulesBuilder2(val name: String, val conditions: List[Condition]) {
    def can(action: Action) = new Rule(name, conditions, action)
    def and(con: Condition) = new RulesBuilder2(name, con :: conditions)
  }
}

import BuildRules._
import RulesHelpers._

object RulesHelpers {
  def matched[T]() = null
}

trait RuleProvider {
  def rules = List[Rule]()
}


trait PowerUser extends RuleProvider {
  var maxPower: Int = 100
  var power: Int = 100

  override def rules = ("Recharge" when At(PowerSocket()) can Charge()) :: super.rules

  case class Charge() extends Action { def act() = power = maxPower}
}

trait EarthMover extends RuleProvider {
  override def rules = ("Flatten ground" when On[Heap] can ConvertTerrainTo(Rubble())) ::
                       ("Heap up rubble" when On[Rubble] can ConvertTerrainTo(Heap())) :: super.rules

  case class ConvertTerrainTo(newTerrain: Terrain) extends Action { def act() = null }
}

trait Groomer {
  val rules = List(
    "Remove rust" when NextTo[Robot] can RemoveRust(matched[Robot]),
    "Oil" when NextTo[Robot] and Has[Oil] can ApplyOil(matched[Robot])
  )
  case class RemoveRust(r: Robot) extends Action { def act() = null }
  case class ApplyOil(r: Robot) extends Action { def act() = null }
}

trait Builder {
  val rules = List(
    "Build robodog" when Has[Metal] can Create[Robodog]
  )
}

trait Musician {
  val rules = List(
    "Make drum" when Has[Metal] can Create[Drum],
    "Play music" when Has[Drum] can PlayMusic()
  )
  case class PlayMusic() extends Action { def act() = null }
}

trait Refiner {
  val rules = List(
    "Refine crude oil" when Has[CrudeOil] can Create[Oil]
  )
}

trait Astronomer {
  val rules = List(
    "Know route" when Always() can KnowLocation()
  )
  case class KnowLocation() extends Action { def act() = null }
}

trait Welder extends RuleProvider {

  override def rules = ("Milk squid" when NextTo[JunkSquid] can Create[Oil]) ::
                       ("Repair robot" when NextTo[Robot] and HasCondition(matched[Robot], isInjured) can Repair(matched[Robot])) ::
                       super.rules

  def isInjured(c: Creature) = false

  case class HasCondition[T](thing: T, fn: T => Boolean) extends Condition {
    def isTrue(state: State): Boolean = fn(thing)
  }

  case class Repair(newTerrain: Terrain) extends Action { def act() = null }
}


trait JunkDealer { }
trait ProjectManager { }

trait Driller {
  val rules = List(
    "Dig oil well" when On[Glass] can ConvertTerrainTo(WellShaft())
  )
  case class ConvertTerrainTo(newTerrain: Terrain) extends Action { def act() = null }
}



