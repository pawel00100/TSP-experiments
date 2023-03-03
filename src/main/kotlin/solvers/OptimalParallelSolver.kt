package solvers

import model.City
import java.util.concurrent.atomic.AtomicReference

class OptimalParallelSolver(cities: List<City>, distances: Map<Pair<City, City>, Double>, val bestSoFar: Double = Double.MAX_VALUE) : Solver(cities, distances) {

    override fun calculateAll(): Result {

        val bestSoFarRef = AtomicReference(bestSoFar)
        val best = kPermutations(cities.indices.toList(), 3).pmapBlocking { ids ->
            val startingCities = ids.map { cities[it] }
            findBest(
                startingCities,
                startingCities.neighborPairs().sumOf { distances[it]!! },
                cities.size,
                cities,
                distances,
                bestSoFarRef,
            )
        }
        .minBy { it.second }

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


fun <T> combinations(items: List<T>, choose: Int): List<List<T>> {
    if (choose == 0) {
        return listOf(emptyList())
    }
    if (items.isEmpty()) {
        return emptyList()
    }
    val head = items.first()
    val tail = items.drop(1)
    val withoutHead = combinations(tail, choose)
    val withHead = combinations(tail, choose - 1).map { it + head }
    return withoutHead + withHead
}

fun <T> kPermutations(items: List<T>, k: Int): List<List<T>> {
    if (k == 1) {
        return items.map { listOf(it) }
    }
    val perms = mutableListOf<List<T>>()
    for (i in items.indices) {
        val item = items[i]
        val rest = items.take(i) + items.drop(i + 1)
        for (perm in kPermutations(rest, k - 1)) {
            perms.add(listOf(item) + perm)
        }
    }
    return perms
}


private fun <T> List<T>.neighborPairs() = (0 until this.size - 1)
    .map { this[it] to this[it+1] }

fun main() {
    println(kPermutations((0..5).toList(), 3))
}