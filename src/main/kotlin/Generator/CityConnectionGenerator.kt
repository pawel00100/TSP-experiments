package Generator

import model.City
import kotlin.math.sqrt

interface CityConnectionGenerator {
    fun generateConnections(cities: List<City>): List<Pair<City, City>>

    fun generateConnectionsWithDistances(cities: List<City>): Map<Pair<City, City>, Double> {
        return generateConnections(cities).associateWith { pair ->
            val dist1 = pair.first.x - pair.second.x
            val dist2 = pair.first.y - pair.second.y
            sqrt(dist1 * dist1 + dist2 * dist2)
        }
    }
}