package me.blvckbytes.aoc_2023.data

class Position(
  val x: Int,
  val y: Int,
) {
  fun move(direction: Direction): Position {
    return Position(x + direction.deltaX, y + direction.deltaY)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Position

    if (x != other.x) return false
    if (y != other.y) return false

    return true
  }

  override fun hashCode(): Int {
    return ((x and 0xFFFF) shl 16) or (y and 0xFFFF)
  }

  override fun toString(): String {
    return "($x, $y)"
  }
}
