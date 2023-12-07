package me.blvckbytes.aoc_2023.data

class CamelCardsHand(
  private val cards: List<CamelCard>,
  val bidAmount: Long,
  withJBeingJoker: Boolean
) : Comparable<CamelCardsHand> {

  private val type: CamelCardsHandType

  init {
    if (cards.size != 5)
      throw IllegalStateException("A hand has to consist of five cards exactly")

    val frequencies = IntArray(CamelCard.strengthSize) { 0 }

    for (card in cards)
      ++frequencies[card.strength]

    var numberOfJokers = 0

    if (withJBeingJoker) {
      numberOfJokers = frequencies[CamelCard.jokerIndex]
      frequencies[CamelCard.jokerIndex] = 0
    }

    frequencies.sortDescending()

    if (withJBeingJoker)
      frequencies[0] += numberOfJokers

    type = CamelCardsHandType.findMatching(frequencies)
  }

  override fun compareTo(other: CamelCardsHand): Int {
    val thisTypeOrdinal = type.ordinal
    val otherTypeOrdinal = other.type.ordinal

    // Type ASC

    if (thisTypeOrdinal > otherTypeOrdinal)
      return -1

    if (thisTypeOrdinal < otherTypeOrdinal)
      return 1

    if (this.cards.size != other.cards.size)
      throw IllegalStateException("Hands should have the same size")

    for (cardIndex in this.cards.indices) {
      val thisCardStrength = this.cards[cardIndex].strength
      val otherCardStrength = other.cards[cardIndex].strength

      // Strength ASC

      if (thisCardStrength == otherCardStrength)
        continue

      if (thisCardStrength > otherCardStrength)
        return -1

      return 1
    }

    throw IllegalStateException("Encountered two completely identical hands")
  }

  override fun toString(): String {
    return "(${cards.map { it.char }.joinToString("")}, $type, $bidAmount)"
  }
}