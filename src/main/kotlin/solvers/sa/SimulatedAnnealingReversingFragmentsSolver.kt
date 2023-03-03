package solvers.sa

import solvers.Solver
import solvers.Result
import model.City
import util.calculateDistance2
import java.util.ArrayList
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.absoluteValue
import kotlin.math.exp
import kotlin.random.Random

class SimulatedAnnealingReversingFragmentsSolver(cities: List<City>, distances: Map<Pair<City, City>, Double>, val decay: Double, var heat: Double = 100.0, val steps: Int) : Solver(cities, distances) {
    var multableCities = cities.toMutableList()
    val random = ThreadLocalRandom.current()

    override fun calculateAll(): Result {
        (1..steps).forEach {
            step()
        }

        return result()
    }

    fun step(): Result {
        var currentResult = result()
        val currentDist = currentResult.distance

        val swapWidth = repeatUntilRangeMatched(2 until cities.size) {
            (random.nextGaussian(0.0, cities.size.toDouble() / 6).absoluteValue).toInt()
        }
        val start = random.nextInt(cities.size - swapWidth)

        val newList = mutableListWithCapacity<City>(cities.size)

        (0 until start).forEach {
            newList.add(multableCities[it])
        }

        (0 until swapWidth).forEach {
            newList.add(multableCities[start + swapWidth - it - 1])
        }

        (start + swapWidth until multableCities.size).forEach {
            newList.add(multableCities[it])
        }

        val newResult = result(newList)
        val newDist = newResult.distance

        if (newDist < currentDist || exp(-(newDist - currentDist) / heat) > Random.nextDouble()) {
            multableCities = newList
            currentResult = newResult
        }

        heat *= decay
        return currentResult
    }

    fun result(): Result {
        val path = (0 until cities.size-1).map { Pair(multableCities[it], multableCities[it+1]) }
        return Result(
            multableCities,
            path,
            distance = path.sumOf { calculateDistance2(it.first, it.second) }
        )
    }

    fun result(cities2: List<City>): Result {
        val path = (0 until cities.size - 1).map { Pair(cities2[it], cities2[it + 1]) }
        return Result(
            cities2,
            path,
            distance = path.sumOf { calculateDistance2(it.first, it.second) }
        )
    }
}



fun repeatUntilRangeMatched(range: IntRange, supplier: () -> Int): Int {
    while (true) {
        val generated = supplier()
        if (generated in range){
            return generated
        }
    }
}

fun <T> mutableListWithCapacity(capacity: Int): MutableList<T> =
    ArrayList(capacity)