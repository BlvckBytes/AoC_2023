package me.blvckbytes.aoc_2023.data

enum class DiagonalDirection(
  val steps: Array<Direction>
) {
  NORTH_EAST(arrayOf(Direction.NORTH, Direction.EAST)),
  EAST_SOUTH(arrayOf(Direction.EAST, Direction.SOUTH)),
  SOUTH_WEST(arrayOf(Direction.SOUTH, Direction.WEST)),
  WEST_NORTH(arrayOf(Direction.WEST, Direction.NORTH)),
}