package net.surguy

/**
  * = Junkyard =
  *
  * Pathfinding, mapping, and goal-seeking behaviour, for things in a junkyard.
  *
  * == Things to consider ==
  *
  *  - Add different speeds by having more frames per second, and each creature only acts in a few frames - e.g. in every 100 frames,
  *     a creature with speed 20 acts in 100/20 = 5 of them.
  *  - Work out pathfinding for large creatures using the "true clearance" metric described at
  *     http://harablog.wordpress.com/2009/01/29/clearance-based-pathfinding/ - start with 1 if it is passable, and
  *     expand a square down and to the right to increase the clearance score. A 2x2 creature needs a clearance score of 2, etc.
  *  - AIGPW 3.5 (ARC) uses iterative deepening with a delay to cope with units blocked by transient properties like other
  *     units: reduces flooding when no route
  *  - The speed of robots could change based on how much power they have - where power drops with time, and then
  *     builds up again when you reach a power socket.
  *  - GPG3 3.6 - naive tactical pathfinding adds a "threatened" cost to the score, but this can be further improved by
  *     adding an additional cost the longer consecutive time a route is threatened (so exposure to the enemy 4 times for
  *     5 seconds each is better than exposure once for 20 seconds), and by assuming that an enemy occupies all of the
  *     squares that they are near (to simulate movement, and to encourage taking better advantage of cover).
  *  - GPG4 7.1 - OpenAL is primarily an audio scene graph library, for 3D positional audio.
  *  - GPG3 2.6 - use cellular automata for spreading phenomena like flooding and fire (excellent article, lots of detail)
  *  - Use a blackboard to allow units to co-operate: post tasks to the blackboard, and units retrieve them
  *  - Use religion as a motivation for robots to provide high-level goalseeking?
  *
  * == Performance ==
  *
  *  - Storing the terrain as a simple list of items is hugely inefficient - it's the slowest part of pathfinding
  *  - We could cache routes in the pathfinder (particularly good at that moment when robots mostly go between power
  *     sockets) - but hard to work out what we can best cache. Currently, source/destination tuples in a set would
  *     be effective, because most routes are the same - but this isn't likely to be true for long.
  *  - Caching individual routes within the unit is sensible - the next 10 steps, or something like that. Possibly
  *     restrict it to only steps within the same zone, to avoid caching the long distance steps.
  *  - GPG 3.3 - Use a binary heap, rather than the default priority queue, to store the open list in the
  *     pathfinder - requires fewer comparisons.
  *
  * == Implemented ==
  *  - Use hierarchical A* to improve performance - see
  *     http://harablog.wordpress.com/2009/02/05/hierarchical-clearance-based-pathfinding/. Split the area into larger scale
  *     nodes that have routes between them, and then you only need to find a path to the next node exit. It seems like for
  *     this to work, you'd need to have interconnection between your zones - i.e. if you can get to one part of the zone,
  *     you can get to all of it. But this means that zones could potentially change when you add obstructions within them.
  *     <ul>
  *         <li>I already have an algorithm for flood-fill - it's A* with no heuristic, and no terrain costs. This can be used
  *             for finding contiguous zones (and could be used for goal-seeking too...)</li>
  *         <li>To work out where zones end, some sort of weighted function based on how many open nodes versus how many
  *             closed nodes should find rooms (many closed, few open, means it's a zone - might also want a limit on number
  *             closed just to make sure we don't have everything as a single zone).</li>
  *         <li>A simpler initial algorithm could just count number of closed nodes. Need
  *             to make sure that inaccessible nodes don't make it onto the closed list.</li>
  *         <li>Zones will depend on the start points chosen - a possible optimization is to do the zoning multiple times,
  *             and choose the version that gives the "best" zones.</li>
  *     </ul>
  *  - Improved Heuristics for Optimal Pathfinding on Game Maps (Bjornsson and Halldorsson) - provides an algorithm
  *     for dividing the map into zones.
  *  - BGPG 3.2 - recommends using a hierarchical pathfinder, then using the lowlevel pathfinder to go to the second zone,
  *     not the first, to give a smoother route. (Also has the benefit that it removes a bug I was having)
  *  - BGPG 3.3 - tweaking the heuristic to make it overestimate forces the pathfinder towards straight lines -
  *     which gives a speedup if obstacles can generally be avoided. Going from a simple taxi-cab distsnce, to 3 * that,
  *     improved performance by about 40%. Most correct is if it is close to the average cost.
  *
  * == Rejected ==
  *  - AIGPW 3.2 (EE) uses an initial quick pathfind to start moving,
  *     and then a more expensive full pathfind to get the path correct: their example is from an open landscape, and this seems
  *     like it could go wrong in a confined space when all routes involve going in the opposite direction to start with. Instead
  *     a hierarchical system seems better.
  *  - AIGPW 3.2 (EE) uses a Pathfinder manager,
  *     that uses time slicing to determine how much pathing time each unit gets. Not convinced by this - seems better to
  *     make it faster generally.
  *  - Could we use some sort of ant-pheromone-trail to speed up pathfinding? If a route is commonly found to be the
  *     best route and travelled along, then that might make it lower cost because it's likely to be used by others too?
  *     Or would that lead to congestion? Or would that be unnecessary because if it's being used, it's the best route anyway?
  *
  * == References ==
  *   - AIGPW - AI Game Programming Wisdom (EE - Empire Earth, ARC - Arcanum)
  *   - GPG - Game Programming Gems
  *   - BGPG - Best of Game Programming Gems
  */
package object junkyard {}
