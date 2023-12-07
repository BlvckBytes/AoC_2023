package me.blvckbytes.aoc_2023.data

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

data class BoatRaceEntry(
  val time: Long,
  val distance: Long,
) {

  fun countNumberOfWaysToWin(raceEntry: BoatRaceEntry): Long {
    /*
      distance(chargeTime) = (raceTime - chargeTime) * chargeTime
      distance(chargeTime) = -chargeTime^2 + raceTime * chargeTime
      distance'(chargeTime) = -2 * chargeTime + raceTime

      0 = -2 * chargeTime + raceTime
      2 * chargeTime = raceTime
      chargeTime = raceTime / 2

      => distance(raceTime / 2) is the max. distance possible

      distance = -chargeTime^2 + raceTime * chargeTime
      0 = -chargeTime^2 + raceTime * chargeTime - distance
      0 = chargeTime^2 - raceTime * chargeTime + distance

      chargeTime = -(-raceTime/2) +- sqrt((-raceTime/2)^2 - distance)
      chargeTime = raceTime/2 +- sqrt(raceTime^2/4 - distance)
      chargeTime = raceTime/2 +- sqrt(raceTime^2/4 - (4*distance)/4)
      chargeTime = raceTime/2 +- sqrt((raceTime^2 - 4*distance)/4)

      => chargeTime(distance) = raceTime/2 +- sqrt(raceTime^2 - 4*distance)/2
     */

    val raceTimeHalf = raceEntry.time / 2.0
    val currentBestChargeTime = raceTimeHalf - sqrt(raceEntry.time * raceEntry.time - 4.0 * raceEntry.distance) / 2.0

    if (currentBestChargeTime == raceTimeHalf)
      return 0

    val currentBestInFirstHalf = currentBestChargeTime < raceTimeHalf

    val nextBestChargeTime = (
      // Original choice was not aligned to the whole number grid, thus the next whole choice is a valid choice
      if (currentBestChargeTime % 1.0 != 0.0) {
        if (currentBestInFirstHalf)
          ceil(currentBestChargeTime)
        else
          floor(currentBestChargeTime)
      }

      // Was aligned to the whole number grid, advance to next choice
      else {
        if (currentBestInFirstHalf)
          currentBestChargeTime + 1
        else
          currentBestChargeTime - 1
      }
    )

    // * 2 for both sides of the parabola (slow speed, much time == high speed, little time)
    // + 1 for the equal choice skipped by 2*(floor/ceil/+1/-1) on the un-chosen other half
    return ((
      if (currentBestInFirstHalf)
        (raceTimeHalf - nextBestChargeTime)
      else
        (nextBestChargeTime - raceTimeHalf)
    ) * 2 + 1).toLong()
  }
}