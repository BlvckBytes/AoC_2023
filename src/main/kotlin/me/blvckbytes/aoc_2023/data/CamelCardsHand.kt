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

    val occurrences = IntArray(CamelCard.strengthSize) { 0 }

    for (card in cards)
      ++occurrences[card.strength]

    var minOccurrence: Int? = null
    var maxOccurrence: Int? = null

    for (occurrence in occurrences) {
      if (occurrence == 0)
        continue

      if (minOccurrence == null || occurrence < minOccurrence)
        minOccurrence = occurrence

      if (maxOccurrence == null || occurrence > maxOccurrence)
        maxOccurrence = occurrence
    }

    if (minOccurrence == null || maxOccurrence == null)
      throw IllegalStateException("Could not determine min/max occurrences")

    // J cards can pretend to be whatever card is best for the purpose of determining hand type
    val numberOfJokers = (
      if (withJBeingJoker)
        occurrences[CamelCard.jokerIndex]
      else
        0
    )

    // all five cards have the same label
    // XXXXX
    if (maxOccurrence == 5)
      type = CamelCardsHandType.FIVE_OF_A_KIND

    // four cards have the same label and one card has a different label
    // XXXXY
    else if (maxOccurrence == 4) {
      type = when(numberOfJokers) {
        // JJJJY -> YYYYY, XXXXJ -> XXXXX
        4, 1 -> CamelCardsHandType.FIVE_OF_A_KIND
        0 -> CamelCardsHandType.FOUR_OF_A_KIND
        else -> throw IllegalStateException("Illegal number of jokers $numberOfJokers")
      }
    }

    // three cards have the same label, and the remaining two cards share a different label
    // XXXYY
    else if (maxOccurrence == 3 && minOccurrence == 2) {
      type = when (numberOfJokers) {
        // JJJYY -> YYYYY, XXXJJ -> XXXXX
        3, 2 -> CamelCardsHandType.FIVE_OF_A_KIND
        1 -> throw IllegalStateException("Impossible case")
        0 -> CamelCardsHandType.FULL_HOUSE
        else -> throw IllegalStateException("Illegal number of jokers $numberOfJokers")
      }
    }

    // three cards have the same label, and the remaining two cards are each different from any other card in the hand
    // XXXYZ
    else if (maxOccurrence == 3 && minOccurrence == 1) {
      type = when (numberOfJokers) {
        2 -> throw IllegalStateException("Impossible case")
        // JJJYZ -> XXXXZ, XXXJZ -> XXXXZ
        3, 1 -> CamelCardsHandType.FOUR_OF_A_KIND
        0 -> CamelCardsHandType.THREE_OF_A_KIND
        else -> throw IllegalStateException("Illegal number of jokers $numberOfJokers")
      }
    }

    // two cards share one label, two other cards share a second label, and the remaining card has a third label
    // XXYYZ
    else if (occurrences.count { it == 2 } == 2) {
      type = when (numberOfJokers) {
        // XXJJZ -> XXXXZ
        2 -> CamelCardsHandType.FOUR_OF_A_KIND
        // XXYYJ -> XXYYY
        1 -> CamelCardsHandType.FULL_HOUSE
        0 -> CamelCardsHandType.TWO_PAIR
        else -> throw IllegalStateException("Illegal number of jokers $numberOfJokers")
      }
    }

    // two cards share one label, and the other three cards have a different label from the pair and each other
    // XXYZW
    else if (maxOccurrence == 2 && occurrences.count { it == 1 } == 3) {
      type = when(numberOfJokers) {
        // JJYZW -> YYYZW, XXJZW -> XXXZW
        2, 1 -> CamelCardsHandType.THREE_OF_A_KIND
        0 -> CamelCardsHandType.ONE_PAIR
        else -> throw IllegalStateException("Illegal number of jokers $numberOfJokers")
      }
    }

    // all cards' labels are distinct
    // VWXYZ
    else if (maxOccurrence == 1) {
      type = when (numberOfJokers) {
        5, 4, 3, 2 -> throw IllegalStateException("Impossible case")
        // VJXYZ -> VVXYZ
        1 -> CamelCardsHandType.ONE_PAIR
        0 -> CamelCardsHandType.HIGH_CARD
        else -> throw IllegalStateException("Illegal number of jokers $numberOfJokers")
      }
    }

    else
      throw IllegalStateException("Could not decide on a hand type")
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