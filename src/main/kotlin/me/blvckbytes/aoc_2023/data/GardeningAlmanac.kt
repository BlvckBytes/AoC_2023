package me.blvckbytes.aoc_2023.data

class GardeningAlmanac(
  private val seedRanges: List<FirstLengthLongRange>,
  @field:AlmanacMap("seed-to-soil")
  val seedToSoil: LongMultiRangeMap,
  @field:AlmanacMap("soil-to-fertilizer")
  val soilToFertilizer: LongMultiRangeMap,
  @field:AlmanacMap("fertilizer-to-water")
  val fertilizerToWater: LongMultiRangeMap,
  @field:AlmanacMap("water-to-light")
  val waterToLight: LongMultiRangeMap,
  @field:AlmanacMap("light-to-temperature")
  val lightToTemperature: LongMultiRangeMap,
  @field:AlmanacMap("temperature-to-humidity")
  val temperatureToHumidity: LongMultiRangeMap,
  @field:AlmanacMap("humidity-to-location")
  val humidityToLocation: LongMultiRangeMap,
) {

  fun seeds(): Iterator<Long> {
    return LongRangesIterator(seedRanges)
  }

  fun findLocationNumber(seed: Long): Long {
    val soil = seedToSoil.lookupValueOrGetKey(seed)
    val fertilizer = soilToFertilizer.lookupValueOrGetKey(soil)
    val water = fertilizerToWater.lookupValueOrGetKey(fertilizer)
    val light = waterToLight.lookupValueOrGetKey(water)
    val temperature = lightToTemperature.lookupValueOrGetKey(light)
    val humidity = temperatureToHumidity.lookupValueOrGetKey(temperature)
    return humidityToLocation.lookupValueOrGetKey(humidity)
  }
}
