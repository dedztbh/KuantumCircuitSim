package operator

import util.*
import kotlin.math.pow

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

fun allStates(n: Int) =
    Array(2.0.pow(n).toInt()) {
        IntArray(n) { i -> (it shr i) and 1 }.apply { reverse() }
    }


class AllInit(N: Int) : TFinder(N) {
    override fun printResult() {
        super.printResult()
        println("\nFinal states: ")
        allStates(N).forEach { arr ->
            println("Init |${arr.joinToString("")}>")
            var jointState = I1
            arr.forEach { i ->
                jointState = (if (i == 0) KET0 else KET1) kron jointState
            }
            (opMatrix * jointState).print()
            println()
        }
    }
}