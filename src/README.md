# KuantumCircuitSim

A quantum circuit simulator implemented in Kotlin

Another 15-459 Assignment

## Usage

java -jar xxx.jar \<input file> \<operator> \<number of qubits (default 5)>

example: java -jar xxx.jar example.txt Tester 3

Where example.txt contains list of commands

## Available Operators

Tester: Run a simulation on circuit with initial state |00...0>, then print the circuit's matrix and final state.

## Available Commands
I don't know how to implement CSwap yet, I will add it after I found out how :(

- Not i
- Hadamard i
- CNot i j
- Swap i j
- CCNot i j k
- CSwap i j k (Not implemented yet)
- Z i
- S i
- T i
- TDag i
- SqrtNot i
- SqrtNotDag i
- SqrtSwap i j (Not implemented yet)
