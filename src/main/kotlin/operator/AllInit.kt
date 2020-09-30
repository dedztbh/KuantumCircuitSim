package operator

import Config
import com.lukaskusik.coroutines.transformations.map.mapParallel
import kotlinx.coroutines.runBlocking
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
        alls.forEachIndexed { idx, arr ->
            println(allssket[idx])
            var jointState = I1
            arr.forEach { i ->
                jointState = jointState kron (if (i == 0) KET0 else KET1)
            }
            (opMatrix * jointState).printFancy2(allssket = allssket)
            println()
        }
    }
}

class PAllInit(config: Config) : PTFinder(config) {
    override fun printResult() {
        super.printResult()
        println("\nFinal states: ")
        runBlocking {
            alls.mapParallel { arr ->
                var jointState = I1
                arr.forEach { i ->
                    jointState = jointState kron (if (i == 0) KET0 else KET1)
                }
                opMatrix * jointState
            }
        }.forEachIndexed { i, mat ->
            println(allssket[i])
            mat.printFancy2(allssket = allssket)
            println()
        }
    }
}