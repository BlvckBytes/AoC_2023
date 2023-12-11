package me.blvckbytes.aoc_2023.data

enum class Direction(
  val deltaX: Int,
  val deltaY: Int,
) {

  /*
    y
    |
    +-- x

       N
       |
    W--+--E
       |
       S
   */

  NORTH(0, -1),
  EAST(1, 0),
  SOUTH(0, 1),
  WEST(-1, 0)
  ;

  companion object {
    val values = values()

    private val orthogonalToVertical = setOf(
      WEST, EAST
    )

    private val orthogonalToHorizontal = setOf(
      NORTH, SOUTH
    )
  }

  fun opposite(): Direction {
    return when (this) {
      NORTH -> SOUTH
      EAST -> WEST
      SOUTH -> NORTH
      WEST -> EAST
    }
  }

  fun orthogonal(): Set<Direction> {
    return when(this) {
      NORTH, SOUTH -> orthogonalToVertical
      EAST, WEST -> orthogonalToHorizontal
    }
  }
}