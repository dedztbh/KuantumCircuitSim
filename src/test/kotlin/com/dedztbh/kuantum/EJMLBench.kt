package com.dedztbh.kuantum

import org.ejml.data.CMatrixRMaj
import org.ejml.data.Complex_F32
import org.ejml.data.Complex_F64
import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.CommonOps_CDRM
import org.ejml.dense.row.CommonOps_ZDRM
import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 * Created by DEDZTBH on 2020/09/25.
 * Project KuantumCircuitSim
 */

infix fun ZMatrixRMaj.kron(B: ZMatrixRMaj) = let { A ->
    val numColsC = A.numCols * B.numCols
    val numRowsC = A.numRows * B.numRows

    val C = ZMatrixRMaj(numRowsC, numColsC)

    val acomplex = Complex_F64()
    val bcomplex = Complex_F64()
    for (i in 0 until A.numRows) {
        for (j in 0 until A.numCols) {
            A.get(i, j, acomplex)
            for (rowB in 0 until B.numRows) {
                for (colB in 0 until B.numCols) {
                    B.get(rowB, colB, bcomplex)
                    val aB = acomplex.times(bcomplex)
                    C.set(i * B.numRows + rowB, j * B.numCols + colB, aB.real, aB.imaginary)
                }
            }
        }
    }

    C
}

infix fun CMatrixRMaj.kron(B: CMatrixRMaj) = let { A ->
    val numColsC = A.numCols * B.numCols
    val numRowsC = A.numRows * B.numRows

    val C = CMatrixRMaj(numRowsC, numColsC)

    val acomplex = Complex_F32()
    val bcomplex = Complex_F32()
    for (i in 0 until A.numRows) {
        for (j in 0 until A.numCols) {
            A.get(i, j, acomplex)
            for (rowB in 0 until B.numRows) {
                for (colB in 0 until B.numCols) {
                    B.get(rowB, colB, bcomplex)
                    val aB = acomplex.times(bcomplex)
                    C.set(i * B.numRows + rowB, j * B.numCols + colB, aB.real, aB.imaginary)
                }
            }
        }
    }

    C
}

fun main() {
    val N = 100
    val t = 5

    val ad = ZMatrixRMaj(Array(N) { DoubleArray(N * 2) { Random.nextDouble() } })
    val bd = ZMatrixRMaj(Array(N) { DoubleArray(N * 2) { Random.nextDouble() } })

    val af = CMatrixRMaj(Array(N) { FloatArray(N * 2) { Random.nextFloat() } })
    val bf = CMatrixRMaj(Array(N) { FloatArray(N * 2) { Random.nextFloat() } })

    LongArray(t) {
        measureTimeMillis {
            val c = ZMatrixRMaj(ad.numRows, bd.numCols).also { CommonOps_ZDRM.mult(ad, bd, it) }
            val d = ad kron bd
        }
    }.average().let(::println)

    LongArray(t) {
        measureTimeMillis {
            val c = CMatrixRMaj(af.numRows, bf.numCols).also { CommonOps_CDRM.mult(af, bf, it) }
            val d = af kron bf
        }
    }.average().let(::println)
}