# KuantumCircuitSim

![EPR](https://raw.githubusercontent.com/DEDZTBH/KuantumCircuitSim/master/EPR.gif)
</br>
<sub>Making an EPR pair</sub>

A Fast & Lightweight Quantum Circuit Simulator & Analyzer implemented in Kotlin.

Quantum Computing is no coin-flipping!

Another 15-459 Assignment(-ish).

There are 2 versions with identical usage implemented with different libraries:    
- EJML version: No native dependencies, super lightweight, good for small input (small N, like <4)
- JBLAS version: Accelerated with BLAS, good for big input (big N)
    - JBLAS comes with default BLAS library for major OS's so it works right out of the box. For extreme performance, you can build JBLAS with custom BLAS library (like OpenBlas, ATLAS, cuBLAS) and put the library file(s) under library load path (for example, current working directory). See [JBLAS github page](https://github.com/jblas-project/jblas) for more detail.
    
### Table of Contents
- [KuantumCircuitSim](#kuantumcircuitsim)
    - [Table of Contents](#table-of-contents)
  - [Features](#features)
    - [Simulate A Quantum Circuit](#simulate-a-quantum-circuit)
    - [N-Qubit System](#n-qubit-system)
    - [Generate Circuit Matrix](#generate-circuit-matrix)
    - [Parallelism](#parallelism)
    - [Acceleration with BLAS](#acceleration-with-blas)
  - [Usage](#usage)
  - [Operators](#operators)
  - [Commands](#commands)
  - [Note](#note)
    - [Notation](#notation)
    - [Matrix File Format](#matrix-file-format)

## Features
#### Simulate A Quantum Circuit
Of course, it says "Quantum Circuit Simulator"! See below for all supported gates and operations, including measurement!

#### N-Qubit System
You can use any number of qubits you want! Just make sure your computer is powerful enough if N is large. Time/Space complexity is exponential on classical computers!

#### Generate Circuit Matrix
The power of Linear Algebra! You can save the matrix after the simulation. Next time, just load it and one matrix multiplication gets you the result of running the entire circuit!

#### Parallelism
CPU0 is not alone! Concurrent command processing provides significantly performance boost on multi-core machines, especially for big N and large number of commands!
- Note that concurrent implementation isn't always faster than sequential (with -s flag). Sequential is often faster in scenarios such as:
    - Small N (like <4)
    - Intensive use of multiple core causes thermal throttling (like when I run JBLAS version on my laptop)

#### Acceleration with BLAS
Now with JBLAS! EJML is great for small matrices but for not big ones (matrices have size 2^N x 2^N). Thus I added a JBLAS version that uses hardware-optimized BLAS library and can really speed things up!

## Usage

```
Usage: java -jar Kuantum.jar options_list
Arguments: 
    input -> Input file { String }
    operator -> Operator name { String }
    N [5] -> Number of qubits (optional) { Int }
Options: 
    --output, -o [] -> Output file to save circuit matrix (csv) if specified { String }
    --input_matrix, -m [] -> Read circuit matrix (csv) as initial matrix if specified { String }
    --no_t, -q [false] -> Do not print circuit matrix in commandline after simulation if present 
    --sequential, -s [false] -> Use sequential instead of concurrent implementation if present 
    --init_state, -i [] -> Read custom initial joint state from csv if specified { String }
    --binary_matrix, -b [false] -> Use binary format instead of csv for read/save circuit matrix if present (EJML version only) 
    --disable_cache, -d [false] -> Disable cache to save memory (same gates will need to be recomputed every time) 
    --help, -h -> Usage info 
```

example: ```java -jar KuantumCircuitSim.jar example.txt Tester 3 -o output.csv -i init.csv```

Where example.txt contains list of commands and circuit matrix (csv) will be stored to output.csv and initial state is read from init.csv

## Operators

TFinder: Generate the circuit's matrix and print it.

Tester: Similar to TFinder but also run |00..0> (or custom initial state with -i option) through circuit and print result.
- Supports Measure, MeasAll, MeasOne

AllInit: Similar to TFinder but also run every possible initial states (2^N of them) through circuit and print results.

## Commands
i, j, k are indices of qubit. (0-indexed, left-to-right)

In the input file, each command should be separated by new line or space.

Commands are case-insensitive.

##### Single-Qubit Gates
- Not i
- Hadamard i
    + You can also use "H" instead of "Hadamard"
- Y i
- Z i
- S i
- T i
- TDag i
- SqrtNot i
- SqrtNotDag i
- Rot i angle
    + Rotate qubit counterclockwise by angle (in radian)
- R i phi
    + Phase shift by phi (mapping |1> to e^(i*phi)|1>)

##### Parallel Mode
You can enclose the above single-qubit gates that can run in parallel between "ParStart" and "ParEnd" commands. This can speed up the simulation a lot. Make sure you are using only above single-qubit gates and no two gates are acting on the same qubit.

##### Multi-Qubit Gates
Note: i controls j (and k, if present)
- CNot i j
- Swap i j
- CZ i j
    + Controlled Z gate
- SqrtSwap i j
    + Not implemented yet
- CR i j phi
    + Controlled phase shift
- CCNot i j k
- CSwap i j k

##### Measurement (Only works when using Tester)
- Measure n
    + "Magically" measures all qubit state n times in standard basis and print results
    + Will not change qubit state or circuit matrix
    + Not saved in circuit matrix
- MeasAll
    + Measure all qubits in standard basis and print measure result
    + Will cause qubit state to collapse
    + Will make circuit matrix unavailable to print or save
- MeasOne i
    + Measure one qubits in standard basis and print measure result
    + Will cause qubit state to collapse
    + Will make circuit matrix unavailable to print or save
    
## Note

### Notation

In a joint state, qubits are represented from left to right. For example, |100> means the first qubit (index 0) is |1> and the second (index 1) and third (index 2) are |0>.

### Matrix File Format

A matrix in CSV format is represented with alternating real and imaginary parts in row-major fashion.
For example, the matrix
```
[[1+0i, 0+1i],
 [0-1i, 1+0i]]
```
is stored in CSV file like
```
1,0,0,1
0,-1,1,0
```
(EJML version only) Alternatively, circuit matrix can be saved/loaded in Java binary format (with -b option). It might be faster to save/load but it is not human readable. In case you want to read it, it is a `org.ejml.data.ZMatrixRMaj`.
