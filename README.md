# KuantumCircuitSim

A Lightweight Quantum Circuit Simulator & Analyzer implemented in Kotlin.

Quantum Computing is no coin-flipping!

Another 15-459 Assignment(-ish).

### Note on Notation

In a joint state, qubits are represented from left to right. For example, |100> means the first qubit is |1> and the second and third are |0>.

## Usage

java -jar xxx.jar \<input file> \<operator> \<number of qubits (default 5)>

example: java -jar xxx.jar example.txt Tester 3

Where example.txt contains list of commands

## Operators

Tester: Run a simulation on circuit with initial state |00...0>, then print the circuit's matrix and final state.

TFinder: Similar to Tester, but only print the circuit's matrix.

AllInit: Similar to Tester, but run for each possible initial state (2^N of them).

## Commands

- Not i
- Hadamard i
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
- SqrtSwap i j (Not implemented yet)
- Rot i deg
    + Rotate qubit counterclockwise by degree, not rad 
- Measure n
    + Measures the joint qubit state n times using standard basis
    + Only works when using Tester
    + Will end command read