package me.blvckbytes.aoc_2023.data

class NavigationNetwork(
  private val directions: Array<NavigationDirection>,
  private val nodes: Map<String, NavigationNode>,
) {
  private var navigationIndex = 0

  /*
    A name only contains uppercase letters and numbers
    A - Z 25
    0 - 9 10
          --
          35

    Range of each char is from 0-34 => 6 bits required
    For three characters, this makes 2^(3*6) = 2^18 = 262144 slots
   */
  private val quickAccessNodes: Array<NavigationNode?> = arrayOfNulls(262144)

  init {
    val seenIndices = mutableSetOf<Int>()

    for (nodeEntry in nodes) {
      val name = nodeEntry.key

      if (name.length != 3)
        throw IllegalStateException("Expected names to be exactly three chars long")

      val index = nameToIndex(name.toCharArray())

      if (!seenIndices.add(index))
        throw IllegalStateException("Encountered duplicate index $index")

      quickAccessNodes[index] = nodeEntry.value
    }
  }

  private fun nameToIndex(name: CharArray): Int {
    return (charToId(name[0]) shl (0)) or (charToId(name[1]) shl (6)) or (charToId(name[2]) shl (12))
  }

  private fun charToId(char: Char): Int {
    val code = char.code

    if (code >= '0'.code && code <= '9'.code)
      return code - '0'.code

    if (code >= 'A'.code && code <= 'Z'.code)
      return code - 'A'.code + 9

    throw IllegalStateException("Illegal character $char")
  }

  fun findNodes(predicate: (name: String) -> Boolean): MutableSet<NavigationNode> {
    val result = HashSet<NavigationNode>()

    for (nodeEntry in nodes) {
      if (!predicate(nodeEntry.key))
        continue

      if (!result.add(nodeEntry.value))
        throw IllegalStateException("Encountered duplicate node")
    }

    return result
  }

  fun findNode(name: String): NavigationNode {
    return quickAccessNodes[nameToIndex(name.toCharArray())]
      ?: throw IllegalStateException("Didn't find the node $name")
  }

  fun nextDirection(): NavigationDirection {
    val direction = directions[navigationIndex]

    if (++navigationIndex == directions.size)
      navigationIndex = 0

    return direction
  }
}