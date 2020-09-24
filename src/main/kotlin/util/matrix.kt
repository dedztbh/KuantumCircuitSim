package util

import org.ejml.UtilEjml
import org.ejml.data.Complex_F64
import org.ejml.data.MatrixSparse
import org.ejml.data.MatrixType
import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.CommonOps_ZDRM
import org.ejml.ops.MatrixIO
import java.io.PrintStream
import java.text.DecimalFormat


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

val KET0 = Matrix(arrayOf(doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 0.0)))
val KET1 = Matrix(arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0)))

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

/** Printing util */
object MyMatrixIO {
    fun getMatrixType(mat: Matrix): String {
        return if (mat.type == MatrixType.UNSPECIFIED) {
            mat.javaClass.simpleName
        } else {
            mat.type.name
        }
    }

    fun printTypeSize(out: PrintStream, mat: Matrix) {
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

fun ZMatrixRMaj.printFancy2(
    out: PrintStream = System.out,
    length: Int = MatrixIO.DEFAULT_LENGTH,
    allssr: List<String>
) = this.let { mat ->
    MyMatrixIO.printTypeSize(out, mat)
    val format = DecimalFormat("#")
    val builder = StringBuilder(length)
    val cols = mat.numCols
    val c = Complex_F64()
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
