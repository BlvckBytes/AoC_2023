package me.blvckbytes.aoc_2023.data

class LongMultiRangeMap(
  private val mappings: List<LongRangeMapping>
) {

  private var mappingsInOrder: List<LongRangeMapping>? = null
  private var mappingsSize: Int? = null

  fun lookupValueOrGetKey(key: Long): Long {
    if (mappingsInOrder == null) {
      mappingsInOrder = mappings.sortedWith(
        compareBy({ it.sourceStart }, { it.length })
      )
      mappingsSize = mappingsInOrder!!.size
    }

    val targetIndex = mappingsInOrder!!.binarySearch {
      if (it.sourceStart > key)
        return@binarySearch 1

      if (it.sourceStart + it.length <= key)
        return@binarySearch -1

      return@binarySearch 0
    }

    if (targetIndex < 0 || targetIndex > mappingsSize!!)
      return key

    val mapping = mappingsInOrder!![targetIndex]

    if (key >= mapping.sourceStart && key < mapping.sourceStart + mapping.length)
      return (key - mapping.sourceStart) + mapping.destinationStart

    throw IllegalStateException("Found mapping mismatched")
  }
}