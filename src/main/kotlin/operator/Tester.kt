package operator

import util.Matrix
import util.times

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */
class Tester(N: Int) : TFinder(N) {
    /** 2^N by 1 column vector */
    val jointState =
        Matrix(jointStateSize, 1).apply { set(0, 0, 1.0, 0.0) }

    override fun printResult() {
        super.printResult()
        println("\nFinal state: ")
        (opMatrix * jointState).print()
    }
}