package solvers.nn

import solvers.Solver
import solvers.Result
import model.City

class NearestNeighborSolver(cities: List<City>, distances: Map<Pair<City, City>, Double>, val starting: Int, val cache: Map<City, List<Pair<City, Double>>>?) : Solver(cities, distances) {
    constructor(cities: List<City>, distances: Map<Pair<City, City>, Double>) : this(cities, distances, 0, null)

    override fun calculateAll(): Result {
        val explored = mutableListOf(cities[starting])
        val notExplored = cities.toMutableSet()
        notExplored.remove(explored[0])
        val exploredRoutes = mutableListOf<Pair<City, City>>()

        var distance = .0

        while (notExplored.isNotEmpty()) {
            val city = explored.last()
            val nextCity = if (cache != null) {
                cache[city]?.first { it.first in notExplored }?.first!!
            } else {
                notExplored.minBy { distances[Pair(city, it)]!! }
            }

            val pair = Pair(city, nextCity)
            notExplored.remove(nextCity)
            explored.add(nextCity)
            exploredRoutes.add(pair)
            distance += distances[pair]!!
        }

        return Result(explored, exploredRoutes, distance)
    }
}