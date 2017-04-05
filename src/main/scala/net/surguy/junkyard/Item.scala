package net.surguy.junkyard

/** A movable item, which has a position when on the ground, but can be carried. */
sealed abstract class Item

case class Oil() extends Item
case class CrudeOil() extends Item
case class Metal() extends Item
case class Drum() extends Item