package me.blvckbytes.aoc_2023.data

class NavigationNode(
  private val name: String,
  private val left: String,
  private val right: String,
) {
  fun choose(direction: NavigationDirection): String {
    return when (direction) {
      NavigationDirection.LEFT -> left
      NavigationDirection.RIGHT -> right
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as NavigationNode

    if (!name.contentEquals(other.name)) return false

    return true
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }
}