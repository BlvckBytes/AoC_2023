package me.blvckbytes.aoc_2023.data

class GardeningAlmanac(
  val seedRanges: List<FirstLastLongRange>,
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

  fun mapSeedsToLocations(): Set<FirstLastLongRange> {
    val soilRanges = seedToSoil.mapRanges(seedRanges)
    val fertilizerRanges = soilToFertilizer.mapRanges(soilRanges)
    val waterRanges = fertilizerToWater.mapRanges(fertilizerRanges)
    val lightRanges = waterToLight.mapRanges(waterRanges)
    val temperatureRanges = lightToTemperature.mapRanges(lightRanges)
    val humidityRanges = temperatureToHumidity.mapRanges(temperatureRanges)
    return humidityToLocation.mapRanges(humidityRanges)
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
