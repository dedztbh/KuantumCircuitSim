package com.dedztbh.kuantum

import com.lukaskusik.coroutines.transformations.reduce.reduceParallel
import kotlinx.coroutines.runBlocking
import org.jblas.ComplexDoubleMatrix
import org.jblas.DoubleMatrix
import kotlin.system.measureTimeMillis

/**
 * Created by DEDZTBH on 2020/10/08.
 * Project KuantumCircuitSim
 */

fun main() {
    // Slower than numpy, I'm not happy :(
    val N = 1000
    val mat = ComplexDoubleMatrix(DoubleMatrix.rand(N, N), DoubleMatrix.rand(N, N))
    measureTimeMillis {
//        repeat(500) {
//            mat.mmul(mat)
//        }
        runBlocking {
            List(500) { mat }.reduceParallel { d1, d2 ->
                d1.mmul(d2)
            }
        }
    }.let {
        println(it.toDouble() / 1000.0)
    }
}