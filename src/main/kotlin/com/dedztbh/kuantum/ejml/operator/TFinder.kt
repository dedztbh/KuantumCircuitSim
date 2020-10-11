package com.dedztbh.kuantum.ejml.operator

import com.dedztbh.kuantum.common.*
import com.dedztbh.kuantum.ejml.matrix.*
import com.lukaskusik.coroutines.transformations.reduce.reduceParallel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

open class TFinder(val config: Config, val scope: CoroutineScope) : Operator {
    @JvmField
    val N = config.N

    /** all possible states (left-to-right) */
    @JvmField
    val alls = allStates(N)

    /** all possible states represented with ket (right-to-left) */
    @JvmField
    val allss = alls.map { arr -> arr.joinToString("") }

    @JvmField
    val allssket = allss.map { "|$it>" }

    @JvmField
    val jointStateSize = alls.size

    @JvmField
    val IN2 = COps.identity(jointStateSize)

    @JvmField
    var opMatrix = if (config.input_matrix.isNotBlank()) {
        if (config.binary_matrix)
            loadBin(config.input_matrix)
        else
            loadCsv(config.input_matrix)
    } else IN2

    @JvmField
    val IKronTable = (0..N).scan(I1) { it, _ -> it kron I2 }.toTypedArray()

    open fun <K, V> getHashMap(): MutableMap<K, V> = HashMap()

    @JvmField
    val matrix0CtrlCache: Array<MutableMap<CMatrix, CMatrix>> = Array(N) { getHashMap() }
    fun get0CtrlMatrix(i: Int, mat: CMatrix, cache: Boolean = true): CMatrix {
        if (cache) matrix0CtrlCache[i][mat]?.let { return it }
        val res = IKronTable[i] kron mat kron IKronTable[N - i - 1]
        if (cache) matrix0CtrlCache[i][mat] = res
        return res
    }

    /** Control matrix generation based on this great article:
     * http://www.sakkaris.com/tutorials/quantum_control_gates.html */
    @JvmField
    val matrix1CtrlCache: Array<Array<MutableMap<CMatrix, CMatrix>>> = Array(N) { Array(N) { getHashMap() } }
    fun get1CtrlMatrix(i: Int, j: Int, mat: CMatrix): CMatrix {
        matrix1CtrlCache[i][j][mat]?.let { return it }
        val res = get0CtrlMatrix(i, KETBRA0) + when {
            i < j -> IKronTable[i] kron KETBRA1 kron
                    IKronTable[j - i - 1] kron
                    mat kron IKronTable[N - j - 1]
            i > j -> IKronTable[j] kron mat kron
                    IKronTable[i - j - 1] kron
                    KETBRA1 kron IKronTable[N - i - 1]
            else -> throw IllegalArgumentException("Control qubit is same as affected qubit")
        }
        matrix1CtrlCache[i][j][mat] = res
        return res
    }

    @JvmField
    val ccnotCache: Array<Array<MutableMap<Int, CMatrix>>> = Array(N) { Array(N) { getHashMap() } }
    fun getCCNotMatrix(i: Int, j: Int, k: Int): CMatrix {
        /** using Sleator-Weinfurter construction */
        ccnotCache[i][j][k]?.let { return it }
        val cnotij = get1CtrlMatrix(i, j, NOT)
        val res = get1CtrlMatrix(i, k, SQRT_NOT) *
                cnotij *
                get1CtrlMatrix(j, k, SQRT_NOT_DAG) *
                cnotij *
                get1CtrlMatrix(j, k, SQRT_NOT)
        ccnotCache[i][j][k] = res
        return res
        /** Alternative implementation:
         * https://en.wikipedia.org/wiki/Toffoli_gate
        val Hk = get0CtrlMatrix(k, H)
        val TDagj = get0CtrlMatrix(j, TDag)
        val TDagk = get0CtrlMatrix(k, TDag)
        val Ti = get0CtrlMatrix(i, T)
        val Tj = get0CtrlMatrix(j, T)
        val Tk = get0CtrlMatrix(k, T)
        val CNotij = get1CtrlMatrix(i, j, NOT)
        val CNotik = get1CtrlMatrix(i, k, NOT)
        val CNotjk = get1CtrlMatrix(j, k, NOT)
        return CNotij * TDagj * Ti *
        Hk * CNotij * Tk * Tj *
        CNotik * TDagk * CNotjk *
        Tk * CNotik * TDagk * CNotjk * Hk */
    }

    @JvmField
    val rotCache: Array<MutableMap<Double, CMatrix>> = Array(N) { getHashMap() }
    fun getRotMatrix(i: Int, d: Double): CMatrix {
        rotCache[i][d]?.let { return it }
        val rad = d * PI / 180.0
        val sine = sin(rad)
        val cosine = cos(rad)
        val rotMat = CMatrix(
            arrayOf(
                doubleArrayOf(cosine, 0.0, -sine, 0.0),
                doubleArrayOf(sine, 0.0, cosine, 0.0)
            )
        )
        return get0CtrlMatrix(i, rotMat, false).also {
            rotCache[i][d] = it
        }
    }

    @JvmField
    val swapCache: Array<MutableMap<Int, CMatrix>> = Array(N) { getHashMap() }
    fun getSwapMatrix(i: Int, j: Int): CMatrix {
        swapCache[i][j]?.let { return it }
        /** https://algassert.com/post/1717
         * Swap implemented with 3 CNots */
        val cnot0 = get1CtrlMatrix(i, j, NOT)
        return (cnot0 * get1CtrlMatrix(j, i, NOT) * cnot0).also {
            swapCache[i][j] = it
        }
    }

