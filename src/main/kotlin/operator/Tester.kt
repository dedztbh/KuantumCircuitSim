package operator

import Config
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import matrix.CMatrix
import matrix.CMatrixIO.printFancy2
import matrix.CNumber
import matrix.COps
import matrix.times
import readInt
import upperBound
import kotlin.random.Random

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

fun getJointState(initState: String, jointStateSize: Int) = CMatrix(jointStateSize, 1).apply {
    if (initState.isBlank()) {
        set(0, 0, 1.0, 0.0)
    } else {
        CsvReader().open(initState) {
            readAllAsSequence().forEachIndexed { i, list ->
                val (re, im) = list
                set(i, 0, re.trim().toDouble(), im.trim().toDouble())
            }
        }
    }
}

class Tester(config: Config) : TFinder(config) {
    /** 2^N by 1 column vector */
    var jointState = getJointState(config.init_state, jointStateSize)
    var hasMeasGate = false

    override fun runCmd(cmd: String) = when (cmd) {
        "MEASURE" -> {
            val i = readInt()
            val results = opMatrix * jointState
            val probs = mutableListOf(0.0)
            val labels = mutableListOf<Int>()
            val a = CNumber()
            for (j in 0 until jointStateSize) {
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
                println(allssket[labels[index]])
            }
            println()
            0
        }
        "MEASALL" -> {
            jointState = opMatrix * jointState
            opMatrix = IN2
            val probs = mutableListOf(0.0)
            val labels = mutableListOf<Int>()
            val a = CNumber()
            for (j in 0 until jointStateSize) {
                jointState.get(j, 0, a)
                if (a.magnitude2 > 0) {
                    labels.add(j)
                    probs.add(a.magnitude2 + probs.last())
                }
            }
            val index = probs.upperBound(Random.nextDouble())
            val collapseState = labels[index]
            println("MeasAll: ${allssket[collapseState]}")
            // reset stuff
            jointState = CMatrix(jointStateSize, 1).apply {
                set(collapseState, 0, 1.0, 0.0)
            }
            hasMeasGate = true
            0
        }
        "MEASONE" -> {
            val i = readInt()
            jointState = opMatrix * jointState
            opMatrix = IN2
            var zeroProb = 0.0
            val a = CNumber()
            for (j in 0 until jointStateSize) {
                if (allss[j][i] == '0') {
                    jointState.get(j, 0, a)
                    zeroProb += a.magnitude2
                }
            }
            val c = if (Random.nextDouble() < zeroProb) '0' else {
                zeroProb = 1.0 - zeroProb
                '1'
            }
            println("MeasOne: Qubit #$i is |$c>")
            for (j in 0 until jointStateSize) {
                if (allss[j][i] != c) {
                    jointState.set(j, 0, 0.0, 0.0)
                }
            }
            COps.scale(1.0 / zeroProb, 0.0, jointState)
            hasMeasGate = true
            0
        }
        else -> super.runCmd(cmd)
    }

    override fun done() {
        if (!hasMeasGate) super.done()
    }

    override fun printResult() {
        if (hasMeasGate) {
            println("Circuit matrix unavailable due to MeasAll/MeasOne command(s).")
        } else {
            super.printResult()
        }
        println("\nFinal state: ")
        println("Init ${allssket[0]}")
        (opMatrix * jointState).printFancy2(allssket = allssket)
    }
}

class PTester(config: Config) : PTFinder(config) {
    /** 2^N by 1 column vector */
    var jointState = getJointState(config.init_state, jointStateSize)
    var hasMeasGate = false

    override fun runCmd(cmd: String) = when (cmd) {
        "MEASURE" -> runBlocking {
            val deferredOpMatrix = reduceOps()
            val i = readInt()
            val probs = mutableListOf(0.0)
            val labels = mutableListOf<Int>()
            opMatrix = deferredOpMatrix.await()
            reversedNewOps = mutableListOf(GlobalScope.async { opMatrix })
            val results = opMatrix * jointState
            val a = CNumber()
            for (j in 0 until jointStateSize) {
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
                println(allssket[labels[index]])
            }
            println()
            0
        }
        "MEASALL" -> runBlocking {
            val deferredOpMatrix = reduceOps()
            val probs = mutableListOf(0.0)
            val labels = mutableListOf<Int>()
            val a = CNumber()
            jointState = deferredOpMatrix.await() * jointState
            reversedNewOps = mutableListOf(GlobalScope.async { IN2 })
            for (j in 0 until jointStateSize) {
                jointState.get(j, 0, a)
                if (a.magnitude2 > 0) {
                    labels.add(j)
                    probs.add(a.magnitude2 + probs.last())
                }
            }
            val r = Random.nextDouble()
            val index = probs.upperBound(r)
            val collapseState = labels[index]
            println("MeasAll: ${allssket[collapseState]}")
            // reset stuff
            jointState = CMatrix(jointStateSize, 1).apply {
                set(collapseState, 0, 1.0, 0.0)
            }
            hasMeasGate = true
            0
        }
        "MEASONE" -> runBlocking {
            val deferredOpMatrix = reduceOps()
            val i = readInt()
            var zeroProb = 0.0
            val a = CNumber()
            jointState = deferredOpMatrix.await() * jointState
            reversedNewOps = mutableListOf(GlobalScope.async { IN2 })
            for (j in 0 until jointStateSize) {
                if (allss[j][i] == '0') {
                    jointState.get(j, 0, a)
                    zeroProb += a.magnitude2
                }
            }
            val c = if (Random.nextDouble() < zeroProb) '0' else {
                zeroProb = 1.0 - zeroProb
                '1'
            }
            println("MeasOne: Qubit #$i is |$c>")
            for (j in 0 until jointStateSize) {
                if (allss[j][i] != c) {
                    jointState.set(j, 0, 0.0, 0.0)
                }
            }
            COps.scale(1.0 / zeroProb, 0.0, jointState)
            hasMeasGate = true
            0
        }
        else -> super.runCmd(cmd)
    }

    override fun done() {
        if (!hasMeasGate) super.done()
        else {
            opMatrix = runBlocking { reduceOps().await() }
        }
    }

    override fun printResult() {
        if (hasMeasGate) {
            println("Circuit matrix unavailable due to MeasAll/MeasOne command(s).")
        } else {
            super.printResult()
        }
        println("\nFinal state: ")
        println("Init ${allssket[0]}")
        (opMatrix * jointState).printFancy2(allssket = allssket)
    }
}