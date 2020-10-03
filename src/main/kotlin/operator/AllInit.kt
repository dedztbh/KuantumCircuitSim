package operator

import Config
import com.lukaskusik.coroutines.transformations.map.mapParallel
import com.lukaskusik.coroutines.transformations.reduce.reduceParallel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import matrix.*
import matrix.CMatrixIO.printFancy2

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

class AllInit(config: Config, scope: CoroutineScope) : TFinder(config, scope) {
    override suspend fun printResult() {
        super.printResult()
        println("\nFinal states: ")
        alls.forEachIndexed { idx, arr ->
            println("Init ${allssket[idx]}")
            var jointState = I1
            arr.forEach { i ->
                jointState = jointState kron (if (i == 0) KET0 else KET1)
            }
            (opMatrix * jointState).printFancy2(allssket = allssket)
            println()
        }
    }
}

class PAllInit(config: Config, scope: CoroutineScope) : PTFinder(config, scope) {
    override suspend fun printResult() {
        super.printResult()
        println("\nFinal states: ")
        alls.mapParallel { arr ->
            scope.async {
                opMatrix *
                        arr.mapParallel { if (it == 0) KET0 else KET1 }
                            .reduceParallel { a, b -> a kron b }
            }
        }.forEachIndexed { i, mat ->
            println("Init ${allssket[i]}")
            mat.await().printFancy2(allssket = allssket)
            println()
        }
    }
}