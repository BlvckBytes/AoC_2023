package me.blvckbytes.aoc_2023.data

enum class PipeMapTileType(
  val char: Char,
  val connectionDirections: Set<Direction>,
) {
  VERTICAL_PIPE('|', Direction.NORTH, Direction.SOUTH),
  HORIZONTAL_PIPE('-', Direction.WEST, Direction.EAST),
  BEND_NE('L', Direction.NORTH, Direction.EAST),
  BEND_NW('J', Direction.NORTH, Direction.WEST),
  BEND_SW('7', Direction.SOUTH, Direction.WEST),
  BEND_SE('F', Direction.SOUTH, Direction.EAST),
  START('S', Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST),
  GROUND('.'),
  ;

  constructor(char: Char, vararg connectionDirections: Direction): this(char, connectionDirections.toSet())

  companion object {
    private val tileByChar: Map<Char, PipeMapTileType> = values().associateBy { it.char }

    fun fromChar(char: Char): PipeMapTileType {
      return tileByChar[char]
        ?: throw IllegalStateException("The char $char does not represent a known tile")
    }
  }

  override fun toString(): String {
    return "$char"
  }
}