package com.dedztbh.kuantum.ejml.operator

import com.dedztbh.kuantum.common.Config
import com.dedztbh.kuantum.ejml.matrix.*
import com.lukaskusik.coroutines.transformations.map.mapParallel
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
            val it = arr.iterator()
            var jointState = if (it.next() == 0) KET0 else KET1
            it.forEachRemaining { i ->
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
                val it = arr.iterator()
                var jointState = if (it.next() == 0) KET0 else KET1
                it.forEachRemaining { i ->
                    jointState = jointState kron (if (i == 0) KET0 else KET1)
                }
                opMatrix * jointState
            }
        }.forEachIndexed { i, mat ->
            println("Init ${allssket[i]}")
            mat.await().printFancy2(allssket = allssket)
            println()
        }
    }
}