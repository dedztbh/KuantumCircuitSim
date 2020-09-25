package matrix

import org.ejml.UtilEjml
import org.ejml.data.MatrixSparse
import org.ejml.data.MatrixType
import org.ejml.ops.MatrixIO
import java.io.PrintStream
import java.text.DecimalFormat


/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

/**
 * Kronecker product of two complex matrices
 */
infix fun CMatrix.kron(B: CMatrix) = let { A ->
    val numColsC = A.numCols * B.numCols
    val numRowsC = A.numRows * B.numRows

    val C = CMatrix(numRowsC, numColsC)

    val acomplex = CNumber()
    val bcomplex = CNumber()
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
operator fun CMatrix.plus(B: CMatrix) =
    CMatrix(numRows, numCols).also { COps.add(this, B, it) }

operator fun CMatrix.times(B: CMatrix) =
    CMatrix(numRows, B.numCols).also { COps.mult(this, B, it) }


/** Printing util */
object MyMatrixIO {
    fun getMatrixType(mat: CMatrix): String {
        return if (mat.type == MatrixType.UNSPECIFIED) {
            mat.javaClass.simpleName
        } else {
            mat.type.name
        }
    }

    fun printTypeSize(out: PrintStream, mat: CMatrix) {
        if (mat is MatrixSparse) {
            val m = mat as MatrixSparse
            out.println(
                "Type = " + getMatrixType(mat) + " , rows = " + mat.getNumRows() +
                        " , cols = " + mat.getNumCols() + " , nz_length = " + m.nonZeroLength
            )
        } else {
            out.println("Type = " + getMatrixType(mat) + " , rows = " + mat.getNumRows() + " , cols = " + mat.getNumCols())
        }
    }

    fun padSpace(builder: java.lang.StringBuilder, length: Int): String {
        builder.delete(0, builder.length)
        for (i in 0 until length) {
            builder.append(' ')
        }
        return builder.toString()
    }
}

fun CMatrix.printFancy2(
    out: PrintStream = System.out,
    length: Int = MatrixIO.DEFAULT_LENGTH,
    allssr: List<String>
) = let { mat ->
    MyMatrixIO.printTypeSize(out, mat)
    val format = DecimalFormat("#")
    val builder = StringBuilder(length)
    val cols = mat.numCols
    val c = CNumber()
    var i = 0
    for (y in 0 until mat.numRows) {
        for (x in 0 until cols) {
            mat[y, x, c]
            var real = UtilEjml.fancyString(c.real, format, length, 4)
            var img = UtilEjml.fancyString(c.imaginary, format, length, 4)
            real += MyMatrixIO.padSpace(builder, length - real.length)
            img = img + "i" + MyMatrixIO.padSpace(builder, length - img.length)
            out.print("${allssr[i++]}: $real + $img")
            if (x < mat.numCols - 1) {
                out.print(" , ")
            }
        }
        out.println()
    }
}
