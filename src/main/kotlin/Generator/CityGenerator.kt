package Generator

import model.City

interface CityGenerator {
    fun generateCities(n: Int): List<City>
}