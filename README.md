# TSP-experiments

This is a simple projects that evaluates some TSP solvers:

- Tree state space search optimal solvers
- Nearest-neighbor solvers
- Simulated annealing solvers
- Genetic algorithm

Outcome of the simple nearest-neighbor heuristic:

<img height="400" src="readme_media\NN_100.png" width="400"/>

Ensemble nearest-neighbor followed by ensemble simulated annealing:

<img height="400" src="readme_media\SA_100.png" width="400"/>

Results show that some SA implementations can reach optimal outcomes or very close ones. Meanwhile, chosen popular genetic library does not work satisfyingly.

## Tree state space search optimal solver

- Brute force
- Includes branch-and-bound that can optionally be set to result from chosen heuristic
- Serial and multi-threaded parallel variants
- For 15-16 cities time grows to around one minute

## Nearest-neighbor solver

## Ensemble Nearest-neighbor solver

It is repeated nearest-neighbor for each possible staring city. Implementation does support multiple cores and scales
well.

## Simulated annealing solvers

It has three implementations with different path modification approaches.
Main result is that this method does not work when it is started with low quality random state. The best outcomes happen
when it is supplied with result of simple construction heuristic, such as nearest-neighbor.

### Swapping two neighbors on a path

<img src="readme_media\graph1a.png"/>

<img src="readme_media\graph1b.png"/>

It has low convergence rate.

### Reversing path fragments of any length

<img src="readme_media\graph2a.png"/>

<img src="readme_media\graph2b.png"/>

This method reaches stable state more quickly and with better results.

There has second implementation, Swapping pairs of paths, which de facto has same effect but has significantly lower
performance

### Ensemble Simulated annealing

Due to random nature of SA repeated runs of the algorithm likely will find better result than single run.
It is an embarrassingly parallel problem so it can easily scale to large number of cores, albeit with diminishing
results.

## Genetic algorithm

Jenetics library was used for the implementation. Library has high overhead with such simple problem and has low
performance of mutations.
Results are significantly worse than simulated annealing.

## Areas for improvements:

- Add ant colony optimization
- Research sparse, planar graphs
