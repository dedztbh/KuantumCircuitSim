package matrix


/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

/**
 * Kronecker product of two complex matrices
 */


infix fun CMatrix.kron(B: CMatrix) = CMatrix(numRows * B.numRows, numCols * B.numCols).also { C ->
    val acomplex = CNumber()
    val bcomplex = CNumber()
    for (i in 0 until numRows) {
        for (j in 0 until numCols) {
            get(i, j, acomplex)
            for (rowB in 0 until B.numRows) {
                for (colB in 0 until B.numCols) {
                    B.get(rowB, colB, bcomplex)
                    acomplex.times(bcomplex).let { aB ->
                        C.set(i * B.numRows + rowB, j * B.numCols + colB, aB.real, aB.imaginary)
                    }
                }
            }
        }
    }
}

/**
 * Convenient pure ops
 */
operator fun CMatrix.plus(B: CMatrix) =
    CMatrix(numRows, numCols).also { COps.add(this, B, it) }

operator fun CMatrix.times(B: CMatrix) =
    CMatrix(numRows, B.numCols).also { COps.mult(this, B, it) }
