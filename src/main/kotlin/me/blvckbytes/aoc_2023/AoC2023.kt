package me.blvckbytes.aoc_2023

import me.blvckbytes.aoc_2023.data.*
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

class AoC2023 {

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      timeExecution("main() took") {
//        day1Puzzle1()
//        day1Puzzle2()
//        day2Puzzle1()
//        day2Puzzle2()
//        day3Puzzle1()
//        day3Puzzle2()
//        day4Puzzle1()
//        day4Puzzle2()
//        day5Puzzle1()
//        day5Puzzle2()
//        day6Puzzle1()
//        day6Puzzle2()
//        day7Puzzle1()
//        day7Puzzle2()
//        day8Puzzle1()
//        day8Puzzle2()
//        day9Puzzle1()
//        day9Puzzle2()
//        day10Puzzle1()
//        day10Puzzle2()
//        day11Puzzle1()
        day11Puzzle2()
      }
    }

    private inline fun timeExecution(prefix: String, executor: () -> Unit) {
      val start = System.nanoTime()
      executor()
      val end = System.nanoTime()

      println("$prefix ${round((end - start) / 1000.0 / 1000.0 * 100) / 100}ms")
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

              val max = when (val color = cubeColorEntry.substring(spaceIndex + 1)) {
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

    private fun parseSchematicLines(file: String): List<SchematicLine> {
      return InputFile(file).use {
        val schematicLines = mutableListOf<SchematicLine>()
        var lineNumberCounter = 0

        for (line in it) {
          val lineNumber = lineNumberCounter++
          val lineChars = line.toCharArray()
          val lineNumbers = mutableListOf<NumberAndIndex>()

          schematicLines.add(SchematicLine(lineChars, lineNumbers))

          var firstNumberIndex: Int? = null

          for (charIndex in lineChars.indices) {
            val currentChar = lineChars[charIndex]
            val isDigit = currentChar.isDigit()

            if (isDigit) {
              if (firstNumberIndex == null)
                firstNumberIndex = charIndex

              if (charIndex != lineChars.size - 1)
                continue
            }

            if (firstNumberIndex != null) {
              val endIndex = if (isDigit) charIndex else charIndex - 1
              val number = numberFromCharArray(lineChars.sliceArray(firstNumberIndex..endIndex))
              lineNumbers.add(NumberAndIndex(number, lineNumber, firstNumberIndex, endIndex))
              firstNumberIndex = null
            }
          }
        }

        schematicLines
      }
    }

    private fun findPartNumbers(schematicLines: List<SchematicLine>, schematicLineIndex: Int): List<NumberAndIndex> {
      val currentSchematicLine = schematicLines[schematicLineIndex]
      val previousSchematicLine = schematicLines.getOrNull(schematicLineIndex - 1)
      val nextSchematicLine = schematicLines.getOrNull(schematicLineIndex + 1)

      val partNumbers = mutableListOf<NumberAndIndex>()

      lineNumberLoop@ for (number in currentSchematicLine.numbers) {

        // Left on same line
        if (number.firstCharIndex != 0) {
          val leftChar = currentSchematicLine.line[number.firstCharIndex - 1]

          if (isSymbol(leftChar)) {
            partNumbers.add(number)
            continue@lineNumberLoop
          }
        }

        // Right on same line
        if (number.lastCharIndex != currentSchematicLine.line.size - 1) {
          val rightChar = currentSchematicLine.line[number.lastCharIndex + 1]

          if (isSymbol(rightChar)) {
            partNumbers.add(number)
            continue@lineNumberLoop
          }
        }

        // Above and below for each number char
        // +-1 to account for diagonals
        for (numberIndex in (number.firstCharIndex - 1)..(number.lastCharIndex + 1)) {
          if (numberIndex < 0)
            continue

          if (previousSchematicLine != null && numberIndex < previousSchematicLine.line.size) {
            val adjacentAbove = previousSchematicLine.line[numberIndex]

            if (isSymbol(adjacentAbove)) {
              partNumbers.add(number)
              continue@lineNumberLoop
            }
          }

          if (nextSchematicLine != null && numberIndex < nextSchematicLine.line.size) {
            val adjacentBelow = nextSchematicLine.line[numberIndex]

            if (isSymbol(adjacentBelow)) {
              partNumbers.add(number)
              continue@lineNumberLoop
            }
          }
        }
      }

      return partNumbers
    }

    private fun day3Puzzle1() {
      val schematicLines = parseSchematicLines("day3_1.txt")
      var totalPartSum = 0L

      for (schematicLineIndex in schematicLines.indices) {
        for (partNumber in findPartNumbers(schematicLines, schematicLineIndex))
          totalPartSum += partNumber.number
      }

      println("The total part sum is: $totalPartSum")
    }

    private fun findGearRatios(schematicLines: List<FilteredSchematicLine>, schematicLineIndex: Int): List<Int> {
      val currentSchematicLine = schematicLines[schematicLineIndex]
      val previousSchematicLine = schematicLines.getOrNull(schematicLineIndex - 1)
      val nextSchematicLine = schematicLines.getOrNull(schematicLineIndex + 1)

      val gearRatios = mutableListOf<Int>()

      charLoop@ for (lineCharIndex in currentSchematicLine.line.indices) {
        val gearRatioMembers = HashSet<NumberAndIndex>()
        val lineChar = currentSchematicLine.line[lineCharIndex]

        if (lineChar != '*')
          continue

        if (lineCharIndex != 0) {
          val partNumberBefore = currentSchematicLine.partNumberIndices[lineCharIndex - 1]

          if (partNumberBefore != null)
            gearRatioMembers.add(partNumberBefore)
        }

        if (lineCharIndex != currentSchematicLine.line.size - 1) {
          val partNumberAfter = currentSchematicLine.partNumberIndices[lineCharIndex + 1]

          if (partNumberAfter != null)
            gearRatioMembers.add(partNumberAfter)
        }

        for (charIndex in (lineCharIndex - 1)..(lineCharIndex + 1)) {
          if (charIndex < 0)
            continue

          if (previousSchematicLine != null && charIndex < previousSchematicLine.line.size) {
            val adjacentAbove = previousSchematicLine.partNumberIndices[charIndex]

            if (adjacentAbove != null)
              gearRatioMembers.add(adjacentAbove)
          }

          if (nextSchematicLine != null && charIndex < nextSchematicLine.line.size) {
            val adjacentBelow = nextSchematicLine.partNumberIndices[charIndex]

            if (adjacentBelow != null)
              gearRatioMembers.add(adjacentBelow)
          }
        }

        if (gearRatioMembers.size == 2) {
          val gearRatio = gearRatioMembers.fold(0) { accumulator, current ->
            if (accumulator == 0)
              current.number
            else
              accumulator * current.number
          }

          gearRatios.add(gearRatio)
        }
      }

      return gearRatios
    }

    private fun day3Puzzle2() {
      val schematicLines = parseSchematicLines("day3_1.txt")
      val filteredSchematicLines = mutableListOf<FilteredSchematicLine>()

      for (schematicLineIndex in schematicLines.indices) {
        val line = schematicLines[schematicLineIndex].line

        val partNumbers = findPartNumbers(schematicLines, schematicLineIndex)
        val partNumberIndices = arrayOfNulls<NumberAndIndex>(line.size)

        for (partNumber in partNumbers) {
          for (digitIndex in partNumber.firstCharIndex..partNumber.lastCharIndex)
            partNumberIndices[digitIndex] = partNumber
        }

        val schematicLine = FilteredSchematicLine(line, partNumberIndices)
        filteredSchematicLines.add(schematicLine)
      }

      var gearRatioSum = 0

      for (filteredSchematicLineIndex in filteredSchematicLines.indices) {
        for (gearRatio in findGearRatios(filteredSchematicLines, filteredSchematicLineIndex))
          gearRatioSum += gearRatio
      }

      println("The total gear ratio sum is: $gearRatioSum")
    }

    private fun isSymbol(char: Char): Boolean {
      return char != '.' && !char.isDigit()
    }

    private fun numberFromCharArray(chars: CharArray): Int {
      var result = 0
      val length = chars.size

      for (i in chars.indices) {
        val placeValue = length - 1 - i
        val digitValue = chars[i].code - '0'.code

        result += digitValue * 10.0.pow(placeValue).toInt()
      }

      return result
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
            firstDigit = currentChar

          if (lastDigitIndex == null || lastDigitIndex < i)
            lastDigitIndex = i
        }
      }

      if (firstDigit == null || lastDigitIndex == null)
        throw IllegalStateException("The input did not contain digits: $input")

      return 10 * (firstDigit.code - '0'.code) + (input[lastDigitIndex].code - '0'.code)
    }

    private fun day4Puzzle1() {
      val cards = parseScratchCards("day4_1.txt")

      val totalWorth = cards.sumOf {
        if (it.matchingNumbers.isEmpty()) 0 else 2.0.pow(it.matchingNumbers.size - 1).toInt()
      }

      println("The total worth in points is $totalWorth")
    }

    private fun day4Puzzle2() {
      val cards = parseScratchCards("day4_1.txt")

      fun evaluateCard(card: ScratchCard, cardIndex: Int, totalCardCount: Int): Int {
        val matchCount = card.matchingNumbers.size
        var result = totalCardCount + matchCount

        for (copyCardNumber in 1..matchCount) {
          val copyCardIndex = cardIndex + copyCardNumber
          val copyCard = cards[copyCardIndex]

          result = evaluateCard(copyCard, copyCardIndex, result)
        }

        return result
      }

      var totalCardCount = 0

      for (cardIndex in cards.indices) {
        totalCardCount = evaluateCard(cards[cardIndex], cardIndex, totalCardCount)
        ++totalCardCount // This original card
      }

      println("The total card count is: $totalCardCount")
    }

    private fun day5Puzzle1() {
      val almanac = parseAlmanac("day5_1.txt", false)

      var lowestLocationNumber: Long? = null

      for (seed in almanac.seedRanges) {
        if (seed.first != seed.last)
          throw IllegalStateException("Expected only single seeds, not ranges")

        val location = almanac.findLocationNumber(seed.first)

        if (lowestLocationNumber == null || lowestLocationNumber > location)
          lowestLocationNumber = location
      }

      println("The lowest location number is $lowestLocationNumber")
    }

    private fun day5Puzzle2() {
      val almanac = parseAlmanac("day5_1.txt", true)

      var lowestLocationNumber: Long? = null

      timeExecution("Location computation took") {
        for (location in almanac.mapSeedsToLocations()) {
          if (lowestLocationNumber == null || lowestLocationNumber!! > location.first)
            lowestLocationNumber = location.first
        }
      }

      println("The lowest location number is $lowestLocationNumber")
    }

    private fun day6Puzzle1() {
      val data = parseBoatRaceData("day6_1.txt", false)

      var result = 0L

      for (entry in data) {
        val numberOfWays = entry.countNumberOfWaysToWin(entry)

        if (result == 0L) {
          result = numberOfWays
          continue
        }

        result *= numberOfWays
      }

      println("The result is $result")
    }

    private fun day6Puzzle2() {
      val data = parseBoatRaceData("day6_1.txt", true)

      var result = 0L

      for (entry in data) {
        val numberOfWays = entry.countNumberOfWaysToWin(entry)

        if (result == 0L) {
          result = numberOfWays
          continue
        }

        result *= numberOfWays
      }

      println("The result is $result")
    }

    private fun day7Puzzle1() {
      val sortedHands = parseCamelCardHands("day7_1.txt", false).sorted()

      var totalWinnings = 0L

      for (handIndex in sortedHands.indices)
        totalWinnings += (handIndex + 1) * sortedHands[handIndex].bidAmount

      println("The total winnings are $totalWinnings")
    }

    private fun day7Puzzle2() {
      val sortedHands = parseCamelCardHands("day7_1.txt", true).sorted()

      var totalWinnings = 0L

      for (handIndex in sortedHands.indices)
        totalWinnings += (handIndex + 1) * sortedHands[handIndex].bidAmount

      println("The total winnings are $totalWinnings")
    }

    private fun day8Puzzle1() {
      val network = parseNavigationNetwork("day8_1.txt")

      val startNode = network.findNode("AAA")
      val endNodes = network.findNodes { it == "ZZZ" }

      val steps = walkUntilEnd(startNode, network, endNodes)

      println("Took $steps steps")
    }

    private fun day8Puzzle2() {
      val network = parseNavigationNetwork("day8_1.txt")

      val endNodes = network.findNodes { it.endsWith('Z') }
      val startNodes = network.findNodes { it.endsWith('A') }

      if (startNodes.size != endNodes.size)
        throw IllegalStateException("Expected there to be as many start- as end-nodes")

      val pathLengths = IntArray(startNodes.size) { 0 }

      for ((index, startNode) in startNodes.withIndex()) {
        val steps = walkUntilEnd(startNode, network, endNodes)
        pathLengths[index] = steps
      }

      if (endNodes.isNotEmpty())
        throw IllegalStateException("Not all end nodes found a corresponding start")

      val factorMaxFrequencies = mutableMapOf<Int, Int>()

      for (pathLength in pathLengths) {
        val factorFrequencies = mutableMapOf<Int, Int>()

        for (primeFactor in primeFactorization(pathLength)) {
          val existingFrequency = factorFrequencies.computeIfAbsent(primeFactor) { 0 }
          factorFrequencies[primeFactor] = existingFrequency + 1
        }

        for (factorFrequencyEntry in factorFrequencies) {
          val currentMaxFrequency = factorMaxFrequencies.computeIfAbsent(factorFrequencyEntry.key) { 0 }
          if (factorFrequencyEntry.value > currentMaxFrequency)
            factorMaxFrequencies[factorFrequencyEntry.key] = factorFrequencyEntry.value
        }
      }

      val leastCommonMultiple = factorMaxFrequencies.entries.fold(0L) { accumulator, current ->
        val currentValue = (current.key * current.value).toLong()

        if (accumulator == 0L)
          currentValue
        else
          accumulator * currentValue
      }

      println("The least common multiple of all path-length's prime factors is: $leastCommonMultiple")
    }

    private fun day9Puzzle1() {
      val result = parseSensorValues("day9_1.txt").sumOf { list -> extrapolateNextElement(list, false) }
      println("The sum of all extrapolated elements is $result")
    }

    private fun day9Puzzle2() {
      val result = parseSensorValues("day9_1.txt").sumOf { list -> extrapolateNextElement(list, true) }
      println("The sum of all extrapolated elements is $result")
    }

    private fun day10Puzzle1() {
      val pipeMap = parsePipeMap("day10_1.txt")
      val mainLoop = pipeMap.walkMainLoop()
      val farthestPoint = (mainLoop.size + (2 - 1)) / 2

      println("The farthest point in steps is: $farthestPoint")
    }

    private fun day10Puzzle2() {
      val pipeMap = parsePipeMap("day10_2_example.txt")
      val mainLoop = pipeMap.walkMainLoop()
      val enclosedTiles = pipeMap.findEnclosedTiles(mainLoop)

      println(pipeMap.toString { tile, result ->
        // Loop, do not override
        if (mainLoop.contains(tile))
          return@toString false

        // Inside
        if (enclosedTiles.contains(tile)) {
          result.append('I')
          return@toString true
        }

        // Outside
        result.append('O')
        return@toString true
      })

      // 152 is too low
      println("There are ${enclosedTiles.size} enclosed tiles within a loop of size ${mainLoop.size}")
    }

    private fun day11Puzzle1() {
      val image = parseObservatoryImage("day11_1.txt")
      println(image)

      val expandedImage = image.expandImage(1)
      println(expandedImage)

      val pairDistances = ObservatoryImage.calculatePairDistances(expandedImage.galaxies)
      var totalDistanceSum = 0L

      for (pairDistance in pairDistances)
        totalDistanceSum += pairDistance

      println("The total distance sum is $totalDistanceSum")
    }

    private fun day11Puzzle2() {
      val image = parseObservatoryImage("day11_1.txt")
      val pairDistances = ObservatoryImage.calculatePairDistances(image.expandGalaxies(1_000_000 - 1))

      var totalDistanceSum = 0L

      for (pairDistance in pairDistances)
        totalDistanceSum += pairDistance

      println("The total distance sum is $totalDistanceSum")
    }

    private fun parseObservatoryImage(file: String): ObservatoryImage {
      return InputFile(file).use {
        var rowIndex = 0
        var columnSize: Int? = null

        val galaxies = buildList {

          for (line in it) {
            var columnIndex = 0

            for (char in line) {
              if (char == '#')
                add(Galaxy(rowIndex, columnIndex))

              else if (char != '.')
                throw IllegalStateException("Unexpected character: $char")

              ++columnIndex
            }

            if (columnSize == null)
              columnSize = columnIndex
            else {
              if (columnSize != columnIndex)
                throw IllegalStateException("Expected all rows to be of equal length")
            }

            ++rowIndex
          }
        }

        ObservatoryImage(
          galaxies,
          columnSize
            ?: throw IllegalStateException("Encountered empty file"),
          rowIndex
        )
      }
    }

    private fun parsePipeMap(file: String): PipeMap {
      return InputFile(file).use {
        val tileLines = mutableListOf<Array<PipeMapTile>>()
        var startingPosition: PipeMapTile? = null

        for (line in it) {
          if (line.isBlank() || line.startsWith('#'))
            continue

          val currentY = tileLines.size
          var startInLine: PipeMapTile? = null

          val lineTiles = line.toCharArray().mapIndexed { index, char ->
            val type = PipeMapTileType.fromChar(char)
            val tile = PipeMapTile(type, Position(index, currentY))

            if (type == PipeMapTileType.START) {
              if (startInLine != null)
                throw IllegalStateException("Encountered two starting positions on the same line in file $file")

              startInLine = tile
            }

            tile
          }.toTypedArray()

          if (startInLine != null) {
            if (startingPosition != null)
              throw IllegalStateException("Encountered another starting position in file $file")

            startingPosition = startInLine
          }

          tileLines.add(lineTiles)
        }

        if (startingPosition == null)
          throw IllegalStateException("Could not find the starting position in file $file")

        PipeMap(tileLines.toTypedArray(), startingPosition)
      }
    }

    private fun generateNextValues(values: List<Long>): Pair<List<Long>, Long?> {
      var commonDelta: Long? = 0

      val nextValues = buildList {
        for (index in 1 until values.size) {
          val delta = values[index] - values[index - 1]

          if (index == 1)
            commonDelta = delta
          else if (commonDelta != delta)
            commonDelta = null

          add(delta)
        }
      }

      return Pair(nextValues, commonDelta)
    }

    private fun extrapolateNextElement(values: List<Long>, previous: Boolean): Long {
      val allRows = mutableListOf<List<Long>>()
      allRows.add(values)

      while (true) {
        val size = allRows.size
        val previousValues = if (size == 0) values else allRows[size - 1]
        val (nextValues, commonDelta) = generateNextValues(previousValues)

        allRows.add(nextValues)

        var fromBelow = commonDelta ?: continue

        // Skip self, iterate other rows backwards
        for (rowIndex in allRows.size - 2 downTo 0) {
          val fromAbove = (
            if (previous)
              allRows[rowIndex].first()
            else
              allRows[rowIndex].last()
          )

          val nextInAbove = (
            if (previous)
              fromAbove - fromBelow
            else
              fromBelow + fromAbove
          )

          fromBelow = nextInAbove
        }

        return fromBelow
      }
    }

    private fun parseSensorValues(file: String): List<List<Long>> {
      return InputFile(file).use {
        buildList {
          for (line in it)
            add(parseSpaceSeparatedNumbers(line))
        }
      }
    }

    private fun primeFactorization(number: Int): List<Int> {
      var currentNumber = number
      var divisorMax = sqrt(currentNumber.toDouble()).toInt()
      var divisor = 2

      val result = mutableListOf<Int>()

      while (divisor <= divisorMax) {
        if (number % divisor == 0) {
          result.add(divisor)
          currentNumber /= divisor
          divisorMax = sqrt(currentNumber.toDouble()).toInt()
          continue
        }

        ++divisor
      }

      if (currentNumber > 0)
        result.add(currentNumber)

      return result
    }

    private fun walkUntilEnd(
      node: NavigationNode,
      network: NavigationNetwork,
      remainingEnds: MutableSet<NavigationNode>
    ): Int {
      var stepCounter = 0

      if (remainingEnds.isEmpty())
        throw IllegalStateException("No more end nodes available, would end in an infinite loop")

      var currentNode = node
      while (true) {
        currentNode = network.findNode(currentNode.choose(network.nextDirection()))

        ++stepCounter

        if (remainingEnds.remove(currentNode))
          break
      }

      return stepCounter
    }

    private fun parseNavigationNetwork(file: String): NavigationNetwork {
      return InputFile(file).use {
        if (!it.hasNext())
          throw IllegalStateException("Expected L/R string line")

        val directions = it.next().toCharArray().map { char ->
          when (char) {
            'L' -> NavigationDirection.LEFT
            'R' -> NavigationDirection.RIGHT
            else -> throw IllegalStateException("Unknown navigation direction: $char")
          }
        }

        val nodes = mutableMapOf<String, NavigationNode>()

        for (networkLine in it) {
          // AAA = (BBB, BBB)
          val equalsIndex = networkLine.indexOf('=')

          if (equalsIndex < 0)
            continue

          val commaIndex = networkLine.indexOf(',')

          if (commaIndex < 0)
            continue

          val key = networkLine.substring(0, equalsIndex - 1)
          val leftName = networkLine.substring(equalsIndex + 3, commaIndex)
          val rightName = networkLine.substring(commaIndex + 2, networkLine.length - 1)

          if (nodes.containsKey(key))
            throw IllegalStateException("Duplicate mapping for key $key encountered")

          nodes[key] = NavigationNode(key, leftName, rightName)
        }

        NavigationNetwork(directions.toTypedArray(), nodes)
      }
    }

    private fun parseCamelCardHands(file: String, withJBeingJoker: Boolean): List<CamelCardsHand> {
      return InputFile(file).use {
        val result = mutableListOf<CamelCardsHand>()

        for (line in it) {
          val lineData = line.split(' ')

          if (lineData.size != 2)
            throw IllegalStateException("Expected two column input")

          val cards = lineData[0].toCharArray().map { char -> CamelCard(char, withJBeingJoker) }
          val bidAmount = lineData[1].toLong()

          result.add(CamelCardsHand(cards, bidAmount, withJBeingJoker))
        }

        result
      }
    }

    private fun parseBoatRaceData(file: String, ignoreSpaces: Boolean): List<BoatRaceEntry> {
      return InputFile(file).use {
        val result = mutableListOf<BoatRaceEntry>()

        if (!it.hasNext())
          throw IllegalStateException("Expected there to be a time line")

        var timeData = it.next()

        if (!it.hasNext())
          throw IllegalStateException("Expected there to be a distance line")

        var distanceData = it.next()

        if (ignoreSpaces) {
          timeData = timeData.replace(" ", "")
          distanceData = distanceData.replace(" ", "")
        }

        val timeValues = parseSpaceSeparatedNumbers(timeData.substring(timeData.indexOf(':') + 1))
        val distanceValues = parseSpaceSeparatedNumbers(distanceData.substring(timeData.indexOf(':') + 1))

        for (index in timeValues.indices)
          result.add(BoatRaceEntry(timeValues[index], distanceValues[index]))

        if (timeValues.size != distanceValues.size)
          throw IllegalStateException("Expected there to be as many time values as distance values")

        if (it.hasNext())
          throw IllegalStateException("Unexpected next line: ${it.next()}")

        result
      }
    }

    private fun parseAlmanac(file: String, interpretSeedLineAsRanges: Boolean): GardeningAlmanac {
      return InputFile(file).use {

        if (!it.hasNext())
          throw IllegalStateException("Expected seeds line")

        val seedsLine = it.next()
        val seedLineNumbers = parseSpaceSeparatedNumbers(seedsLine.substring(seedsLine.indexOf(':') + 1)).toList()

        val seeds: List<FirstLastLongRange> = if (!interpretSeedLineAsRanges)
          seedLineNumbers.map { number -> FirstLastLongRange(number, number) }
        else {
          if (seedLineNumbers.size % 2 != 0)
            throw IllegalStateException("Expected seed numbers to come in pairs")

          buildList {
            for (numberIndex in seedLineNumbers.indices step 2) {
              val firstNumber = seedLineNumbers[numberIndex]
              val secondNumber = seedLineNumbers[numberIndex + 1]

              add(FirstLastLongRange(firstNumber, firstNumber + secondNumber - 1))
            }
          }
        }

        val almanacMapFields = GardeningAlmanac::class.java.declaredFields
          .mapNotNull { field ->
            val annotation = field.getAnnotation(AlmanacMap::class.java) ?: return@mapNotNull null
            Pair(annotation.name, field)
          }

        val almanacParameters = arrayOfNulls<Any>(almanacMapFields.size + 1)
        almanacParameters[0] = seeds

        var collectedRanges = mutableListOf<LongRangeMapping>()

        lineIterator@ while (it.hasNext()) {
          val currentLine = it.next()

          if (currentLine.isBlank())
            continue

          for (almanacMapFieldIndex in almanacMapFields.indices) {
            val almanacMapField = almanacMapFields[almanacMapFieldIndex]

            if (currentLine.startsWith(almanacMapField.first)) {
              collectedRanges = mutableListOf()
              almanacParameters[almanacMapFieldIndex + 1] = LongMultiRangeMap(collectedRanges)
              continue@lineIterator
            }
          }

          val rangeMappingNumbers = parseSpaceSeparatedNumbers(currentLine).toList()

          if (rangeMappingNumbers.size != 3)
            throw IllegalStateException("Expected three numbers per range mapping")

          val destinationRangeStart = rangeMappingNumbers[0]
          val sourceRangeStart = rangeMappingNumbers[1]
          val rangeLength = rangeMappingNumbers[2]

          collectedRanges.add(LongRangeMapping(
            sourceRangeStart, sourceRangeStart + rangeLength - 1,
            - sourceRangeStart + destinationRangeStart
          ))
        }

        val constructor = GardeningAlmanac::class.java.declaredConstructors
          .filter { constructor ->
            if (constructor.parameterCount != almanacMapFields.size + 1)
              return@filter false

            if (constructor.parameterTypes[0] != List::class.java)
              return@filter false

            for (i in almanacMapFields.indices) {
              if (constructor.parameterTypes[i + 1] != LongMultiRangeMap::class.java)
                return@filter false
            }

            return@filter true
          }
          .firstOrNull()
          ?: throw IllegalStateException("Could not find target constructor (List, Map...)")

        constructor.newInstance(*almanacParameters) as GardeningAlmanac
      }
    }

    private fun parseScratchCards(file: String): List<ScratchCard> {
      return InputFile(file).use {
        val cards = mutableListOf<ScratchCard>()

        for (line in it) {
          val colonIndex = line.indexOf(':')
//          val cardId = line.substring("Card ".length, colonIndex).toInt()
          val (winningNumbersString, ownNumbersString) = line.substring(colonIndex + 2).split(" | ")
          val winningNumbers = parseSpaceSeparatedNumbers(winningNumbersString).toHashSet()
          val ownNumbers = parseSpaceSeparatedNumbers(ownNumbersString).toHashSet()

          val matchingNumbers = winningNumbers.intersect(ownNumbers)
          cards.add(ScratchCard(winningNumbers, ownNumbers, matchingNumbers))
        }

        cards
      }
    }

    private fun parseSpaceSeparatedNumbers(input: String): List<Long> {
      val result = mutableListOf<Long>()
      val numberBuilder = StringBuilder()
      val inputLength = input.length

      for (charIndex in 0 until inputLength) {
        val char = input[charIndex]
        val isDigit = char.isDigit() || char == '-'

        if (isDigit)
          numberBuilder.append(char)

        if (!isDigit || charIndex == inputLength - 1) {
          if (numberBuilder.isEmpty())
            continue

          result.add(numberBuilder.toString().toLong())
          numberBuilder.clear()
          continue
        }
      }

      return result
    }

  }
}