package me.blvckbytes.aoc_2023.data

class ObservatoryImage(
  val galaxies: List<Galaxy>,
  private val numberOfColumns: Int,
  private val numberOfRows: Int
) {
  companion object {
    fun calculatePairDistances(galaxies: List<Galaxy>): List<Int> {
      return buildList {
        for (firstGalaxyIndex in 0 until galaxies.size - 1) {
          val firstGalaxy = galaxies[firstGalaxyIndex]
          for (secondGalaxyIndex in firstGalaxyIndex + 1 until galaxies.size)
            add(firstGalaxy.distanceTo(galaxies[secondGalaxyIndex]))
        }
      }
    }
  }

  private val grid: Array<Array<Galaxy?>> = Array(numberOfRows) { arrayOfNulls(numberOfColumns) }

  private val nonEmptyColumnFlags = BooleanArray(numberOfColumns)
  private val nonEmptyRowFlags = BooleanArray(numberOfRows)

  private val numberOfEmptyRows: Int
  private val numberOfEmptyColumns: Int

  init {
    var numberOfNonEmptyColumns = 0
    var numberOfNonEmptyRows = 0

    for (galaxy in galaxies) {
      grid[galaxy.rowIndex][galaxy.columnIndex] = galaxy

      if (!nonEmptyRowFlags[galaxy.rowIndex]) {
        nonEmptyRowFlags[galaxy.rowIndex] = true
        ++numberOfNonEmptyRows
      }

      if (!nonEmptyColumnFlags[galaxy.columnIndex]) {
        nonEmptyColumnFlags[galaxy.columnIndex] = true
        ++numberOfNonEmptyColumns
      }
    }

    numberOfEmptyColumns = numberOfColumns - numberOfNonEmptyColumns
    numberOfEmptyRows = numberOfRows - numberOfNonEmptyRows
  }

  fun expandGalaxies(additional: Int): List<Galaxy> {
    return buildList {
      var newGridRowIndex = 0

      for (rowIndex in grid.indices) {
        val row = grid[rowIndex]
        var newGridColumnIndex = 0

        // Insert blank row before current row
        if (!nonEmptyRowFlags[rowIndex])
          newGridRowIndex += additional

        for (columnIndex in row.indices) {
          // Insert blank column before current column
          if (!nonEmptyColumnFlags[columnIndex])
            newGridColumnIndex += additional

          row[columnIndex]?.also {
            add(Galaxy(newGridRowIndex, newGridColumnIndex))
          }

          ++newGridColumnIndex
        }

        ++newGridRowIndex
      }
    }
  }

  fun expandImage(additional: Int): ObservatoryImage {
    return ObservatoryImage(
      expandGalaxies(additional),
      numberOfColumns + numberOfEmptyColumns * additional,
      numberOfRows + numberOfEmptyRows * additional,
    )
  }

  override fun toString(): String {
    val result = StringBuilder()

    result.append(' ')

    for (columnIndex in 0 until numberOfColumns) {
      if (!nonEmptyColumnFlags[columnIndex])
        result.append('v')
      else
        result.append(' ')
    }

    for (rowIndex in grid.indices) {
      result.append('\n')

      val isRowEmpty = !nonEmptyRowFlags[rowIndex]

      if (isRowEmpty)
        result.append('>')
      else
        result.append(' ')

      for (galaxy in grid[rowIndex]) {
        if (galaxy == null)
          result.append('.')
        else
          result.append('#')
      }

      if (isRowEmpty)
        result.append('<')
      else
        result.append(' ')
    }

    result.append('\n')
    result.append(' ')

    for (columnIndex in 0 until numberOfColumns) {
      if (!nonEmptyColumnFlags[columnIndex])
        result.append('^')
      else
        result.append(' ')
    }

    result.append('\n')

    return result.toString()
  }
}