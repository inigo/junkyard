package net.surguy.junkyard

/**
  * Provides several approaches for pathfinding across a map. The basic approach is the [[net.surguy.junkyard.pathfinder.AstarPathFinder AstarPathFinder]],
  * which uses the standard A* routing algorithm. Two higher-level implementations, [[net.surguy.junkyard.pathfinder.EdgesPathFinder EdgesPathFinder]]
  * and [[net.surguy.junkyard.pathfinder.ZoningPathFinder ZoningPathFinder]], handle path-finding more efficiently for larger maps, by organizing
  * maps into zones using [[zoning]] algorithms, and then first navigating between the high-level zones, and then finding lower-level
  * routes within those zones.
  *
  * Pathfinding is independent of the map implementation (and can potentially be used for things that don't
  * represent geographies, such as tech trees - although it does currently depend on x/y co-ordinates).
  * The [[net.surguy.junkyard.pathfinder.PathFinder PathFinder]] is tied to the map via a [[net.surguy.junkyard.pathfinder.PathRules PathRules]]
  * implementation, such as the [[net.surguy.junkyard.pathfinder.JunkyardPathRules JunkyardPathRules]].
  */
package object pathfinder { }
