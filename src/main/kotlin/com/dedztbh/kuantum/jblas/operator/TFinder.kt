package com.dedztbh.kuantum.jblas.operator

import com.dedztbh.kuantum.common.*
import com.dedztbh.kuantum.jblas.matrix.*
import com.lukaskusik.coroutines.transformations.reduce.reduceParallel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.jblas.DoubleMatrix
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

open class TFinder(val config: Config, val scope: CoroutineScope) : Operator {
    @JvmField
    val N = config.N

    @JvmField
    val sqrtN = sqrt(N.toDouble()).roundToInt()

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
    val IN2 = CMatrix(DoubleMatrix.eye(jointStateSize), null)

    @JvmField
    var opMatrix = if (config.input_matrix.isNotBlank()) {
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
                    (mat kron IKronTable[N - j - 1])
            i > j -> IKronTable[j] kron mat kron
                    IKronTable[i - j - 1] kron
                    (KETBRA1 kron IKronTable[N - i - 1])
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
    }

    @JvmField
    val rotCache: Array<MutableMap<Double, CMatrix>> = Array(N) { getHashMap() }
    fun getRotMatrix(i: Int, d: Double): CMatrix {
        rotCache[i][d]?.let { return it }
        val rad = d * PI / 180.0
        val sine = sin(rad)
        val cosine = cos(rad)

        /** This is column major!
         * It's actually
         *  cos -sin
         *  sin cos */
        val rotMat = CMatrix(
            2, 2,
            cosine, 0.0, sine, 0.0,
            -sine, 0.0, cosine, 0.0
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


    @JvmField
    var parallelMode = false

    @JvmField
    var parallelMatrices = MutableList(N) { I2 }
    suspend inline fun checkParAndWhen(cmd: String, block: () -> Unit) {
        when (cmd) {
            "PARSTART" -> {
                parallelMode = true
            }
            "PAREND" -> {
                parallelMode = false
                opMatrix = parallelMatrices
                    .asIterable()
                    .reduceParallel(sqrtN) { d1, d2 -> d1 kron d2 } * opMatrix
                parallelMatrices = MutableList(N) { I2 }
            }
            else -> block()
        }
    }

    override suspend fun runCmd(cmd: String): Int {
        checkParAndWhen(cmd) {
            val i = readInt()
            val newOp = when (cmd) {
                "CNOT" -> get1CtrlMatrix(i, readInt(), NOT)
                "SWAP" -> getSwapMatrix(i, readInt())
                "CCNOT" -> getCCNotMatrix(i, readInt(), readInt())
                "CSWAP" -> getCSwapMatrix(i, readInt(), readInt())
//            "SQRTSWAP" -> {
//                TODO: Implement SqrtSwap
//            }
                "ROT" -> {
                    val deg = readDouble()
                    if (parallelMode) {
                        parallelMatrices[i] = getRotMatrix(i, deg)
                        return@checkParAndWhen
                    }
                    getRotMatrix(i, deg)
                }
                "CZ" -> get1CtrlMatrix(i, readInt(), Z)
                else -> map0Ctrl(cmd).let {
                    if (it != null) {
                        if (parallelMode) {
                            parallelMatrices[i] = it
                            return@checkParAndWhen
                        } else get0CtrlMatrix(i, it)
                    } else {
                        System.err.println("Unknown command \"${cmd}\". Stop reading commands.")
                        return@runCmd -1
                    }
                }
            }
            opMatrix = newOp * opMatrix
        }
        return 0
    }

    override suspend fun done() {
        if (config.output.isNotBlank()) {
            saveCsv(opMatrix, config.output)
        }
    }

    override suspend fun printResult() {
        if (!config.no_t) {
            println("Transformation: ")
            opMatrix.printFancy()
            println()
        }
    }
}

@JvmField
val CONCURRENT_MATRIX = Runtime.getRuntime().availableProcessors().let {
    it shl 9
}

open class PTFinder(config: Config, scope: CoroutineScope) : TFinder(config, scope) {

    @JvmField
    var reversedNewOps = mutableListOf(scope.async { opMatrix })

    override fun <K, V> getHashMap(): MutableMap<K, V> = ConcurrentHashMap()

    suspend inline fun checkParAndWhenConcurrent(cmd: String, block: () -> Unit) =
        when (cmd) {
            "PARSTART" -> {
                parallelMode = true
            }
            "PAREND" -> {
                parallelMode = false
                val parallelMatricesBackup = parallelMatrices
                reversedNewOps.add(scope.async {
                    parallelMatricesBackup
                        .asIterable()
                        .reduceParallel(sqrtN) { d1, d2 -> d1 kron d2 }
                })
                parallelMatrices = MutableList(N) { I2 }
            }
            else -> block()
        }

    override suspend fun runCmd(cmd: String): Int {
        checkParAndWhenConcurrent(cmd) {
            val i = readInt()
            scope.run {
                reversedNewOps.add(when (cmd) {
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
//                "SQRTSWAP" -> {
//                    TODO: Implement SqrtSwap
//                }
                    "ROT" -> {
                        val deg = readDouble()
                        if (parallelMode) {
                            parallelMatrices[i] = getRotMatrix(i, deg)
                            return@checkParAndWhenConcurrent
                        }
                        async { getRotMatrix(i, deg) }
                    }
                    "CZ" -> {
                        val j = readInt()
                        async { get1CtrlMatrix(i, j, Z) }
                    }
                    else -> map0Ctrl(cmd).let {
                        if (it != null) {
                            if (parallelMode) {
                                parallelMatrices[i] = it
                                return@checkParAndWhenConcurrent
                            } else async { get0CtrlMatrix(i, it) }
                        } else {
                            System.err.println("Unknown command \"${cmd}\". Stop reading commands.")
                            return@runCmd -1
                        }
                    }
                })
                if (reversedNewOps.size >= CONCURRENT_MATRIX) {
                    // We have enough matrices, do a reduction to prevent OOM
                    val newNewOps = reduceOps()
                    reversedNewOps = mutableListOf(async { newNewOps })
                }
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