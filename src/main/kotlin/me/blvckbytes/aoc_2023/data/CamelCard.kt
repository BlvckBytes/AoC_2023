package me.blvckbytes.aoc_2023.data

class CamelCard(
  val char: Char,
  withJBeingJoker: Boolean
) {

  companion object {
    private val strengthsDescending = arrayOf(
      'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'
    )

    private val strengthsWithJokerDescending = arrayOf(
      'A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J'
    )

    val strengthSize: Int
    val jokerIndex = strengthsWithJokerDescending.indexOf('J')

    init {
      if (strengthsDescending.size != strengthsWithJokerDescending.size)
        throw IllegalStateException("Strength sizes need to be equal")

      strengthSize = strengthsDescending.size

      if (jokerIndex != strengthSize - 1)
        throw IllegalStateException("The joker has to be the weakest card")
    }
  }

  val strength: Int

  init {
    val strengthIndex = (
      if (withJBeingJoker)
        strengthsWithJokerDescending
      else
        strengthsDescending
    ).indexOf(char)

    if (strengthIndex < 0)
      throw IllegalStateException("Unknown card char: $char")

    strength = strengthIndex
  }
}