package operator

import util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin


/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

open class TFinder(val N: Int) : Operator {
    val jointStateSize = 2.0.pow(N).toInt()
    val IN2 = Ops.identity(jointStateSize)

    var opMatrix = IN2

    val IKronTable = Array(N + 1) { I1 }.also {
        for (i in 1..N) {
            it[i] = I2 kron it[i - 1]
        }
    }

    fun get0CtrlMatrix(i: Int, mat: Matrix): Matrix =
        IKronTable[N - i - 1] kron mat kron IKronTable[i]

    /** Control matrix generation based on this great article:
     * http://www.sakkaris.com/tutorials/quantum_control_gates.html */
    val base0Table = Array(N) { i -> get0CtrlMatrix(i, KETBRA0) }
    fun get1CtrlMatrix(i: Int, j: Int, mat: Matrix): Matrix =
        base0Table[i] + when {
            i < j -> IKronTable[N - j - 1] kron
                    mat kron
                    IKronTable[j - i - 1] kron
                    KETBRA1 kron
                    IKronTable[i]
            i > j -> IKronTable[N - i - 1] kron
                    KETBRA1 kron
                    IKronTable[i - j - 1] kron
                    mat kron
                    IKronTable[j]
            else -> throw IllegalArgumentException("Control qubit is same as affected qubit")
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
                /** https://algassert.com/post/1717
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
            "Rot" -> {
                val rad = readDouble() * PI / 180
                val sine = sin(rad)
                val cosine = cos(rad)
                val rotMat = Matrix(
                    arrayOf(
                        doubleArrayOf(cosine, 0.0, -sine, 0.0),
                        doubleArrayOf(sine, 0.0, cosine, 0.0)
                    )
                )
                get0CtrlMatrix(i, rotMat)
            }
            else -> {
                System.err.println("Unknown command \"$cmd\". Stop reading commands.")
                return -1
            }
        }
        opMatrix = newOp * opMatrix
        return 0
    }

    override fun printResult() {
        println("Transformation: ")
        opMatrix.print()
    }
}