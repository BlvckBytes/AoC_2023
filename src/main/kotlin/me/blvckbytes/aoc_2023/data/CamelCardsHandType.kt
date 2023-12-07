package me.blvckbytes.aoc_2023.data

enum class CamelCardsHandType(
  private val frequencies: IntArray,
  private val numberOfFrequencies: Int,
) {
  FIVE_OF_A_KIND(5),
  FOUR_OF_A_KIND(4, 1),
  FULL_HOUSE(3, 2),
  THREE_OF_A_KIND(3, 1, 1),
  TWO_PAIR(2, 2, 1),
  ONE_PAIR(2, 1, 1, 1),
  HIGH_CARD(1, 1, 1, 1, 1),
  ;

  constructor(vararg frequencies: Int) : this(frequencies, frequencies.size)

  companion object {
    private val values = values()

    fun findMatching(sortedFrequencies: IntArray): CamelCardsHandType {
      for (value in values) {
        if (value.matches(sortedFrequencies))
          return value
      }

      throw IllegalStateException("Could not find a match for the frequencies $sortedFrequencies")
    }
  }

  fun matches(sortedFrequencies: IntArray): Boolean {
    if (sortedFrequencies.size < numberOfFrequencies)
      return false

    for (frequencyIndex in sortedFrequencies.indices) {
      val frequency = sortedFrequencies[frequencyIndex]

      if (frequencyIndex >= numberOfFrequencies) {
        if (frequency != 0)
          return false

        continue
      }

      if (frequency != frequencies[frequencyIndex])
        return false
    }

    return true
  }
}