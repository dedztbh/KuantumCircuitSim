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
@JvmField
val NOT: ComplexDoubleMatrix = CMatrix(
    2, 2,
    0.0, 0.0, 1.0, 0.0,
    1.0, 0.0, 0.0, 0.0
)

@JvmField
val H: ComplexDoubleMatrix = CMatrix(
    2, 2,
    HALF_AMPL, 0.0, HALF_AMPL, 0.0,
    HALF_AMPL, 0.0, -HALF_AMPL, 0.0
)

@JvmField
val I1 = CMatrix(DoubleMatrix.eye(1), null)

@JvmField
val I2 = CMatrix(DoubleMatrix.eye(2), null)

@JvmField
val KET0: ComplexDoubleMatrix = CMatrix(
    2, 1,
    1.0, 0.0,
    0.0, 0.0
)

@JvmField
val KET1: ComplexDoubleMatrix = CMatrix(
    2, 1,
    0.0, 0.0,
    1.0, 0.0
)

@JvmField
val KETBRA0: ComplexDoubleMatrix = CMatrix.diag(CMatrix(doubleArrayOf(1.0, 0.0, 0.0, 0.0)))

@JvmField
val KETBRA1: ComplexDoubleMatrix = CMatrix.diag(CMatrix(doubleArrayOf(0.0, 0.0, 1.0, 0.0)))

@JvmField
val SQRT_NOT: ComplexDoubleMatrix = CMatrix(
    2, 2,
    0.5, 0.5, 0.5, -0.5,
    0.5, -0.5, 0.5, 0.5
)

@JvmField
val SQRT_NOT_DAG: ComplexDoubleMatrix = SQRT_NOT.hermitian()

@JvmField
val Z: ComplexDoubleMatrix = CMatrix(
    2, 2,
    1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, -1.0, 0.0
)

@JvmField
val S: ComplexDoubleMatrix = CMatrix(
    2, 2,
    1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, 0.0, 1.0
)

@JvmField
val T: ComplexDoubleMatrix = CMatrix(
    2, 2,
    1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, HALF_AMPL, HALF_AMPL
)

@JvmField
val TDag: ComplexDoubleMatrix = T.hermitian()

/** These need transpose */
@JvmField
val Y: ComplexDoubleMatrix = CMatrix(
    2, 2,
    0.0, 0.0, 0.0, -1.0,
    0.0, 1.0, 0.0, 0.0
).transpose()

fun map0Ctrl(cmd: String) = when (cmd) {
    "NOT" -> NOT
    "HADAMARD", "H" -> H
    "CNOT" -> NOT
    "Y" -> Y
    "Z" -> Z
    "S" -> S
    "T" -> T
    "TDAG" -> TDag
    "SQRTNOT" -> SQRT_NOT
    "SQRTNOTDAG" -> SQRT_NOT_DAG
    else -> null
}