package com.dedztbh.kuantum.jblas.operator

import com.dedztbh.kuantum.common.Config
import com.dedztbh.kuantum.jblas.matrix.*
import com.lukaskusik.coroutines.transformations.map.mapParallel
import com.lukaskusik.coroutines.transformations.reduce.reduceParallel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

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
            val jointState =
                arr.map { if (it == 0) KET0 else KET1 }
                    .asIterable()
                    .reduceParallel(sqrtN) { a, b -> a kron b }
            (opMatrix * jointState).printFancy(allssket)
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
                val jointState =
                    arr.mapParallel { if (it == 0) KET0 else KET1 }
                        .asIterable()
                        .reduceParallel(sqrtN) { a, b -> a kron b }
                opMatrix * jointState
            }
        }.forEachIndexed { i, mat ->
            println("Init ${allssket[i]}")
            mat.await().printFancy(allssket)
            println()
        }
    }
}