    @JvmField
    val cswapCache: Array<Array<MutableMap<Int, CMatrix>>> = Array(N) { Array(N) { getHashMap() } }
    fun getCSwapMatrix(i: Int, j: Int, k: Int): CMatrix {
        cswapCache[i][j][k]?.let { return it }
        /** https://quantumcomputing.stackexchange.com/
         * questions/9342/how-to-implement-a-fredkin
         * -gate-using-toffoli-and-cnots */
        val cnotkj = get1CtrlMatrix(k, j, NOT)
        return (cnotkj * getCCNotMatrix(i, j, k) * cnotkj).also {
            cswapCache[i][j][k] = it
        }
    }

    override suspend fun runCmd(cmd: String): Int {
        val i = readInt()
        val newOp = when (cmd) {
            "NOT" -> get0CtrlMatrix(i, NOT)
            "HADAMARD", "H" -> get0CtrlMatrix(i, H)
            "CNOT" -> get1CtrlMatrix(i, readInt(), NOT)
            "SWAP" -> getSwapMatrix(i, readInt())
            "CCNOT" -> getCCNotMatrix(i, readInt(), readInt())
            "CSWAP" -> getCSwapMatrix(i, readInt(), readInt())
            "Y" -> get0CtrlMatrix(i, Y)
            "Z" -> get0CtrlMatrix(i, Z)
            "S" -> get0CtrlMatrix(i, S)
            "T" -> get0CtrlMatrix(i, T)
            "TDAG" -> get0CtrlMatrix(i, TDag)
            "SQRTNOT" -> get0CtrlMatrix(i, SQRT_NOT)
            "SQRTNOTDAG" -> get0CtrlMatrix(i, SQRT_NOT_DAG)
//            "SQRTSWAP" -> {
//                TODO: Implement SqrtSwap
//            }
            "ROT" -> getRotMatrix(i, readDouble())
            "CZ" -> get1CtrlMatrix(i, readInt(), Z)
            else -> {
                System.err.println("Unknown command \"${cmd}\". Stop reading commands.")
                return -1
            }
        }
        opMatrix = newOp * opMatrix
        return 0
    }

    override suspend fun done() {
        if (config.output.isNotBlank()) {
            if (config.binary_matrix)
                saveBin(opMatrix, config.output)
            else
                saveCsv(opMatrix, config.output)
        }
    }

    override suspend fun printResult() {
        if (!config.no_t) {
            println("Transformation: ")
            opMatrix.print()
        }
    }
}

const val CONCURRENT_MATRIX = 4096

open class PTFinder(config: Config, scope: CoroutineScope) : TFinder(config, scope) {

    @JvmField
    var reversedNewOps = mutableListOf(scope.async { opMatrix })

    override fun <K, V> getHashMap(): MutableMap<K, V> = ConcurrentHashMap()

    override suspend fun runCmd(cmd: String): Int {
        val i = readInt()
        scope.run {
            reversedNewOps.add(when (cmd) {
                "NOT" -> async { get0CtrlMatrix(i, NOT) }
                "HADAMARD", "H" -> async { get0CtrlMatrix(i, H) }
                "CNOT" -> {
                    val j = readInt()
                    async { get1CtrlMatrix(i, j, NOT) }
                }
                "SWAP" -> {
                    /** https://algassert.com/post/1717
                     * Swap implemented with 3 CNots */
                    val j = readInt()
                    async { getSwapMatrix(i, j) }
                }
                "CCNOT" -> {
                    val j = readInt()
                    val k = readInt()
                    async { getCCNotMatrix(i, j, k) }
                }
                "CSWAP" -> {
                    /** https://quantumcomputing.stackexchange.com/
                     * questions/9342/how-to-implement-a-fredkin
                     * -gate-using-toffoli-and-cnots */
                    val j = readInt()
                    val k = readInt()
                    async { getCSwapMatrix(i, j, k) }
                }
                "Y" -> async { get0CtrlMatrix(i, Y) }
                "Z" -> async { get0CtrlMatrix(i, Z) }
                "S" -> async { get0CtrlMatrix(i, S) }
                "T" -> async { get0CtrlMatrix(i, T) }
                "TDAG" -> async { get0CtrlMatrix(i, TDag) }
                "SQRTNOT" -> async { get0CtrlMatrix(i, SQRT_NOT) }
                "SQRTNOTDAG" -> async { get0CtrlMatrix(i, SQRT_NOT_DAG) }
//                "SQRTSWAP" -> {
//                    TODO: Implement SqrtSwap
//                }
                "ROT" -> {
                    val deg = readDouble()
                    async { getRotMatrix(i, deg) }
                }
                "CZ" -> {
                    val j = readInt()
                    async { get1CtrlMatrix(i, j, Z) }
                }
                else -> {
                    System.err.println("Unknown command \"${cmd}\". Stop reading commands.")
                    return -1
                }
            })
            if (reversedNewOps.size >= CONCURRENT_MATRIX) {
                // We have enough matrices, do a reduction to prevent OOM
                val newNewOps = reduceOps()
                reversedNewOps = mutableListOf(async { newNewOps })
            }
        }
        return 0
    }

    suspend fun reduceOps() =
        reversedNewOps.map { it.await() }
            .reduceParallel { d1, d2 ->
                d2 * d1
            }

    override suspend fun done() {
        opMatrix = reduceOps()
        super.done()
    }
}