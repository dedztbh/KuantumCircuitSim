package com.dedztbh.kuantum.jblas.matrix

import com.dedztbh.kuantum.common.matrix.HALF_AMPL
import org.jblas.ComplexDoubleMatrix
import org.jblas.DoubleMatrix

/**
 * Created by DEDZTBH on 2020/09/25.
 * Project KuantumCircuitSim
 */

/** Don't change these constant matrices!
 * JBLAS is column-major so have to transpose non-symmetric matrices */

/** Don't need transpose for vectors and symmetric matrices */
val NOT: ComplexDoubleMatrix = CMatrix(
    2, 2,
    0.0, 0.0, 1.0, 0.0,
    1.0, 0.0, 0.0, 0.0
)
val H: ComplexDoubleMatrix = CMatrix(
    2, 2,
    HALF_AMPL, 0.0, HALF_AMPL, 0.0,
    HALF_AMPL, 0.0, -HALF_AMPL, 0.0
)
val I1 = CMatrix(DoubleMatrix.eye(1), null)
val I2 = CMatrix(DoubleMatrix.eye(2), null)

val KET0: ComplexDoubleMatrix = CMatrix(
    2, 1,
    1.0, 0.0,
    0.0, 0.0
)
val KET1: ComplexDoubleMatrix = CMatrix(
    2, 1,
    0.0, 0.0,
    1.0, 0.0
)

val KETBRA0: ComplexDoubleMatrix = CMatrix.diag(CMatrix(doubleArrayOf(1.0, 0.0, 0.0, 0.0)))
val KETBRA1: ComplexDoubleMatrix = CMatrix.diag(CMatrix(doubleArrayOf(0.0, 0.0, 1.0, 0.0)))
val SQRT_NOT: ComplexDoubleMatrix = CMatrix(
    2, 2,
    0.5, 0.5, 0.5, -0.5,
    0.5, -0.5, 0.5, 0.5
)
val SQRT_NOT_DAG: ComplexDoubleMatrix = SQRT_NOT.hermitian()
val Z: ComplexDoubleMatrix = CMatrix(
    2, 2,
    1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, -1.0, 0.0
)
val S: ComplexDoubleMatrix = CMatrix(
    2, 2,
    1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, 0.0, 1.0
)
val T: ComplexDoubleMatrix = CMatrix(
    2, 2,
    1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, HALF_AMPL, HALF_AMPL
)
val TDag: ComplexDoubleMatrix = T.hermitian()

/** These need transpose */
val Y: ComplexDoubleMatrix = CMatrix(
    2, 2,
    0.0, 0.0, 0.0, -1.0,
    0.0, 1.0, 0.0, 0.0
).transpose()

