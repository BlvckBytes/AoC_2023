package me.blvckbytes.aoc_2023.data

class LongRangesIterator(
  private val ranges: List<FirstLengthLongRange>
) : Iterator<Long> {

  init {
    if (ranges.isEmpty())
      throw IllegalStateException("Please provide at least one range")
  }

  private var rangeIndex = 0
  private var currentRange = ranges[0]
  private var currentRangeOffset = 0L

  private fun tryGotoNextRange(): Boolean {
    if (rangeIndex + 1 >= ranges.size)
      return false

    currentRange = ranges[++rangeIndex]

    // Sometimes, you just need a progress indicator.
    println("completed range ${rangeIndex}/${ranges.size}")

    currentRangeOffset = 0
    return true
  }

  private fun getNextElement(peek: Boolean): Long? {
    if (currentRangeOffset >= currentRange.length) {
      if (!tryGotoNextRange())
        return null
    }

    val value = currentRange.first + currentRangeOffset

    if (!peek)
      ++currentRangeOffset

    return value
  }

  override fun hasNext(): Boolean {
    return getNextElement(true) != null
  }

  override fun next(): Long {
    return getNextElement(false)
      ?: throw IllegalStateException("End reached")
  }
}