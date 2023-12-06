package me.blvckbytes.aoc_2023.data

import kotlin.math.max
import kotlin.math.min

class LongRangeMapping(
  val sourceStart: Long,
  val sourceEnd: Long,
  private val destinationDelta: Long
) {
  fun mapValue(value: Long): Long? {
    if (value in sourceStart..sourceEnd)
      return value + destinationDelta
    return null
  }

  fun intersect(range: FirstLastLongRange, remainders: MutableSet<FirstLastLongRange>): FirstLastLongRange? {
    val intersectionBegin = max(sourceStart, range.first)
    val intersectionEnd = min(sourceEnd, range.last)

    if (intersectionBegin > intersectionEnd)
      return null

    val intersection = FirstLastLongRange(
      intersectionBegin + destinationDelta,
      intersectionEnd + destinationDelta
    )

    if (intersectionBegin > range.first)
      remainders.add(FirstLastLongRange(range.first, intersectionBegin - 1))

    if (intersectionEnd < range.last)
      remainders.add(FirstLastLongRange(intersectionEnd + 1, range.last))

    return intersection
  }
}