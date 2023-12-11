package me.blvckbytes.aoc_2023.data

class PipeMap(
  private val map: Array<Array<PipeMapTile>>,
  private val start: PipeMapTile
) {
  /*
    What a mess! I totally went down a rabbit-hole on this one, and didn't even make
    the second part of the puzzle work out for the real case.

    Gonna start over, once I feel better about it.
   */

  fun walkMainLoop(): Set<PipeMapTile> {
    return buildSet {
      add(start)

      var currentTile = start
      var lastDirection: Direction? = null

      walkerLoop@ while (true) {
        val choices = findConnections(currentTile)

        for ((choice, direction) in choices) {
          // Don't walk backwards, don't go back on the start
          if (direction.opposite() == lastDirection)
            continue

          if (choice == start)
            break@walkerLoop

          add(choice)

          currentTile = choice
          lastDirection = direction
          continue@walkerLoop
        }

        throw IllegalStateException("Did not loop back to the starting point")
      }
    }
  }

  fun findEnclosedTiles(loop: Set<PipeMapTile>): Set<PipeMapTile> {
    return buildSet {
      for (y in map.indices) {
        for (tile in map[y]) {
          if (loop.contains(tile))
            continue

          val successfulMoves = HashSet<PipeMapTile>()

          // Try to walk out of bounds, starting from each tile
          // If that's not possible, it's fully blocked off by the loop
          if (!hasPathToBounds(tile, loop, HashSet(), successfulMoves)) {
            add(tile)
          }

          printMoves(tile, successfulMoves, loop)
        }
      }
    }
  }

  companion object {
    private const val ANSI_RESET = "\u001B[0m"
    private const val ANSI_RED   = "\u001B[31m"
    private const val ANSI_GREEN = "\u001B[32m"
    private const val ANSI_YELLOW = "\u001B[33m"
    private const val ANSI_BLUE = "\u001B[34m"
    private const val ANSI_PURPLE = "\u001B[35m"
    private const val ANSI_CYAN = "\u001B[36m"
  }

  private fun printMoves(start: PipeMapTile, moves: HashSet<PipeMapTile>, loop: Set<PipeMapTile>) {
    val movesList = moves.toList()

    println("$start, ${moves.size} moves:")
    println(toString { currentTile, result ->
      if (currentTile == start) {
        result.append(ANSI_RED)
        result.append(currentTile.type)
        result.append(ANSI_RESET)
        return@toString true
      }

      val index = moves.indexOf(currentTile)

      if (index >= 0) {
        result.append(movesList.getOrNull(index)?.color ?: "")
        result.append(currentTile.type)
        result.append(ANSI_RESET)
        return@toString true
      }

      if (!loop.contains(currentTile)) {
        result.append('O')
        return@toString true
      }

      return@toString false
    })
    println()
  }

  private fun findConnections(tile: PipeMapTile): List<Pair<PipeMapTile, Direction>> {
    val connections = mutableListOf<Pair<PipeMapTile, Direction>>()

    for (connectionDirection in tile.type.connectionDirections) {
      val connectedTile = tileAt(tile.position.move(connectionDirection))

      if (connectedTile == null || connectedTile.type == PipeMapTileType.GROUND)
        continue

      if (!connectedTile.type.connectionDirections.contains(connectionDirection.opposite()))
        continue

      connections.add(Pair(connectedTile, connectionDirection))
    }

    return connections
  }

  private fun tileAt(position: Position): PipeMapTile? {
    // (0, 0) is at top left, x corresponds to columns, y to rows
    if (position.y < 0 || position.y >= map.size)
      return null

    val row = map[position.y]

    if (position.x < 0 || position.x >= row.size)
      return null

    return row[position.x]
  }

//  private fun isPartOfExit(tile: PipeMapTile, movingDirection: Direction): Boolean {
//    val eastToNextType = tileAt(tile.position.move(Direction.EAST))?.type
//    val westToNextType = tileAt(tile.position.move(Direction.WEST))?.type
//    val northToNextType = tileAt(tile.position.move(Direction.NORTH))?.type
//    val southToNextType = tileAt(tile.position.move(Direction.SOUTH))?.type
//
//    return when (movingDirection) {
//      Direction.NORTH, Direction.SOUTH -> {
//        when (tile.type) {
//          PipeMapTileType.BEND_SW -> eastToNextType == PipeMapTileType.BEND_SE
//          PipeMapTileType.BEND_SE -> westToNextType == PipeMapTileType.BEND_SW
//          PipeMapTileType.BEND_NW -> eastToNextType == PipeMapTileType.BEND_NE
//          PipeMapTileType.BEND_NE -> westToNextType == PipeMapTileType.BEND_NW
//          else -> false
//        }
//      }
//      Direction.EAST, Direction.WEST -> {
//        when (tile.type) {
//          PipeMapTileType.BEND_NW -> southToNextType == PipeMapTileType.BEND_SW
//          PipeMapTileType.BEND_SW -> northToNextType == PipeMapTileType.BEND_NW
//          PipeMapTileType.BEND_NE -> southToNextType == PipeMapTileType.BEND_SE
//          PipeMapTileType.BEND_SE -> northToNextType == PipeMapTileType.BEND_NE
//          else -> false
//        }
//      }
//    }
//  }

  private fun isWalkablePipe(movingDirection: Direction, tile: PipeMapTile): Boolean {
    when (movingDirection) {
      Direction.NORTH, Direction.SOUTH -> {
        val eastToNextType = tileAt(tile.position.move(Direction.EAST))?.type
        val westToNextType = tileAt(tile.position.move(Direction.WEST))?.type

        return when (tile.type) {
          PipeMapTileType.BEND_NW -> eastToNextType == PipeMapTileType.BEND_NE || eastToNextType == PipeMapTileType.VERTICAL_PIPE
          PipeMapTileType.BEND_NE -> westToNextType == PipeMapTileType.BEND_NW || westToNextType == PipeMapTileType.VERTICAL_PIPE
          PipeMapTileType.BEND_SW -> eastToNextType == PipeMapTileType.BEND_SE || eastToNextType == PipeMapTileType.VERTICAL_PIPE
          PipeMapTileType.BEND_SE -> westToNextType == PipeMapTileType.BEND_SW || westToNextType == PipeMapTileType.VERTICAL_PIPE
          else -> false
        }
      }
      Direction.EAST, Direction.WEST -> {
        val northToNextType = tileAt(tile.position.move(Direction.NORTH))?.type
        val southToNextType = tileAt(tile.position.move(Direction.SOUTH))?.type

        return when (tile.type) {
          PipeMapTileType.BEND_NE -> southToNextType == PipeMapTileType.BEND_SE || southToNextType == PipeMapTileType.HORIZONTAL_PIPE
          PipeMapTileType.BEND_SE -> northToNextType == PipeMapTileType.BEND_NE || southToNextType == PipeMapTileType.HORIZONTAL_PIPE
          PipeMapTileType.BEND_NW -> southToNextType == PipeMapTileType.BEND_SW || southToNextType == PipeMapTileType.HORIZONTAL_PIPE
          PipeMapTileType.BEND_SW -> northToNextType == PipeMapTileType.BEND_NW || northToNextType == PipeMapTileType.HORIZONTAL_PIPE
          else -> false
        }
      }
    }
  }

  private fun isBetweenParallelPipes(tile: PipeMapTile, movingDirection: Direction): Boolean {
    when (movingDirection) {
      Direction.NORTH, Direction.SOUTH -> {
        if (tile.type != PipeMapTileType.VERTICAL_PIPE)
          return false
      }
      Direction.EAST, Direction.WEST -> {
        if (tile.type != PipeMapTileType.HORIZONTAL_PIPE)
          return false
      }
    }

    val left = when(movingDirection) {
      Direction.NORTH -> Direction.WEST
      Direction.EAST -> Direction.NORTH
      Direction.SOUTH -> Direction.EAST
      Direction.WEST -> Direction.SOUTH
    }

    return tileAt(tile.position.move(left))?.type == tile.type
  }

  private fun tileAt(from: PipeMapTile, steps: Array<Direction>): PipeMapTile? {
    var currentTile = from

    for (step in steps)
      currentTile = tileAt(currentTile.position.move(step)) ?: return null

    return currentTile
  }

  private fun isBend(tile: PipeMapTile): Boolean {
    val steps = when(tile.type) {
      PipeMapTileType.BEND_SE -> arrayOf(Direction.NORTH, Direction.WEST)
      PipeMapTileType.BEND_SW -> arrayOf(Direction.NORTH, Direction.EAST)
      PipeMapTileType.BEND_NE -> arrayOf(Direction.SOUTH, Direction.WEST)
      PipeMapTileType.BEND_NW -> arrayOf(Direction.SOUTH, Direction.EAST)
      else -> return false
    }

    val tileAfterSteps = tileAt(tile, steps)?.type

    // Start is a wildcard
    return tileAfterSteps == tile.type || tileAfterSteps == PipeMapTileType.START
  }

  private fun canMoveToTile(
    previousTile: PipeMapTile,
    nextTile: PipeMapTile,
    movingDirection: Direction,
    loop: Set<PipeMapTile>
  ): String? { // null -> false, "<move color>" -> true
    // Coming from "not on the loop"
    if (!loop.contains(previousTile)) {
      // Trying to go "on the loop"
      if (loop.contains(nextTile)) {
        return (
          // No parallels, only entrances and exits
          if (isWalkablePipe(movingDirection, nextTile))
            ANSI_YELLOW
          else
            null
          )
      }

      // Going to "not on the loop", allow
      return ANSI_BLUE
    }

    // Coming from "on the loop"
    val isPreviousEntranceExit = isWalkablePipe(movingDirection, previousTile)

    // Trying to leave the loop
    if (!loop.contains(nextTile)) {
      // Has used an exit
      if (isPreviousEntranceExit)
        return ANSI_YELLOW

      // Has to stay on the loop
      return null
    }

    // Staying "on the loop"

    val isPreviousParallel = isBetweenParallelPipes(previousTile, movingDirection)
    val isNextParallel = isBetweenParallelPipes(nextTile, movingDirection)

    // Following a bend
    if (isBend(nextTile) || isWalkablePipe(movingDirection, nextTile)) {
      return ANSI_PURPLE
    }

    if (isPreviousEntranceExit && isNextParallel) {
      return ANSI_GREEN
    }

    if (isBend(previousTile) && isNextParallel) {
      return ANSI_GREEN
    }

    if (isPreviousParallel && isNextParallel) {
      return ANSI_GREEN
    }

    return null
  }

  private fun canMoveDiagonallyThroughBends(
    loop: Set<PipeMapTile>,
    previousTile: PipeMapTile,
    nextTile: PipeMapTile,
    diagonal: DiagonalDirection
  ): Boolean {
    if (loop.contains(previousTile) || loop.contains(nextTile))
      return false

    val eastToPreviousType = tileAt(previousTile.position.move(Direction.EAST))?.type
    val westToPreviousType = tileAt(previousTile.position.move(Direction.WEST))?.type
    val northToPreviousType = tileAt(previousTile.position.move(Direction.NORTH))?.type
    val southToPreviousType = tileAt(previousTile.position.move(Direction.SOUTH))?.type

    return when (diagonal) {
      DiagonalDirection.NORTH_EAST -> {
        northToPreviousType == PipeMapTileType.BEND_NW && eastToPreviousType == PipeMapTileType.BEND_SE
      }
      DiagonalDirection.EAST_SOUTH -> {
        eastToPreviousType == PipeMapTileType.BEND_NE && southToPreviousType == PipeMapTileType.BEND_SW
      }
      DiagonalDirection.SOUTH_WEST -> {
        southToPreviousType == PipeMapTileType.BEND_SE && westToPreviousType == PipeMapTileType.BEND_NW
      }
      DiagonalDirection.WEST_NORTH -> {
        westToPreviousType == PipeMapTileType.BEND_SW && northToPreviousType == PipeMapTileType.BEND_NE
      }
    }
  }

  private fun hasPathToBounds(
    tile: PipeMapTile,
    loop: Set<PipeMapTile>,
    seenTiles: HashSet<PipeMapTile>,
    successfulMoves: HashSet<PipeMapTile>,
  ): Boolean {
    for (movingDirection in Direction.values) {
      val nextTile = tileAt(tile.position.move(movingDirection)) ?: return true

      if (!seenTiles.add(nextTile))
        continue

      val moveColor = canMoveToTile(tile, nextTile, movingDirection, loop) ?: continue

      successfulMoves.add(nextTile.copyOf(moveColor))

      if (hasPathToBounds(nextTile, loop, seenTiles, successfulMoves))
        return true
    }

    for (diagonal in DiagonalDirection.values()) {
      val nextTile = tileAt(tile, diagonal.steps) ?: continue

      if (!canMoveDiagonallyThroughBends(loop, tile, nextTile, diagonal))
        continue

      if (!seenTiles.add(nextTile))
        continue

      successfulMoves.add(nextTile.copyOf(ANSI_CYAN))

      // NOTE: (6, 6) Why did F not have to go around?
      // Because touching the bounds returns early, even if that's not a valid move

      if (hasPathToBounds(nextTile, loop, seenTiles, successfulMoves))
        return true
    }

    return false
  }

  override fun toString(): String {
    return toString(null)
  }

  fun toString(override: ((tile: PipeMapTile, builder: StringBuilder) -> Boolean)?): String {
    val result = StringBuilder()

    for (y in map.indices) {
      if (y != 0)
        result.append('\n')

      tileLoop@ for (tile in map[y]) {
        if (override != null) {
          if (override(tile, result))
            continue@tileLoop
        }

        result.append(tile.type)
      }
    }

    return result.toString()
  }
}