package solvers.util

import io.jenetics.Chromosome
import io.jenetics.Gene
import io.jenetics.Mutator
import io.jenetics.MutatorResult
import io.jenetics.util.MSeq
import java.util.random.RandomGenerator
import kotlin.math.absoluteValue


class ReverseFragmentMutator<G : Gene<*, G>?, C : Comparable<C>?>(probability: Double = DEFAULT_ALTER_PROBABILITY) : Mutator<G, C>(probability) {
    override fun mutate(chromosome: Chromosome<G>, p: Double, random: RandomGenerator): MutatorResult<Chromosome<G>> {
        return if (random.nextDouble() < probability() && chromosome.length() > 1) {
            val genes = MSeq.of(chromosome)

            val swapWidth = repeatUntilRangeMatched(2 until chromosome.length()) {
                (random.nextGaussian(0.0, chromosome.length().toDouble()/3.5).absoluteValue).toInt()
            }
            val start = random.nextInt(chromosome.length() - swapWidth)

            (0 until swapWidth/2).forEach {
                genes.swap(start + it, start + swapWidth - it - 1)
            }


            MutatorResult(
                chromosome.newInstance(genes.toISeq()),
                swapWidth
            )
        } else {
            MutatorResult(chromosome, 0)
        }
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

