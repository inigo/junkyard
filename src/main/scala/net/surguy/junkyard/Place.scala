package net.surguy.junkyard

/** A fixed place. */
sealed abstract class Place  extends Thing

case class PowerSocket() extends Place
case class Terminal() extends Place