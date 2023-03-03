package Generator

import model.City
import java.util.Random

class NClosestConnectionGenerator(val n: Int, val fuzzyness: Double? = null) : CityConnectionGenerator {
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
                .sortedBy { it.second }
                .possiblyFuzzyTake(n)
        }.flatten().toMap()
    }

    fun <T> Iterable<T>.possiblyFuzzyTake(n: Int): List<T> {
        if (fuzzyness == null) {
            return take(n)
        }
        return fuzzyTake(n, fuzzyness)
    }

    fun <T> Iterable<T>.fuzzyTake(n: Int, fuzzyness: Double): List<T> {
        var taken = 0
        val list= mutableListOf<T>()
        val iterator = iterator()

        while (taken < n && iterator.hasNext()) {
            if (random.nextDouble() > fuzzyness) {
                taken++
                list.add(iterator.next())
            }
        }

        return list
    }

    companion object {
        val random = Random()
    }
}

