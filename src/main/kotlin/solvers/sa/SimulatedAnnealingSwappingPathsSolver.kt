package solvers.sa

import solvers.Solver
import solvers.Result
import model.City
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.random.Random

class SimulatedAnnealingSwappingPathsSolver(cities: List<City>, distances: Map<Pair<City, City>, Double>, val decay: Double, var heat: Double = 100.0, val steps: Int) : Solver(cities, distances) {
    val path: MutableList<Pair<City, City>>

    init {
        val list = mutableListOf<Pair<City, City>>()
        var city1 = cities[0]
        cities.drop(1).forEach { city2 ->
            list.add(Pair(city1, city2))
            city1 = city2
        }
        path = list.toMutableList()
    }

    override fun calculateAll(): Result {

        (1..steps).forEach {
            step()
        }

        return result()
    }

    private fun step(): Result {
        val currentDist = result().distance


        var i: Int
        var j: Int
        var route1: Pair<City, City>
        var route2: Pair<City, City>
        while (true) {

            i = Random.nextInt(path.size)
            j = Random.nextInt(path.size)

            route1 = path[i]
            route2 = path[j]

            path[i] = Pair(route1.first, route2.first)
            path[j] = Pair(route1.second, route2.second)

            if (route1.first != route2.first && route1.second != route2.second && Random.nextBoolean()) {
                path[i] = Pair(route1.first, route2.first)
                path[j] = Pair(route1.second, route2.second)
            } else if (route1.first != route2.second && route1.second != route2.first) {
                path[i] = Pair(route1.first, route2.second)
                path[j] = Pair(route1.second, route2.first)
            } else if (route1.first != route2.first && route1.second != route2.second) {
                path[i] = Pair(route1.first, route2.first)
                path[j] = Pair(route1.second, route2.second)
            } else {
                println("weird")
            }

            if (checkIfConnected(cities, path)) {
                break
            } else { //undo
                path[i] = route1
                path[j] = route2
            }
        }

        val newDist = result().distance

        if (newDist >= currentDist && exp(-(newDist - currentDist) / heat) <= Random.nextDouble()) {
            path[i] = route1
            path[j] = route2
        }


        heat *= decay
        return result()
    }

    fun result(): Result {
        return Result(
            path.map { it.first } + path.last().second,
            path,
//            distance = path.sumOf { distances[it] ?: distances[Pair(it.second, it.first)]!! }
            distance = path.sumOf { calculateDistance(it.first, it.second) }
        )

//        val path = (0 until cities.size-1).map { Pair(multableCities[it], multableCities[it+1]) }
//        return Result(
//            multableCities,
//            path,
//            distance = path.sumOf { distances[it]!! }
//        )
    }

}

fun calculateDistance(city1:City, city2: City): Double {
    val dist1 = city1.x - city2.x
    val dist2 = city1.y - city2.y
    return sqrt(dist1*dist1 + dist2*dist2)
}

fun checkIfConnected(cities: List<City>, path: MutableList<Pair<City, City>>): Boolean { //dfs
    val map = cities.associateWith {false}.toMutableMap()
    val path2 = path.toMutableList()
    val first = path2.removeLast()
    map[first.first] = true
    map[first.second] = true

    for (i in (0..path.size)) {
        for (j in (0 until path2.size)) {
            if ( map[path2[j].first]!! || map[path2[j].second]!!) {
                map[path2[j].first] = true
                map[path2[j].second] = true
                path2.removeAt(j)
                break
            }
        }
    }
    return path2.isEmpty()
}

