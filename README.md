# Assignment 3 — Graph Algorithms

## Overview

This Java project implements minimum spanning trees, topological ordering, strongly connected components and shortest paths. It compares their performance on reproducible graphs of different sizes.

## Implementation

The graph uses adjacency lists backed by dynamically resized arrays. Vertices have integer IDs, and edges store their source, destination and weight. Undirected connections are stored in both directions. Graph storage is O(V + E).

| Algorithm | Approach |
|---|---|
| DSU | Parent and size arrays, path compression, union by size |
| Kruskal | Sort edges by weight and use DSU to avoid cycles; reset DSU for each run |
| Prim | Grow a tree using a priority queue of candidate edges |
| Kahn | Process zero-indegree vertices using a priority queue; detect cycles if fewer than V vertices are processed |
| Kosaraju | Record DFS finishing order, reverse the graph, then perform a second DFS pass |
| Dijkstra | Relax edges using a priority queue of tentative distances; store predecessors and skip stale entries |

Dijkstra uses `long` distances and handles unreachable vertices. Negative weights are rejected because they invalidate its greedy shortest-path guarantee.

## Complexity Analysis

V is the number of vertices; E is the number of logical edges. Bounds exclude printing and reconstruction of all paths.

| Algorithm / operation | Time | Additional space |
|---|---|---|
| DSU initialization | O(V) | O(V) |
| DSU find / union | O(α(V)) amortized | O(log V) recursive stack; O(V) total DSU storage |
| Kruskal | O(V + E log(E+1) + Eα(V)) | O(V + E) |
| Prim | O(V + E log(E+1)) | O(V + E) |
| Kahn | O(E + V log(V+1)) | O(V) |
| Kosaraju | O(V + E) | O(V + E) |
| Dijkstra | O(V + E log(E+1)) | O(V + E) |

These bounds assume adjacency lists and binary-heap priority queues. Prim and Dijkstra can retain O(E) queue entries; on connected simple graphs their time is commonly written O(E log V). Kahn uses a priority queue rather than a FIFO queue, so its implementation is not strictly O(V + E).

Path compression shortens parent paths, while union by size prevents unnecessarily tall trees. Together they make DSU operations nearly constant amortized time. More vertices increase initialization and traversal work; more edges increase scans, sorting and queue operations.

## Experimental Setup

| Size | Vertices | Edges |
|---|---:|---:|
| Small | 10 | 30 |
| Medium | 100 | 500 |
| Large | 1,000 | 5,000 |

The reported run used `Random(56)`, three warm-ups and five measured repetitions. Execution time was measured with `System.nanoTime()` and averaged. Graph generation, correctness checks and printing were excluded from timing.

- **Connected undirected graphs:** Generate a spanning tree first, then add random edges. Used for Kruskal, Prim and Dijkstra from vertex 0.
- **DAGs:** Generate edges only from smaller to larger vertex IDs. Used for Kahn and Kosaraju.
- **Cyclic graphs:** Generate a directed ring through all vertices, then add random edges. Used for Kahn and Kosaraju.

Weights range from 1 to 100. Duplicate weights are allowed; generated graphs exclude duplicate edges and self-loops. E counts each undirected connection once. Kruskal and Prim use the same graph.

Timing includes Kruskal's edge collection, sorting and DSU initialization, Kosaraju's graph reversal, and Dijkstra's weight validation. It excludes Dijkstra's path printing. There are 21 configurations and 105 measured runs.

**Environment:** Java 26.0.1, OpenJDK 64-Bit Server VM, Windows 11 amd64, Ryzen 7 7435hs 8 cores 16 threads.

## Results

### Average execution time (milliseconds)

| Algorithm | Graph | 10 vertices | 100 vertices | 1,000 vertices |
|---|---|---:|---:|---:|
| Kruskal | Connected | 0.05926 | 0.57094 | 3.27060 |
| Prim | Connected | 0.05070 | 0.11602 | 1.95808 |
| Kahn | DAG | 0.02090 | 0.16796 | 0.66834 |
| Kahn | Cyclic | 0.00754 | 0.03040 | 0.05894 |
| Kosaraju | DAG | 0.06456 | 0.21180 | 1.00126 |
| Kosaraju | Cyclic | 0.03996 | 0.69828 | 0.55298 |
| Dijkstra | Connected | 0.05428 | 0.22748 | 1.51122 |

