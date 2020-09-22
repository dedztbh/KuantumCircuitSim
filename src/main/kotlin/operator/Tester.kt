package operator

import util.*
import kotlin.math.pow


/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

class Tester(val N: Int) : Operator {
    val jointStateSize = 2.0.pow(N).toInt()

    /* 2^N by 1 column vector */
    val jointState =
        Matrix(jointStateSize, 1).apply { set(0, 0, 1.0, 0.0) }

    val Zero = Matrix(N, N)
    val IN2 = Ops.identity(jointStateSize)

    /* Control Matrix generation based on this great article:
     * http://www.sakkaris.com/tutorials/quantum_control_gates.html */
    fun zeroCtrlMatrix(i: Int, mat: Matrix): Matrix {
        var base0 = I1
        repeat(N) {
            val op =
                if (it == i)
                    mat
                else I2
            base0 = op kron base0
        }
        return base0
    }

    fun oneCtrlMatrix(i: Int, j: Int, mat: Matrix): Matrix {
        var base0 = I1
        repeat(N) {
            val op = if (it == i) KETBRA0 else I2
            base0 = base0 kron op
        }
        var base1 = I1
        repeat(N) {
            val op = when (it) {
                i -> KETBRA1
                j -> mat
                else -> I2
            }
            base1 = base1 kron op
        }
        return base0 + base1
    }

    var opMatrix = IN2

    override fun runCmd(cmd: String): Int {
        val i = readInt()
        val newOp = when (cmd) {
            "Not" -> zeroCtrlMatrix(i, NOT)
            "Hadamard" -> zeroCtrlMatrix(i, H)
            "CNot" -> oneCtrlMatrix(i, readInt(), NOT)
            "Swap" -> {
                /* https://algassert.com/post/1717
                 * Swap implemented with 3 CNots */
                val j = readInt()
                val cnot0 = oneCtrlMatrix(i, j, NOT)
                cnot0 * oneCtrlMatrix(j, i, NOT) * cnot0
            }
            "CCNot" -> {
                // using Sleator-Weinfurter construction
                val j = readInt()
                val k = readInt()
                val cnotij = oneCtrlMatrix(i, j, NOT)
                // matrix is actually reverse order as graph
                oneCtrlMatrix(i, k, SQRT_NOT) *
                        cnotij *
                        oneCtrlMatrix(j, k, SQRT_NOT_DAG) *
                        cnotij *
                        oneCtrlMatrix(j, k, SQRT_NOT)
            }
//            "CSwap" -> {
//                TODO: Implement CSwap
//                Can anyone tell me how to implement CSwap :(
//            }
            "Z" -> zeroCtrlMatrix(i, Z)
            "S" -> zeroCtrlMatrix(i, S)
            "T" -> zeroCtrlMatrix(i, T)
            "TDag" -> zeroCtrlMatrix(i, TDag)
            "SqrtNot" -> zeroCtrlMatrix(i, SQRT_NOT)
            "SqrtNotDag" -> zeroCtrlMatrix(i, SQRT_NOT_DAG)
//            "SqrtSwap" -> {
//                TODO: Implement SqrtSwap
//            }
            else -> return -1
        }
        opMatrix = newOp * opMatrix
        return 0
    }

    override fun printResult() {
        println("Transformation: ")
        opMatrix.print()
        println()
        println("Final states: ")
        (opMatrix * jointState).print()
    }
}