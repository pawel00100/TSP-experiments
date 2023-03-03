package solvers

import kotlinx.coroutines.*
import model.City
import kotlin.random.Random

class EnsembleSolver(cities: List<City>, distances: Map<Pair<City, City>, Double>, val n: Int, val solver: (List<City>, Map<Pair<City, City>, Double>)-> Solver) : Solver(cities, distances) {
    override fun calculateAll(): Result {
        return (1..n)
            .map { solver(cities.scrambled(), distances) }
            .pmapBlocking { it.calculateAll() }
            .minBy { it.distance }
    }
}

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}
fun <A, B> Iterable<A>.pmapBlocking(f: (A) -> B): List<B> = runBlocking(Dispatchers.Default) {
    pmap(f)
}



private fun <T> List<T>.scrambled(): List<T> {
    val result = this.toMutableList()
    for (i in result.size - 1 downTo 1) {
        val j = Random.nextInt(i + 1)
        val temp = result[i]
        result[i] = result[j]
        result[j] = temp
    }
    return result
}