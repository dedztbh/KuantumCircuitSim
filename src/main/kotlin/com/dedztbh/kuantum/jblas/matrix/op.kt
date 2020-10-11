package com.dedztbh.kuantum.jblas.matrix

import org.jblas.ComplexDoubleMatrix


/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

/**
 * Kronecker product of two complex matrices
 */

infix fun CMatrix.kron(B: CMatrix) = CMatrix(rows * B.rows, columns * B.columns).also { C ->
    var jbcol = 0
    for (j in 0 until columns) {
        // jbcol = j * B.columns
        var ibrow = 0
        for (i in 0 until rows) {
            // ibrow = i * B.rows
            val a = get(i, j)
            var c = jbcol
            for (colB in 0 until B.columns) {
                // c = jbcol + colB
                var r = ibrow
                for (rowB in 0 until B.rows) {
                    // r = ibrow + rowB
                    C.put(r++, c, a.mul(B.get(rowB, colB)))
                }
                ++c
            }
            ibrow += B.rows
        }
        jbcol += B.columns
    }
}

/**
 * Convenient pure ops
 */
inline operator fun CMatrix.plus(B: CMatrix): ComplexDoubleMatrix = add(B)

inline operator fun CMatrix.times(B: CMatrix): ComplexDoubleMatrix = mmul(B)

val CNum.magnitude2
    get() = let {
        val r = real()
        val i = imag()
        r * r + i * i
    }