package net.surguy.junkyard.goals

import net.surguy.junkyard.mapping._
import net.surguy.junkyard.{JunkSquid, Robot}
import org.specs2.mutable.Specification

class RulesTest extends Specification {

  class MyRobot extends Robot("myrobot") with Welder
  private val welder = new MyRobot().at(3,3)

  private val items = List( welder, JunkSquid("squiddy").at(3,4) )
  private val map = new MapGenerator().createMap(5, items)

  "identifying applicable rules" should {
    "inherit rules from all traits" in {
      welder.thing.asInstanceOf[MyRobot].rules must haveSize(3)
    }
    "give a sensible result" in {
      RulesMatcher.applicableRules(welder, map) must haveSize(1)
    }
  }

}
