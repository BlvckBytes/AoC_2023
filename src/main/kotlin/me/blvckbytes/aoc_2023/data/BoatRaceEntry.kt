package me.blvckbytes.aoc_2023.data

data class BoatRaceEntry(
  val time: Long,
  val distance: Long,
) {

  fun countNumberOfWaysToWin(raceEntry: BoatRaceEntry): Long {
    val currentBestDistance = raceEntry.distance
    var result = 0L

    // 0 and time don't make any sense, as with zero there's no speed
    // and with time there's no time left to move, no matter how fast
    for (chargeTime in 1 until raceEntry.time) {
      val raceTime = raceEntry.time - chargeTime
      val distanceTravelled = chargeTime * raceTime // [mm/ms * ms]

      if (distanceTravelled > currentBestDistance)
        ++result
    }

    return result
  }

}