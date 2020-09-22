package operator

import util.*
import kotlin.math.pow


/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

class Tester(val N: Int) : Operator {
    val jointStateSize = 2.0.pow(N).toInt()
    val IN2 = Ops.identity(jointStateSize)

    /* 2^N by 1 column vector */
    val jointState =
        Matrix(jointStateSize, 1).apply { set(0, 0, 1.0, 0.0) }
    var opMatrix = IN2


    /** Control matrix generation based on this great article:
     * http://www.sakkaris.com/tutorials/quantum_control_gates.html */
    fun get0CtrlMatrix(i: Int, mat: Matrix): Matrix {
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

    fun get1CtrlMatrix(i: Int, j: Int, mat: Matrix): Matrix {
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

    fun getCCNotMatrix(i: Int, j: Int, k: Int): Matrix {
        /** using Sleator-Weinfurter construction
         * matrix is actually reverse order as graph */
        val cnotij = get1CtrlMatrix(i, j, NOT)
        return get1CtrlMatrix(i, k, SQRT_NOT) *
                cnotij *
                get1CtrlMatrix(j, k, SQRT_NOT_DAG) *
                cnotij *
                get1CtrlMatrix(j, k, SQRT_NOT)
    }


    override fun runCmd(cmd: String): Int {
        val i = readInt()
        val newOp = when (cmd) {
            "Not" -> get0CtrlMatrix(i, NOT)
            "Hadamard" -> get0CtrlMatrix(i, H)
            "CNot" -> get1CtrlMatrix(i, readInt(), NOT)
            "Swap" -> {
                /* https://algassert.com/post/1717
                 * Swap implemented with 3 CNots */
                val j = readInt()
                val cnot0 = get1CtrlMatrix(i, j, NOT)
                cnot0 * get1CtrlMatrix(j, i, NOT) * cnot0
            }
            "CCNot" -> getCCNotMatrix(i, readInt(), readInt())
            "CSwap" -> {
                /** https://quantumcomputing.stackexchange.com/
                 * questions/9342/how-to-implement-a-fredkin
                 * -gate-using-toffoli-and-cnots */
                val j = readInt()
                val k = readInt()
                val cnotkj = get1CtrlMatrix(k, j, NOT)
                cnotkj * getCCNotMatrix(i, j, k) * cnotkj
            }
            "Z" -> get0CtrlMatrix(i, Z)
            "S" -> get0CtrlMatrix(i, S)
            "T" -> get0CtrlMatrix(i, T)
            "TDag" -> get0CtrlMatrix(i, TDag)
            "SqrtNot" -> get0CtrlMatrix(i, SQRT_NOT)
            "SqrtNotDag" -> get0CtrlMatrix(i, SQRT_NOT_DAG)
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