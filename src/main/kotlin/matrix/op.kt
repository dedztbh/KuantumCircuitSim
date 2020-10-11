package matrix


/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

/**
 * Kronecker product of two complex matrices
 */

infix fun CMatrix.kron(B: CMatrix) = CMatrix(numRows * B.numRows, numCols * B.numCols).also { C ->
    val a = CNum()
    val b = CNum()
    val ab = CNum()
    var ibrow = 0
    for (i in 0 until numRows) {
        var jbcol = 0
        for (j in 0 until numCols) {
            get(i, j, a)
            var r = ibrow
            for (rowB in 0 until B.numRows) {
                var c = jbcol
                for (colB in 0 until B.numCols) {
                    B.get(rowB, colB, b)
                    CMath.multiply(a, b, ab)
                    C.set(r, c++, ab.real, ab.imaginary)
                }
                ++r
            }
            jbcol += B.numCols
        }
        ibrow += B.numRows
    }
}

/**
 * Convenient pure ops
 */
inline operator fun CMatrix.plus(B: CMatrix) =
    CMatrix(numRows, numCols).also { COps.add(this, B, it) }

inline operator fun CMatrix.times(B: CMatrix) =
    CMatrix(numRows, B.numCols).also { COps.mult(this, B, it) }
