package me.blvckbytes.aoc_2023.data

data class NumberAndIndex(
  val number: Int,
  val lineNumber: Int,
  val firstCharIndex: Int,
  val lastCharIndex: Int,
) {
  override fun hashCode(): Int {
    if (lineNumber > 255 || firstCharIndex > 255 || lastCharIndex > 255)
      throw IllegalStateException()
    return lineNumber + (firstCharIndex shl 8) or (lastCharIndex shl 16)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as NumberAndIndex

    if (lineNumber != other.lineNumber) return false
    if (firstCharIndex != other.firstCharIndex) return false
    if (lastCharIndex != other.lastCharIndex) return false

    return true
  }
}