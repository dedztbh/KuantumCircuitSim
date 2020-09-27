package operator

import Config
import matrix.*
import matrix.CMatrixIO.printFancy2


/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */


class AllInit(config: Config) : TFinder(config) {
    override fun printResult() {
        super.printResult()
        println("\nFinal states: ")
        alls.forEach { arr ->
            println("Init |${arr.joinToString("")}>")
            var jointState = I1
            arr.forEach { i ->
                jointState = jointState kron (if (i == 0) KET0 else KET1)
            }
            (opMatrix * jointState).printFancy2(allssr = allssr)
            println()
        }
    }
}