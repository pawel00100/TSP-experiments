package solvers.nn

import solvers.Solver
import solvers.Result
import solvers.sa.calculateDistance
import solvers.pmapBlocking
import model.City

class EnsembleNearestNeighborSolver(cities: List<City>, distances: Map<Pair<City, City>, Double>) : Solver(cities, distances) {

    override fun calculateAll(): Result {
        val neighborCache = cities.associateWith { c1 ->
            cities
                .map { c2 -> Pair(c2, calculateDistance(c1, c2)) }
                .sortedBy { it.second }
        }

        return cities.indices
            .pmapBlocking { NearestNeighborSolver(cities, distances, it, neighborCache ).calculateAll() }
            .minBy { it.distance }
    }
}