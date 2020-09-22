# KuantumCircuitSim

A quantum circuit simulator implemented in Kotlin

Another 15-459 Assignment

## Usage

java -jar xxx.jar \<input file> \<operator> \<number of qubits (default 5)>

example: java -jar xxx.jar example.txt Tester 3

Where example.txt contains list of commands

## Available Operators

Tester: Run a simulation on circuit with initial state |00...0>, then print the circuit's matrix and final state.

TFinder: Similar to Tester, but only print the circuit's matrix.

AllInit: Similar to Tester, but run for each possible initial state (2^N of them).

## Available Commands

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
    + Rotate qubit clockwise by degree, not rad 
- Measure n
    + Measures the joint qubit state n times using standard basis
    + Only works when using Tester
    + Will end command read