package me.blvckbytes.aoc_2023

class AoC2023 {

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
//      day1Puzzle1()
//      day1Puzzle2()
//      day2Puzzle1()
      day2Puzzle2()
    }

    private fun day1Puzzle1() {
      InputFile("day1_1.txt").use {
        var sum = 0

        for (line in it)
          sum += reconstructCalibrationValue(line)

        println("The total sum of all calibration values is: $sum")
      }
    }

    private fun day1Puzzle2() {
      InputFile("day1_1.txt").use {
        var sum = 0

        for (line in it)
          sum += reconstructCalibrationValueWithWords(line)

        println("The total sum of all calibration values is: $sum")
      }
    }

    private fun day2Puzzle1() {
      InputFile("day2_1.txt").use {
        var possibleIndicesSum = 0

        gameLoop@ for (line in it) {
          val colonIndex = line.indexOf(':')
          val gameId = line.substring("Game ".length, colonIndex).toInt()
          val subsets = line.substring(colonIndex + 2).split("; ")

          for (subset in subsets) {
            val cubeColorEntries = subset.split(", ")

            for (cubeColorEntry in cubeColorEntries) {
              val spaceIndex = cubeColorEntry.indexOf(' ')
              val amount = cubeColorEntry.substring(0, spaceIndex).toInt()

              val max = when(val color = cubeColorEntry.substring(spaceIndex + 1)) {
                "red" -> 12
                "green" -> 13
                "blue" -> 14
                else -> throw IllegalStateException("Unknown color: $color")
              }

              if (amount > max)
                continue@gameLoop
            }
          }

          possibleIndicesSum += gameId
        }

        println(possibleIndicesSum)
      }
    }

    private fun day2Puzzle2() {
      InputFile("day2_1.txt").use {
        var setPowerSum = 0

        gameLoop@ for (line in it) {
          val colonIndex = line.indexOf(':')
//        val gameId = line.substring("Game ".length, colonIndex).toInt()
          val subsets = line.substring(colonIndex + 2).split("; ")

          // [red, green, blue]
          val colorMaximums = arrayOf(0, 0, 0)

          for (subset in subsets) {
            val cubeColorEntries = subset.split(", ")

            for (cubeColorEntry in cubeColorEntries) {
              val spaceIndex = cubeColorEntry.indexOf(' ')
              val amount = cubeColorEntry.substring(0, spaceIndex).toInt()

              val maximumsIndex = when (val color = cubeColorEntry.substring(spaceIndex + 1)) {
                "red" -> 0
                "green" -> 1
                "blue" -> 2
                else -> throw IllegalStateException("Unknown color: $color")
              }

              if (colorMaximums[maximumsIndex] < amount)
                colorMaximums[maximumsIndex] = amount
            }
          }

          setPowerSum += colorMaximums[0] * colorMaximums[1] * colorMaximums[2]
        }

        println(setPowerSum)
      }
    }

    private val digitWords = arrayOf(
      "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
    )

    private fun reconstructCalibrationValueWithWords(input: String): Int {
      var firstDigit: Char? = null
      var lastDigit: Char? = null
      var lastDigitIndex: Int? = null

      val inputLength = input.length
      var i = -1

      inputLoop@ while (++i < inputLength) {
        val currentChar = input[i]

        if (currentChar.isDigit()) {
          if (firstDigit == null)
            firstDigit = currentChar

          if (lastDigitIndex == null || lastDigitIndex < i) {
            lastDigit = currentChar
            lastDigitIndex = i
          }

          continue
        }

        wordLoop@ for (digitWordIndex in digitWords.indices) {
          val digitWord = digitWords[digitWordIndex]
          val digitWordLength = digitWord.length

          if (i + digitWordLength > inputLength)
            continue@wordLoop

          for (wordCharIndex in 0 until digitWordLength) {
            val inputChar = input[i + wordCharIndex]
            val digitChar = digitWord[wordCharIndex]

            if (inputChar != digitChar)
              continue@wordLoop
          }

          val wordChar = (digitWordIndex + 1 + '0'.code).toChar()

          if (firstDigit == null)
            firstDigit = wordChar

          if (lastDigitIndex == null || lastDigitIndex < i) {
            lastDigit = wordChar
            lastDigitIndex = i
          }

          // Advance the input loop by this word
          // -1 as the current i is already the first character
          // -1 again as continue will ++i again
          i += digitWordLength - 2
          continue@inputLoop
        }
      }

      if (firstDigit == null || lastDigit == null)
        throw IllegalStateException("The input did not contain digits or words: $input")

      return 10 * (firstDigit.code - '0'.code) + (lastDigit.code - '0'.code)
    }

    private fun reconstructCalibrationValue(input: String): Int {
      var firstDigit: Char? = null
      var lastDigitIndex: Int? = null

      for (i in input.indices) {
        val currentChar = input[i]

        if (currentChar.isDigit()) {
          if (firstDigit == null)
            firstDigit= currentChar

          if (lastDigitIndex == null || lastDigitIndex < i)
            lastDigitIndex = i
        }
      }

      if (firstDigit == null || lastDigitIndex == null)
        throw IllegalStateException("The input did not contain digits: $input")

      return 10 * (firstDigit.code - '0'.code) + (input[lastDigitIndex].code - '0'.code)
    }
  }
}