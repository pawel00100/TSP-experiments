package Generator

import model.City

class FixedDistanceConnectionGenerator(val distance: Double) : CityConnectionGenerator {
    val fullyConnectedConnectionGenerator = FullyConnectedConnectionGenerator()
    override fun generateConnections(cities: List<City>): List<Pair<City, City>> {
        TODO("Not yet implemented")
    }

    override fun generateConnectionsWithDistances(cities: List<City>): Map<Pair<City, City>, Double> {
        return (0 until cities.size).map { i ->
            (0 until cities.size)
                .filter { i != it }
                .map { j ->
                    val dist1 = cities[i].x - cities[j].x
                    val dist2 = cities[i].y - cities[j].y
                    Pair(cities[i], cities[j]) to Math.sqrt(dist1 * dist1 + dist2 * dist2)
                }
                .filter { it.second < distance}
        }.flatten().toMap()
    }

}