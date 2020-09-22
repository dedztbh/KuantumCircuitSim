package util

import org.ejml.data.Complex_F64
import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.CommonOps_ZDRM

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

/**
 * Kronecker product of two ZDRM matrices
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

/**
 * Convenient pure ops
 */
operator fun ZMatrixRMaj.plus(B: ZMatrixRMaj) =
    ZMatrixRMaj(numRows, numCols).also { CommonOps_ZDRM.add(this, B, it) }

operator fun ZMatrixRMaj.times(B: ZMatrixRMaj) =
    ZMatrixRMaj(numRows, B.numCols).also { CommonOps_ZDRM.mult(this, B, it) }


/**
 * Useful constants
 */
typealias Matrix = ZMatrixRMaj
typealias Ops = CommonOps_ZDRM

/** 2^(-1/2) */
const val HALF_AMPL = 0.70710678118654757273731092936941422522068023681640625

/** Don't change these constant matrices! */
val NOT = Matrix(
    arrayOf(
        doubleArrayOf(0.0, 0.0, 1.0, 0.0),
        doubleArrayOf(1.0, 0.0, 0.0, 0.0)
    )
)
val H = Matrix(
    arrayOf(
        doubleArrayOf(HALF_AMPL, 0.0, HALF_AMPL, 0.0),
        doubleArrayOf(HALF_AMPL, 0.0, -HALF_AMPL, 0.0)
    )
)
val I1 = Ops.identity(1)
val I2 = Ops.identity(2)

val KETBRA0 = Ops.diag(1.0, 0.0, 0.0, 0.0)
val KETBRA1 = Ops.diag(0.0, 0.0, 1.0, 0.0)
val SQRT_NOT = Matrix(
    arrayOf(
        doubleArrayOf(0.5, 0.5, 0.5, -0.5),
        doubleArrayOf(0.5, -0.5, 0.5, 0.5),
    )
)
val SQRT_NOT_DAG = ZMatrixRMaj(2, 2).also {
    Ops.transposeConjugate(SQRT_NOT, it)
}
val Z = Matrix(
    arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, -1.0, 0.0),
    )
)
val S = Matrix(
    arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, 0.0, 1.0),
    )
)
val T = Matrix(
    arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, HALF_AMPL, HALF_AMPL),
    )
)
val TDag = ZMatrixRMaj(2, 2).also {
    Ops.transposeConjugate(T, it)
}