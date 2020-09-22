package operator

import util.times

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */
class Tester(N: Int) : TFinder(N) {
    override fun printResult() {
        super.printResult()
        println("\nFinal state: ")
        (opMatrix * jointState).print()
    }
}