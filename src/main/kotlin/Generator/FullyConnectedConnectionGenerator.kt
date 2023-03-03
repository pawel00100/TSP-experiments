package Generator

import model.City

class FullyConnectedConnectionGenerator : CityConnectionGenerator {
    override fun generateConnections(cities: List<City>): List<Pair<City, City>> {
        val n = cities.size
        return (0 until n).map { i ->
            (0 until n)
                .filter { i != it }
                .map { j -> Pair(cities[i], cities[j]) }
        }.flatten()
    }
}