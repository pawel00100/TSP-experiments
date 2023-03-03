package solvers

import model.City

data class Result(val orderedCities: List<City>, val routes: List<Pair<City, City>>, val distance: Double) {
    constructor(orderedCities: List<City>, distance: Double) : this(
        orderedCities,
        orderedCities.neighborPairs(),
        distance
    )
}

abstract class Solver(val cities: List<City>, val distances: Map<Pair<City, City>, Double>) {
    abstract fun calculateAll(): Result
}

private fun <T> List<T>.neighborPairs() = (0 until this.size - 1)
    .map { this[it] to this[it + 1] }