package operator

import org.ejml.data.Complex_F64
import util.*
import kotlin.random.Random

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */
class Tester(N: Int) : TFinder(N) {
    /** 2^N by 1 column vector */
    val jointState =
        Matrix(jointStateSize, 1).apply { set(0, 0, 1.0, 0.0) }

    override fun runCmd(cmd: String): Int {
        if (cmd == "Measure") {
            val i = readInt()
            val results = opMatrix * jointState
            val probs = mutableListOf(0.0)
            val labels = mutableListOf<Int>()
            val a = Complex_F64()
            for (j in 0 until results.numRows) {
                results.get(0, j, a)
                if (a.magnitude2 > 0) {
                    labels.add(j)
                    probs.add(a.magnitude2 + probs.last())
                }
            }
            println("Measurement(s):")
            repeat(i) {
                val r = Random.nextDouble()
                val index = probs.upperBound(r)
                println(allssr[labels[index]])
            }
            println()
            return 1
        }
        return super.runCmd(cmd)
    }

    override fun printResult() {
        super.printResult()
        println("\nFinal state: ")
        println("Init |${"0".repeat(N)}>")
        (opMatrix * jointState).printFancy2(allssr = allssr)
    }
}