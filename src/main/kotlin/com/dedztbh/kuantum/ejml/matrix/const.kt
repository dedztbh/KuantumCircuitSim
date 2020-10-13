package com.dedztbh.kuantum.ejml.matrix

import com.dedztbh.kuantum.common.matrix.HALF_AMPL
import org.ejml.data.ZMatrixRMaj

/**
 * Created by DEDZTBH on 2020/09/25.
 * Project KuantumCircuitSim
 */

/** Don't change these constant matrices! */
@JvmField
val NOT: ZMatrixRMaj = CMatrix(
    arrayOf(
        doubleArrayOf(0.0, 0.0, 1.0, 0.0),
        doubleArrayOf(1.0, 0.0, 0.0, 0.0)
    )
)

@JvmField
val H: ZMatrixRMaj = CMatrix(
    arrayOf(
        doubleArrayOf(HALF_AMPL, 0.0, HALF_AMPL, 0.0),
        doubleArrayOf(HALF_AMPL, 0.0, -HALF_AMPL, 0.0)
    )
)

@JvmField
val I1: ZMatrixRMaj = COps.identity(1)

@JvmField
val I2: ZMatrixRMaj = COps.identity(2)

@JvmField
val KET0: ZMatrixRMaj = CMatrix(arrayOf(doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 0.0)))

@JvmField
val KET1: ZMatrixRMaj = CMatrix(arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0)))

@JvmField
val KETBRA0: ZMatrixRMaj = COps.diag(1.0, 0.0, 0.0, 0.0)

@JvmField
val KETBRA1: ZMatrixRMaj = COps.diag(0.0, 0.0, 1.0, 0.0)

@JvmField
val SQRT_NOT: ZMatrixRMaj = CMatrix(
    arrayOf(
        doubleArrayOf(0.5, 0.5, 0.5, -0.5),
        doubleArrayOf(0.5, -0.5, 0.5, 0.5),
    )
)

@JvmField
val SQRT_NOT_DAG: ZMatrixRMaj = CMatrix(2, 2).also {
    COps.transposeConjugate(SQRT_NOT, it)
}

@JvmField
val Y: ZMatrixRMaj = CMatrix(
    arrayOf(
        doubleArrayOf(0.0, 0.0, 0.0, -1.0),
        doubleArrayOf(0.0, 1.0, 0.0, 0.0),
    )
)

@JvmField
val Z: ZMatrixRMaj = CMatrix(
    arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, -1.0, 0.0),
    )
)

@JvmField
val S: ZMatrixRMaj = CMatrix(
    arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, 0.0, 1.0),
    )
)

@JvmField
val T: ZMatrixRMaj = CMatrix(
    arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, HALF_AMPL, HALF_AMPL),
    )
)

@JvmField
val TDag: ZMatrixRMaj = CMatrix(2, 2).also {
    COps.transposeConjugate(T, it)
}
