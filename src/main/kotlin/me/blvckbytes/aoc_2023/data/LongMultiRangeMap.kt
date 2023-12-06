package me.blvckbytes.aoc_2023.data

class LongMultiRangeMap(
  private val mappings: List<LongRangeMapping>
) {

  private var mappingsInOrder: List<LongRangeMapping>? = null
  private var mappingsSize: Int? = null
  private var clearBooleanArray: BooleanArray? = null

  fun lookupValueOrGetKey(key: Long): Long {
    initializeIfApplicable()

    val targetIndex = mappingsInOrder!!.binarySearch {
      if (it.sourceStart > key)
        return@binarySearch 1

      if (it.sourceEnd < key)
        return@binarySearch -1

      return@binarySearch 0
    }

    if (targetIndex < 0 || targetIndex > mappingsSize!!)
      return key

    return mappingsInOrder!![targetIndex].mapValue(key)
      ?: throw IllegalStateException("Found mapping mismatched")
  }

  fun mapRanges(inputRanges: Collection<FirstLastLongRange>): Set<FirstLastLongRange> {
    initializeIfApplicable()

    val result = HashSet<FirstLastLongRange>()
    val remainders = HashSet<FirstLastLongRange>()
    val usedFlagByMappingIndex = BooleanArray(mappingsSize!!) { false }

    for (inputRange in inputRanges) {
      remainders.add(inputRange)

      remainderLoop@ while (remainders.size > 0) {
        val remainder = remainders.first()

        mappingLoop@ for (mappingIndex in mappings.indices) {
          if (usedFlagByMappingIndex[mappingIndex])
            continue@mappingLoop

          result.add(mappings[mappingIndex].intersect(remainder, remainders) ?: continue)

          remainders.remove(remainder)
          usedFlagByMappingIndex[mappingIndex] = true
          continue@remainderLoop
        }

        // Remainder did not find a match. Unmapped values stay the same.
        remainders.remove(remainder)
        result.add(remainder)
      }

      // Current input done, reset used flags
      // Remainders is already empty, due to the remainder loop
      clearBooleanArray!!.copyInto(usedFlagByMappingIndex)
    }

    return result
  }

  private fun initializeIfApplicable() {
    if (mappingsSize != null)
      return

    mappingsSize = mappings.size
    clearBooleanArray = BooleanArray(mappingsSize!!) { false }
    mappingsInOrder = mappings.sortedWith(
      compareBy({ it.sourceStart }, { it.sourceEnd })
    )
  }
}