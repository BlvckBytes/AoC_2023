package me.blvckbytes.aoc_2023.data

class PipeMapTile(
  val type: PipeMapTileType,
  val position: Position,
) {
  override fun toString(): String {
    return "Tile(${type.char}, $position)"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as PipeMapTile

    if (position != other.position) return false

    return true
  }

  override fun hashCode(): Int {
    return position.hashCode()
  }

  var color: String? = null

  fun copyOf(color: String): PipeMapTile {
    val copy = PipeMapTile(type, position)
    copy.color = color

    return copy
  }
}