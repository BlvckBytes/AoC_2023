package me.blvckbytes.aoc_2023

import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader

class InputFile(name: String): Iterator<String>, Closeable {

  private val resourceStream = javaClass.getResourceAsStream("/input/$name")
    ?: throw IllegalArgumentException("Could not locate resource $name")

  private val resourceReader = BufferedReader(InputStreamReader(resourceStream))

  private var nextLine: String? = null
  private var streamClosed = false

  override fun hasNext(): Boolean {
    if (streamClosed)
      throw IllegalStateException("Do not reuse a closed input file")

    nextLine = resourceReader.readLine()

    if (nextLine == null) {
      close()
      return false
    }

    return true
  }

  override fun next(): String {
    return nextLine
      ?: throw IllegalStateException("Missing prior hasNext() check")
  }

  override fun close() {
    if (streamClosed)
      return

    streamClosed = true
    resourceReader.close()
    resourceStream.close()
  }
}