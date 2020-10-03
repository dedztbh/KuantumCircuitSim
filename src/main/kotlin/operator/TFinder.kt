package operator

import Config
import allStates
import com.lukaskusik.coroutines.transformations.reduce.reduceParallel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import matrix.*
import readDouble
import readInt
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

open class TFinder(val config: Config, val scope: CoroutineScope) : Operator {
    val N = config.N

    /** all possible states (left-to-right) */
    val alls = allStates(N)

    /** all possible states represented with ket (right-to-left) */
    val allss = alls.map { arr -> arr.joinToString("") }
    val allssket = allss.map { "|$it>" }

    val jointStateSize = alls.size
    val IN2 = COps.identity(jointStateSize)

    var opMatrix = if (config.input_matrix.isNotBlank()) {
        if (config.binary_matrix)
            CMatrixIO.loadBin(config.input_matrix)
        else
            CMatrixIO.loadCsv(config.input_matrix)
    } else IN2

    val IKronTable = Array(N + 1) { I1 }.also {
        for (i in 1..N) {
            it[i] = it[i - 1] kron I2
        }
    }

    open val matrix0CtrlCache: Array<MutableMap<CMatrix, CMatrix>> = Array(N) { HashMap() }
    fun get0CtrlMatrix(i: Int, mat: CMatrix, cache: Boolean = true): CMatrix {
        if (cache) matrix0CtrlCache[i][mat]?.let { return it }
        val res = IKronTable[i] kron mat kron IKronTable[N - i - 1]
        if (cache) matrix0CtrlCache[i][mat] = res
        return res
    }

    open val matrix1CtrlCache: Array<Array<MutableMap<CMatrix, CMatrix>>> = Array(N) { Array(N) { HashMap() } }

    /** Control matrix generation based on this great article:
     * http://www.sakkaris.com/tutorials/quantum_control_gates.html */
    fun get1CtrlMatrix(i: Int, j: Int, mat: CMatrix, cache: Boolean = true): CMatrix {
        if (cache) matrix1CtrlCache[i][j][mat]?.let { return it }
        val res = get0CtrlMatrix(i, KETBRA0) + when {
            i < j -> IKronTable[i] kron KETBRA1 kron
                    IKronTable[j - i - 1] kron
                    mat kron IKronTable[N - j - 1]
            i > j -> IKronTable[j] kron mat kron
                    IKronTable[i - j - 1] kron
                    KETBRA1 kron IKronTable[N - i - 1]
            else -> throw IllegalArgumentException("Control qubit is same as affected qubit")
        }
        if (cache) matrix1CtrlCache[i][j][mat] = res
        return res
    }

    open suspend fun getCCNotMatrix(i: Int, j: Int, k: Int): CMatrix {
        /** using Sleator-Weinfurter construction */
        val cnotij = get1CtrlMatrix(i, j, NOT)
        return get1CtrlMatrix(i, k, SQRT_NOT) *
                cnotij *
                get1CtrlMatrix(j, k, SQRT_NOT_DAG) *
                cnotij *
                get1CtrlMatrix(j, k, SQRT_NOT)
    }

    override suspend fun runCmd(cmd: String): Int {
        val i = readInt()
        val newOp = when (cmd) {
            "NOT" -> get0CtrlMatrix(i, NOT)
            "HADAMARD", "H" -> get0CtrlMatrix(i, H)
            "CNOT" -> get1CtrlMatrix(i, readInt(), NOT)
            "SWAP" -> {
                /** https://algassert.com/post/1717
                 * Swap implemented with 3 CNots */
                val j = readInt()
                val cnot0 = get1CtrlMatrix(i, j, NOT)
                cnot0 * get1CtrlMatrix(j, i, NOT) * cnot0
            }
            "CCNOT" -> getCCNotMatrix(i, readInt(), readInt())
            "CSWAP" -> {
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
            "TDAG" -> get0CtrlMatrix(i, TDag)
            "SQRTNOT" -> get0CtrlMatrix(i, SQRT_NOT)
            "SQRTNOTDAG" -> get0CtrlMatrix(i, SQRT_NOT_DAG)
//            "SQRTSWAP" -> {
//                TODO: Implement SqrtSwap
//            }
            "ROT" -> {
                val rad = readDouble() * PI / 180
                val sine = sin(rad)
                val cosine = cos(rad)
                val rotMat = CMatrix(
                    arrayOf(
                        doubleArrayOf(cosine, 0.0, -sine, 0.0),
                        doubleArrayOf(sine, 0.0, cosine, 0.0)
                    )
                )
                get0CtrlMatrix(i, rotMat, false)
            }
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
                CMatrixIO.saveBin(opMatrix, config.output)
            else
                CMatrixIO.saveCsv(opMatrix, config.output)
        }
    }

    override suspend fun printResult() {
        if (!config.no_t) {
            println("Transformation: ")
            opMatrix.print()
        }
    }
}


open class PTFinder(config: Config, scope: CoroutineScope) : TFinder(config, scope) {

