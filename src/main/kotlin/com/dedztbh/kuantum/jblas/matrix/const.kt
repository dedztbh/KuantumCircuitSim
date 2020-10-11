package com.dedztbh.kuantum.jblas.matrix

import org.jblas.ComplexDoubleMatrix
import org.jblas.DoubleMatrix

/**
 * Created by DEDZTBH on 2020/09/25.
 * Project KuantumCircuitSim
 */

/** 2^(-1/2) */
const val HALF_AMPL = 0.70710678118654757273731092936941422522068023681640625

/** Don't change these constant matrices! */
val NOT: ComplexDoubleMatrix = CMatrix(
    2, 2,
    0.0, 0.0, 1.0, 0.0,
    1.0, 0.0, 0.0, 0.0
).transpose()
val H: ComplexDoubleMatrix = CMatrix(
    2, 2,
    HALF_AMPL, 0.0, HALF_AMPL, 0.0,
    HALF_AMPL, 0.0, -HALF_AMPL, 0.0
).transpose()
val I1 = CMatrix(DoubleMatrix.eye(1), null)
val I2 = CMatrix(DoubleMatrix.eye(2), null)

val KET0: ComplexDoubleMatrix = CMatrix(
    2, 1,
    1.0, 0.0,
    0.0, 0.0
).transpose()
val KET1: ComplexDoubleMatrix = CMatrix(
    2, 1,
    0.0, 0.0,
    1.0, 0.0
).transpose()

val KETBRA0: ComplexDoubleMatrix = CMatrix.diag(CMatrix(doubleArrayOf(1.0, 0.0, 0.0, 0.0)).transpose())
val KETBRA1: ComplexDoubleMatrix = CMatrix.diag(CMatrix(doubleArrayOf(0.0, 0.0, 1.0, 0.0)).transpose())
val SQRT_NOT: ComplexDoubleMatrix = CMatrix(
    2, 2,
    0.5, 0.5, 0.5, -0.5,
    0.5, -0.5, 0.5, 0.5
).transpose()
val SQRT_NOT_DAG: ComplexDoubleMatrix = SQRT_NOT.hermitian()
val Y: ComplexDoubleMatrix = CMatrix(
    2, 2,
    0.0, 0.0, 0.0, -1.0,
    0.0, 1.0, 0.0, 0.0
).transpose()
val Z: ComplexDoubleMatrix = CMatrix(
    2, 2,
    1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, -1.0, 0.0
).transpose()
val S: ComplexDoubleMatrix = CMatrix(
    2, 2,
    1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, 0.0, 1.0
).transpose()
val T: ComplexDoubleMatrix = CMatrix(
    2, 2,
    1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, HALF_AMPL, HALF_AMPL
).transpose()
val TDag: ComplexDoubleMatrix = T.hermitian()
