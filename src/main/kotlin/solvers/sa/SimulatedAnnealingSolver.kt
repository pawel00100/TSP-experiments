package solvers.sa

import solvers.Solver
import solvers.Result
import model.City
import kotlin.math.exp
import kotlin.random.Random

class SimulatedAnnealingSolver(cities: List<City>, distances: Map<Pair<City, City>, Double>, val decay: Double, var heat: Double = 100.0, val steps: Int) : Solver(cities, distances) {
    val multableCities = cities.toMutableList()

    override fun calculateAll(): Result {
        (1..steps).forEach {
            step()
        }

        return result()
    }

    private fun step(): Result {
        val currentDist = result().distance

        val i = Random.nextInt(cities.size)
        val j = Random.nextInt(cities.size)
        swap(multableCities, i, j)

        val newDist = result().distance

        if (newDist >= currentDist && exp(-(newDist-currentDist)/heat) <= Random.nextDouble()) {
            swap(multableCities, i, j) //undo
        }


        heat *= decay
        return result()
    }

    fun result(): Result {
        val path = (0 until cities.size-1).map { Pair(multableCities[it], multableCities[it+1]) }
        return Result(
            multableCities,
            path,
            distance = path.sumOf { distances[it]!! }
        )
    }

}

private fun <T> swap(list: MutableList<T>, i: Int, j: Int) {
    val city1 = list[i]
    val city2 = list[j]
    list[i] = city2
    list[j] = city1
}
