import Generator.EquallySpacedCityGenerator
import Generator.FullyConnectedConnectionGenerator
import display.CityDrawer
import solvers.*
import solvers.nn.EnsembleNearestNeighborSolver
import solvers.nn.NearestNeighborSolver
import solvers.sa.SimulatedAnnealingReversingFragmentsSolver
import solvers.sa.SimulatedAnnealingSolver
import solvers.sa.SimulatedAnnealingSwappingPathsSolver
import model.City

fun main() {
    val n = 100

    val cityGenerator = EquallySpacedCityGenerator()
    val connectionGenerator = FullyConnectedConnectionGenerator()

    val cities = cityGenerator.generateCities(n)
    val distances = connectionGenerator.generateConnectionsWithDistances(cities)

    val heat = 5.0/128
    val decay = 0.99995
    val steps = 400_000

    createSolver(cities, distances, "NN", draw = true) { NearestNeighborSolver(cities, distances) }
    val result = createSolver(cities, distances, "ENN") { EnsembleNearestNeighborSolver(cities, distances) }
    createSolver(cities, distances, "Basic SA") { SimulatedAnnealingReversingFragmentsSolver(result.orderedCities, distances, decay, heat, steps) }
    createSolver(cities, distances, "Path SA") { SimulatedAnnealingReversingFragmentsSolver(result.orderedCities, distances, decay, heat, steps) }
    createSolver(cities, distances, "Reversing SA") { SimulatedAnnealingReversingFragmentsSolver(result.orderedCities, distances, decay, heat, steps) }

    createSolver(cities, distances, "Ensemble Reversing SA", draw = true) {
        EnsembleSolver(cities, distances, 14) {
            cities, distances -> SimulatedAnnealingReversingFragmentsSolver(result.orderedCities, distances, decay, heat, steps)
        }
    }
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