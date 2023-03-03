package solvers

import model.City
import java.util.concurrent.atomic.AtomicReference

class OptimalSolver(cities: List<City>, distances: Map<Pair<City, City>, Double>, val bestSoFar: Double = Double.MAX_VALUE) : Solver(cities, distances) {

    override fun calculateAll(): Result {

        val best = findBest(cities, distances, bestSoFar)

        return Result(best.first, best.second)
    }

    fun findBest(
        allCities: List<City>,
        distances: Map<Pair<City, City>, Double>,
        bestSoFar: Double,
    ) = findBest(listOf(), 0.0, allCities.size, allCities, distances, AtomicReference(bestSoFar))

    fun findBest(
        citiesSoFar: List<City>,
        distanceSoFar: Double,
        n: Int,
        allCities: List<City>,
        distances: Map<Pair<City, City>, Double>,
        bestSoFar: AtomicReference<Double>
    ): Pair<List<City>, Double> {
        if (citiesSoFar.size == n) {
            if (distanceSoFar < bestSoFar.get() + 1e-6) {
                bestSoFar.set(distanceSoFar)
            }
            return Pair(citiesSoFar, distanceSoFar)
        }

        if (distanceSoFar > bestSoFar.get()) {
            return Pair(emptyList(), Double.MAX_VALUE)
        }

        val citiesToExplore = allCities.asSequence().filter { it !in citiesSoFar }
        return citiesToExplore
            .map {
                findBest(
                    citiesSoFar + it,
                    distanceSoFar + if (citiesSoFar.size >= 1) distances[Pair(citiesSoFar.last(), it)]!! else 0.0,
                    n,
                    allCities,
                    distances,
                    bestSoFar,
                )
            }
            .minBy { it.second }
    }
}