    var reversedNewOps = mutableListOf(scope.async { opMatrix })

    /** Use concurrent maps to prevent concurrent modification */
    override val matrix0CtrlCache: Array<MutableMap<CMatrix, CMatrix>> =
        Array(N) { ConcurrentHashMap() }

    fun add0CtrlMatrix(i: Int, mat: CMatrix, cache: Boolean = true) =
        reversedNewOps.add(scope.async { get0CtrlMatrix(i, mat, cache) })

    override val matrix1CtrlCache: Array<Array<MutableMap<CMatrix, CMatrix>>> =
        Array(N) { Array(N) { ConcurrentHashMap() } }

    fun add1CtrlMatrix(i: Int, j: Int, mat: CMatrix, cache: Boolean = true) =
        reversedNewOps.add(scope.async {
            if (cache) matrix1CtrlCache[i][j][mat]?.let { return@async it }
            get0CtrlMatrix(i, KETBRA0) + when {
                i < j -> listOf(
                    IKronTable[i], KETBRA1,
                    IKronTable[j - i - 1],
                    mat, IKronTable[N - j - 1]
                )
                i > j -> listOf(
                    IKronTable[j], mat,
                    IKronTable[i - j - 1],
                    KETBRA1, IKronTable[N - i - 1]
                )
                else -> throw IllegalArgumentException("Control qubit is same as affected qubit")
            }.reduceParallel { a, b -> a kron b }.also {
                if (cache) matrix1CtrlCache[i][j][mat] = it
            }
        })

    /** using Sleator-Weinfurter construction */
    fun addCCNotMatrix(i: Int, j: Int, k: Int) {
        add1CtrlMatrix(j, k, SQRT_NOT)
        add1CtrlMatrix(i, j, NOT)
        add1CtrlMatrix(j, k, SQRT_NOT_DAG)
        add1CtrlMatrix(i, j, NOT)
        add1CtrlMatrix(i, k, SQRT_NOT)
    }

    override suspend fun runCmd(cmd: String): Int = scope.run {
        val i = readInt()
        when (cmd) {
            "NOT" -> add0CtrlMatrix(i, NOT)
            "HADAMARD", "H" -> add0CtrlMatrix(i, H)
            "CNOT" -> add1CtrlMatrix(i, readInt(), NOT)
            "SWAP" -> {
                /** https://algassert.com/post/1717
                 * Swap implemented with 3 CNots */
                val j = readInt()
                add1CtrlMatrix(i, j, NOT)
                add1CtrlMatrix(j, i, NOT)
                add1CtrlMatrix(i, j, NOT)
            }
            "CCNOT" -> addCCNotMatrix(i, readInt(), readInt())
            "CSWAP" -> {
                /** https://quantumcomputing.stackexchange.com/
                 * questions/9342/how-to-implement-a-fredkin
                 * -gate-using-toffoli-and-cnots */
                val j = readInt()
                val k = readInt()
                add1CtrlMatrix(k, j, NOT)
                addCCNotMatrix(i, j, k)
                add1CtrlMatrix(k, j, NOT)
            }
            "Z" -> add0CtrlMatrix(i, Z)
            "S" -> add0CtrlMatrix(i, S)
            "T" -> add0CtrlMatrix(i, T)
            "TDAG" -> add0CtrlMatrix(i, TDag)
            "SQRTNOT" -> add0CtrlMatrix(i, SQRT_NOT)
            "SQRTNOTDAG" -> add0CtrlMatrix(i, SQRT_NOT_DAG)
//            "SQRTSWAP" -> {
//                TODO: Implement SqrtSwap
//            }
            "ROT" -> {
                val deg = readDouble()
                reversedNewOps.add(async {
                    val rad = deg * PI / 180
                    val sine = sin(rad)
                    val cosine = cos(rad)
                    val rotMat = CMatrix(
                        arrayOf(
                            doubleArrayOf(cosine, 0.0, -sine, 0.0),
                            doubleArrayOf(sine, 0.0, cosine, 0.0)
                        )
                    )
                    get0CtrlMatrix(i, rotMat, false)
                })
            }
            "CZ" -> add1CtrlMatrix(i, readInt(), Z)
            else -> {
                System.err.println("Unknown command \"${cmd}\". Stop reading commands.")
                return -1
            }
        }
        return 0
    }

    suspend fun reduceOps() =
        reversedNewOps.reduceParallel { d1, d2 ->
            scope.async {
                d2.await() * d1.await()
            }
        }

    override suspend fun done() {
        opMatrix = reduceOps().await()
        super.done()
    }
}