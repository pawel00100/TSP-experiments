package solvers

import solvers.util.ReverseFragmentMutator
import io.jenetics.*
import io.jenetics.engine.*
import io.jenetics.util.ISeq
import model.City
import java.util.function.Function


class GeneticSolver(cities: List<City>, distances: Map<Pair<City, City>, Double>, val population: Int, val steps: Int) : Solver(cities, distances) {
    val multableCities = cities.toMutableList()

    fun ff(g: Genotype<AnyGene<List<City>>>): Int {
        return 0
    }

    fun ff2(seq: ISeq<City>): Double = result(seq.asList()).distance

    inner class TSPProblem : Problem<ISeq<City>, EnumGene<City>, Double> {

        override fun fitness(): Function<ISeq<City>, Double> {
            return Function { a: ISeq<City> -> ff2(a) }
        }

        override fun codec(): Codec<ISeq<City>, EnumGene<City>> {
            return Codecs.ofPermutation(ISeq.of(cities))
        }
    }

    override fun calculateAll(): Result {

        val engine = Engine
            .builder(TSPProblem())
            .optimize(Optimize.MINIMUM)
            .populationSize(population)
//            .executor(Executors.newFixedThreadPool(16))
            .alterers(
                ReverseFragmentMutator(0.2),
                SwapMutator(0.2),
                PartiallyMatchedCrossover(0.35)
            )
            .build()

        val result = engine.stream()
            .limit(steps.toLong())
            .collect(EvolutionResult.toBestGenotype())

        val newCities = result.chromosome().genes().map { cities[it.alleleIndex()] }
        multableCities.clear()
        multableCities.addAll(newCities)

        return result()
    }

    fun result(): Result {
        val path = (0 until cities.size - 1).map { Pair(multableCities[it], multableCities[it + 1]) }
        return Result(
            multableCities,
            path,
            distance = path.sumOf { distances[it]!! }
        )
    }


    fun result(cities2: List<City>): Result {
        val path = (0 until cities.size - 1).map { Pair(cities2[it], cities2[it + 1]) }
        return Result(
            cities2,
            path,
            distance = path.sumOf { distances[it]!! }
        )
    }
}

private fun <G : Gene<*, G>> Chromosome<G>.genes(): List<G> {
    val genes = mutableListOf<G>()
    (0 until length()).forEach {
        genes.add(get(it))
    }
    return genes
}
