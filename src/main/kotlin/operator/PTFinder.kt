package operator

import Config
import com.lukaskusik.coroutines.transformations.reduce.reduceParallel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import matrix.*
import readDouble
import readInt
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

open class PTFinder(config: Config) : TFinder(config) {

    val reversedNewOps = mutableListOf(GlobalScope.async { opMatrix })

    /** Use synchronized maps to prevent concurrent modification */
    override val matrix0CtrlCache = Array(N) { Collections.synchronizedMap(HashMap<CMatrix, CMatrix>()) }
    override val matrix1CtrlCache = Array(N) { Array(N) { Collections.synchronizedMap(HashMap<CMatrix, CMatrix>()) } }

    override fun runCmd(cmd: String): Int {
        val i = readInt()
        val newOp = when (cmd) {
            "NOT" -> GlobalScope.async { get0CtrlMatrix(i, NOT) }
            "HADAMARD", "H" -> GlobalScope.async { get0CtrlMatrix(i, H) }
            "CNOT" -> {
                val j = readInt()
                GlobalScope.async { get1CtrlMatrix(i, j, NOT) }
            }
            "SWAP" -> {
                /** https://algassert.com/post/1717
                 * Swap implemented with 3 CNots */
                val j = readInt()
                GlobalScope.async {
                    val cnot0 = get1CtrlMatrix(i, j, NOT)
                    cnot0 * get1CtrlMatrix(j, i, NOT) * cnot0
                }
            }
            "CCNOT" -> {
                val j = readInt()
                val k = readInt()
                GlobalScope.async {
                    getCCNotMatrix(i, j, k)
                }
            }
            "CSWAP" -> {
                /** https://quantumcomputing.stackexchange.com/
                 * questions/9342/how-to-implement-a-fredkin
                 * -gate-using-toffoli-and-cnots */
                val j = readInt()
                val k = readInt()
                GlobalScope.async {
                    val cnotkj = get1CtrlMatrix(k, j, NOT)
                    cnotkj * getCCNotMatrix(i, j, k) * cnotkj
                }
            }
            "Z" -> GlobalScope.async { get0CtrlMatrix(i, Z) }
            "S" -> GlobalScope.async { get0CtrlMatrix(i, S) }
            "T" -> GlobalScope.async { get0CtrlMatrix(i, T) }
            "TDAG" -> GlobalScope.async { get0CtrlMatrix(i, TDag) }
            "SQRTNOT" -> GlobalScope.async { get0CtrlMatrix(i, SQRT_NOT) }
            "SQRTNOTDAG" -> GlobalScope.async { get0CtrlMatrix(i, SQRT_NOT_DAG) }
//            "SQRTSWAP" -> {
//                TODO: Implement SqrtSwap
//            }
            "ROT" -> {
                val deg = readDouble()
                GlobalScope.async {
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
                }
            }
            else -> {
                System.err.println("Unknown command \"${cmd}\". Stop reading commands.")
                return -1
            }
        }
        reversedNewOps.add(newOp)
        return 0
    }

    override fun printResult() {
        opMatrix = runBlocking {
            reversedNewOps.reduceParallel { d1, d2 ->
                GlobalScope.async {
                    val m1 = d1.await()
                    val m2 = d2.await()
//                    m1.print()
//                    m2.print()
//                    println()
                    m2 * m1
                }
            }.await()
        }

        super.printResult()
    }
}