# KuantumCircuitSim

A Lightweight Quantum Circuit Simulator & Analyzer implemented in Kotlin.

Quantum Computing is no coin-flipping!

Another 15-459 Assignment(-ish).

### Note on Notation

In a joint state, qubits are represented from left to right. For example, |100> means the first qubit is |1> and the second and third are |0>.

## Usage

```
Usage: java -jar KuantumCircuitSim.jar options_list
Arguments: 
    input -> Input file { String }
    operator -> Operator name { String }
    N [5] -> Number of qubits (optional) { Int }
Options: 
    --output, -o [] -> Output file to save circuit matrix (binary) if specified { String }
    --input_matrix, -m [false] -> Read input file as circuit matrix (binary) instead of commands if present 
    --help, -h -> Usage info 
```

example: ```java -jar KuantumCircuitSim.jar example.txt Tester 3 -o output.data```

Where example.txt contains list of commands and circuit matrix (binary) will be stored to output.data

## Operators

TFinder: Generate the circuit's matrix and print it.

Tester: Similar to TFinder but also run |00..0> through circuit and print result. (Supports Measure command)

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
    + Measures the joint qubit state n times using the standard basis
    + Only works when using Tester