package com.dedztbh.kuantum.ejml.matrix

/**
 * Created by DEDZTBH on 2020/09/25.
 * Project KuantumCircuitSim
 */

/** 2^(-1/2) */
const val HALF_AMPL = 0.70710678118654757273731092936941422522068023681640625

/** Don't change these constant matrices! */
val NOT = CMatrix(
    arrayOf(
        doubleArrayOf(0.0, 0.0, 1.0, 0.0),
        doubleArrayOf(1.0, 0.0, 0.0, 0.0)
    )
)
val H = CMatrix(
    arrayOf(
        doubleArrayOf(HALF_AMPL, 0.0, HALF_AMPL, 0.0),
        doubleArrayOf(HALF_AMPL, 0.0, -HALF_AMPL, 0.0)
    )
)
val I1 = COps.identity(1)
val I2 = COps.identity(2)

val KET0 = CMatrix(arrayOf(doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 0.0)))
val KET1 = CMatrix(arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0)))

val KETBRA0 = COps.diag(1.0, 0.0, 0.0, 0.0)
val KETBRA1 = COps.diag(0.0, 0.0, 1.0, 0.0)
val SQRT_NOT = CMatrix(
    arrayOf(
        doubleArrayOf(0.5, 0.5, 0.5, -0.5),
        doubleArrayOf(0.5, -0.5, 0.5, 0.5),
    )
)
val SQRT_NOT_DAG = CMatrix(2, 2).also {
    COps.transposeConjugate(SQRT_NOT, it)
}
val Y = CMatrix(
    arrayOf(
        doubleArrayOf(0.0, 0.0, 0.0, -1.0),
        doubleArrayOf(0.0, 1.0, 0.0, 0.0),
    )
)
val Z = CMatrix(
    arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, -1.0, 0.0),
    )
)
val S = CMatrix(
    arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, 0.0, 1.0),
    )
)
val T = CMatrix(
    arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, HALF_AMPL, HALF_AMPL),
    )
)
val TDag = CMatrix(2, 2).also {
    COps.transposeConjugate(T, it)
}
