# KuantumCircuitSim

A Fast & Lightweight Quantum Circuit Simulator & Analyzer implemented in Kotlin.

Quantum Computing is no coin-flipping!

Another 15-459 Assignment(-ish).

## Features
### Simulate A Quantum Circuit
Of course, it says "Quantum Circuit Simulator"! See below for all supported gates and operations, including measurement!

### N-Qubit System
You can use any number of qubits you want! Just make sure your computer is powerful enough if N is large. Time complexity is exponential on classical computers!

### Generate Matrix of Circuit
The power of Linear Algebra! You can save the matrix after the simulation. Next time, just load it and one matrix multiplication gets you the result of running the entire circuit!

### Parallelism
CPU0 is not alone! Concurrent command processing (with -c option) provides significantly performance boost on multi-core machines, especially for large number of commands!

## Usage

```
Usage: java -jar KuantumCircuitSim.jar options_list
Arguments: 
    input -> Input file { String }
    operator -> Operator name { String }
    N [5] -> Number of qubits (optional) { Int }
Options: 
    --output, -o [] -> Output file to save circuit matrix (binary) if specified { String }
    --input_matrix, -m [] -> Read circuit matrix (binary) as initial matrix if specified, use an empty file for input if no extra commands { String }
    --no_t, -q [false] -> Do not print circuit matrix in commandline after simulation if present 
    --concurrent, -c [false] -> Use concurrent implementation if present (recommended on multi-core machines) 
    --init_state, -i [] -> Read custom initial joint state from csv if specified, first column is real and second column is imaginary { String }
    --help, -h -> Usage info 
```

example: ```java -jar KuantumCircuitSim.jar example.txt Tester 3 -o output.data -c -i init.csv```

Where example.txt contains list of commands and circuit matrix (binary) will be stored to output.data and initial state is read from init.csv

## Operators

TFinder: Generate the circuit's matrix and print it.

Tester: Similar to TFinder but also run |00..0> through circuit and print result.
- Non-concurrent Tester supports Measure, MeasAll, MeasOne

AllInit: Similar to TFinder but also run every possible initial states (2^N of them) through circuit and print results.

## Commands
i, j, k are indicies of qubit. (0-indexed)

In the input file, each command should be separated by new line or space.

Commands are case-insensitive.

- Not i
- Hadamard i
    + You can also use "H" instead of "Hadamard"
- CNot i j
- Swap i j
- CCNot i j k
- CSwap i j k
- Z i
- CZ i j
    + Controlled Z gate
- S i
- T i
- TDag i
- SqrtNot i
- SqrtNotDag i
- SqrtSwap i j
    + Not implemented yet
- Rot i deg
    + Rotate qubit counterclockwise by degree, not rad
- Measure n
    + Measures all qubit state n times in standard basis and print results
    + Will not change qubit state or circuit matrix
    + Only works when using non-concurrent Tester
    + Not saved in circuit matrix
- MeasAll
    + Measure all qubits in standard basis and print measure result
    + Will cause qubit state to collapse
    + Will make circuit matrix unavailable to print or save
    + Only works when using non-concurrent Tester
- MeasAll i
    + Measure one qubits in standard basis and print measure result
    + Will cause qubit state to collapse
    + Will make circuit matrix unavailable to print or save
    + Only works when using non-concurrent Tester
    
## Note on Notation

In a joint state, qubits are represented from left to right. For example, |100> means the first qubit is |1> and the second and third are |0>.