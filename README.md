# CME2201_HW2_STRING_PPI_Network

A Java console application that models the human **protein-protein interaction (PPI) network** from the [STRING database](https://string-db.org/) as a custom weighted directed graph, and lets you explore it interactively (search proteins, check interactions, find the most confident interaction path, compute graph metrics, and run traversals). Built as Homework 2 for CME2201 (Data Structures).

## Overview

STRING provides two files for *Homo sapiens* (taxonomy ID `9606`):

- `9606.protein.info.v12.0.txt` — protein metadata (ID, preferred name, protein size, functional annotation)
- `9606.protein.links.v12.0.txt` — pairwise interaction scores ("combined confidence scores", 0–999) between proteins

This project loads both files, builds a **directed, weighted graph** (an interaction score is treated as an edge weight, and an interaction is only added if its score meets a user-supplied threshold), and provides a menu-driven interface to query and analyze the resulting network.

The graph and its algorithms (BFS, DFS, most-confident-path search) are implemented **from scratch** — no external graph libraries are used.

## Features

- **Custom directed graph ADT** (`DirectedGraph<T>`) built on an adjacency-list representation (`Vertex<T>`), supporting weighted edges and generic vertex labels
- **Threshold-based graph construction** — only interactions with a confidence score ≥ a chosen threshold are added as edges, so you can trade off network density vs. reliability
- **Protein lookup** — view a protein's preferred name, size, and functional annotation by its STRING ID
- **Interaction check** — test whether two proteins have a direct interaction edge
- **Most confident path** — finds the path between two proteins that maximizes total interaction confidence (shortest number of hops first, then highest cumulative score), using a priority-queue-based search, and prints the path with its total weight
- **Graph metrics** — vertex count, edge count, average degree, diameter (via BFS from every vertex), and reciprocity (fraction of edges that are mutual)
- **Traversals** — Breadth-First and Depth-First traversal starting from any protein
- Timing instrumentation for graph loading

## Project Structure

| File | Description |
|---|---|
| `Main.java` | Entry point — launches the interactive menu. |
| `Menu.java` | Handles user I/O: builds the graph from the STRING files, and dispatches menu commands to the graph operations. |
| `MenuInterface.java` | Interface describing the menu/application-level operations. |
| `DirectedGraph.java` | Core graph ADT: vertex/edge management, BFS, DFS, most-confident-path (priority-queue search), diameter, reciprocity. |
| `GraphInterface.java` | Combined graph interface (`BasicGraphInterface` + `GraphAlgorithmsInterface`). |
| `BasicGraphInterface.java` | Interface for basic graph operations (add vertex/edge, has-edge, counts, clear). |
| `GraphAlgorithmsInterface.java` | Interface for graph algorithms (BFS, DFS, most confident path). |
| `Vertex.java` / `VertexInterface.java` | Vertex representation with adjacency list, visited/cost/predecessor state used by the algorithms. |
| `Protein.java` | Data model for a single protein (ID, preferred name, size, annotation); equality/hashing is based on ID so it can be used directly as a graph vertex label. |
| `2022510101_ProjectII_Report.pdf` | Written project report accompanying the assignment. |

## Requirements

- Java 8+ (JDK)
- The following STRING data files present in the working directory at runtime:
  - `9606.protein.info.v12.0.txt`
  - `9606.protein.links.v12.0.txt`

  These are not included in the repository and can be downloaded from [STRING's download page](https://string-db.org/cgi/download) for organism *Homo sapiens* (taxonomy ID 9606).

## Usage

```bash
# Compile
javac *.java

# Run (make sure the two STRING data files are in the same directory)
java Main
```

On launch you'll see a menu; enter the corresponding number and follow the prompts:

```
Enter command:
> 1
```

| Command | Action |
|---|---|
| `1` | Load/build the graph — prompts for a confidence-score threshold, then reads both STRING files and constructs the graph |
| `2` | Search for a protein by ID — prints its preferred name, size, and annotation |
| `3` | Check interaction — enter two protein IDs to see if a direct edge exists between them |
| `4` | Find most confident path — enter two protein IDs to get the path between them with the highest cumulative confidence score |
| `5` | Get graph metrics — vertex/edge count, average degree, diameter, reciprocity |
| `6` | Get traversals — enter a protein ID to print its BFS and DFS traversal order |

Note: commands `2`–`6` other than protein search generally require the graph to be loaded first (command `1`).

## Notes

This project was built for coursework purposes to practice implementing a graph ADT (adjacency-list based, directed, weighted) from scratch and applying classic graph algorithms (BFS, DFS, shortest/most-confident path via priority queue) to a real-world biological network dataset.
