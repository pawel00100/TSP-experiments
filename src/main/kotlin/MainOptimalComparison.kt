import Generator.EquallySpacedCityGenerator
import Generator.FullyConnectedConnectionGenerator
import display.CityDrawer
import solvers.*
import solvers.nn.EnsembleNearestNeighborSolver
import solvers.nn.NearestNeighborSolver
import solvers.sa.SimulatedAnnealingReversingFragmentsSolver
import model.City

fun main() {
    val n = 15

    val cityGenerator = EquallySpacedCityGenerator()
    val connectionGenerator = FullyConnectedConnectionGenerator()

    val cities = cityGenerator.generateCities(n)
    val distances = connectionGenerator.generateConnectionsWithDistances(cities)

    val heat = 5.0/128
    val decay = 0.99995
    val steps = 400_000

    createSolver(cities, distances, "NN", draw = true) { NearestNeighborSolver(cities, distances) }
    val result = createSolver(cities, distances, "ENN") { EnsembleNearestNeighborSolver(cities, distances) }
    createSolver(cities, distances, "Reversing SA") { SimulatedAnnealingReversingFragmentsSolver(result.orderedCities, distances, decay, heat, steps) }
    val betterResult = createSolver(cities, distances, "Ensemble Reversing SA", draw = true) {
        EnsembleSolver(cities, distances, 16) {
                _, distances -> SimulatedAnnealingReversingFragmentsSolver(result.orderedCities, distances, decay, heat, steps)
        }
    }
    createSolver(cities, distances, "Optimal", draw = true) { OptimalParallelSolver(cities, distances, betterResult.distance) }

}

private fun createSolver(
    cities: List<City>,
    distances: Map<Pair<City, City>, Double>,
    windowTitle: String,
    draw: Boolean = false,
    solver: () -> Solver
): Result {
    val start = System.currentTimeMillis()
    val heuristicSolver = solver()
    val result = heuristicSolver.calculateAll()
    val time = (System.currentTimeMillis() - start).toFloat()/1000
    println("$windowTitle: ${result.distance}    $time s")

    val bestPath = result.routes.toMutableList()
    if (draw) {
        CityDrawer(1.1, cities, distances.keys, bestPath, windowTitle).saveToPng(windowTitle)
    }
    return result
}