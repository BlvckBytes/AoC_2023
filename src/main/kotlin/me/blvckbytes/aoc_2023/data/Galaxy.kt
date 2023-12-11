package me.blvckbytes.aoc_2023.data

import kotlin.math.abs

class Galaxy(
  val rowIndex: Int,
  val columnIndex: Int,
) {
  fun distanceTo(other: Galaxy): Int {
    return abs(other.rowIndex - rowIndex) + abs(other.columnIndex - columnIndex)
  }
}