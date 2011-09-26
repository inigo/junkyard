package net.surguy.junkyard.goals

import org.specs.SpecificationWithJUnit
import net.surguy.junkyard.mapping._
import net.surguy.junkyard.{JunkSquid, PlacedThing, Robot}

/**
 * 
 *
 * @author Inigo Surguy
 * @created Apr 11, 2010 3:14:36 PM
 */

class RulesTest extends SpecificationWithJUnit {

  class MyRobot extends Robot("myrobot") with Welder
  val welder = new MyRobot().at(3,3)

  val items = List( welder, new JunkSquid("squiddy").at(3,4) )
  val map = new MapGenerator().createMap(5, items)


  "identifying applicable rules" should {
    "inherit rules from all traits" in {
      welder.thing.asInstanceOf[MyRobot].rules must haveSize(3)
    }
    "give a sensible result" in {
      RulesMatcher.applicableRules(welder, map) must haveSize(1)
    }
  }

}