### Recorded edge examinations

| Algorithm | Graph | Small | Medium | Large |
|---|---|---:|---:|---:|
| Kruskal | Connected | 150 | 2,500 | 25,000 |
| Prim | Connected | 60 | 1,000 | 10,000 |
| Kahn | DAG | 60 | 1,000 | 10,000 |
| Kahn | Cyclic | 30 | 500 | 5,000 |
| Kosaraju | DAG | 30 | 500 | 5,000 |
| Kosaraju | Cyclic | 30 | 500 | 5,000 |
| Dijkstra | Connected | 120 | 2,000 | 20,000 |

Counters measure explicit edge examinations, not internal sorting or heap comparisons. Kruskal includes its two collection passes; Dijkstra includes validation. The supplied Kosaraju counts equal E and cover only part of its work: counting both DFS passes and reversal would give 3E. These recorded values are retained rather than presented as complete operation counts.

| Kruskal DSU metric | Small | Medium | Large |
|---|---:|---:|---:|
| Find calls, including recursion | 131 | 2,193 | 22,227 |
| Union calls | 9 | 99 | 999 |

![Execution time vs vertices](results/plots/time_vs_vertices.png)

![Execution time vs edges](results/plots/time_vs_edges.png)

Detailed data: [raw runs](results/tables/raw.csv), [summary](results/tables/summary.csv), [environment](results/environment.txt).

## Comparison and Discussion

Prim was faster than Kruskal at every tested size. At 1,000 vertices, Prim was approximately **1.67 times faster**. Kruskal collects and sorts all edges and performs DSU operations, while Prim selects candidate edges incrementally. This comparison applies to these implementations and inputs, not every graph.

Kosaraju's DAG timings increased with graph size, consistent with growing traversal work. Its cyclic medium case had one unusually slow run of 2.9694 ms, raising the average above the large case. The other four medium runs were 0.0903–0.1783 ms. JVM activity or system scheduling may contribute, but the data does not identify the cause.

Kahn was faster on cyclic graphs because the generated ring gives every vertex a positive indegree. After its initial scan, the queue is empty and it immediately reports a cycle. On DAGs it processes all vertices and edges.

Dijkstra's runtime and edge examinations increased with graph size. Queue operations, allocation and successful distance updates also affect runtime. Likewise, equal asymptotic bounds do not imply equal execution times because implementations perform different operations with different constant costs.

Five-run averages reduce some noise but do not eliminate JVM and scheduling effects. Both V and E increase together, so the two plots do not isolate their independent effects. DSU optimization benefits are explained theoretically; no unoptimized DSU baseline was measured.

## Testing

`Tests.run()` covers DSU initialization, resizing, union by size and path compression; directed and undirected edge storage; known MST weights and repeated runs; equal weights and multiple valid MSTs; cycles and SCC partitions; shortest distances and predecessor paths; and invalid inputs.

Boundary cases include empty graphs, single vertices, disconnected graphs, isolated and unreachable vertices, self-loops, parallel edges, zero-weight cycles and a 1,000-vertex chain. MST tests for disconnected graphs should expect `IllegalStateException` in the updated implementation.

Correctness checks compare Kruskal and Prim weights, validate every DAG edge against its ordering, ensure every vertex belongs to exactly one expected SCC, and verify Dijkstra paths. Before timing, the experiment runner additionally compares Dijkstra distances with a Bellman-Ford reference.


## Conclusion

Kruskal and Prim solve minimum spanning tree problems, Kahn orders acyclic dependencies, Kosaraju identifies strongly connected components, and Dijkstra finds shortest paths with non-negative weights. The measurements generally show increasing runtime as graphs grow. Prim performed better than Kruskal in this run, while the SCC outlier highlights the importance of repeated measurements and careful interpretation.
