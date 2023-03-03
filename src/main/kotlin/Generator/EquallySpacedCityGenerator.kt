package Generator

import model.City
import java.util.*

class EquallySpacedCityGenerator : CityGenerator {
    override fun generateCities(n: Int): List<City> {
        return (1..n).map { City(random.nextDouble()*2 - 1, random.nextDouble()*2 - 1) }
    }
    companion object {
        val random = Random()
    }
}