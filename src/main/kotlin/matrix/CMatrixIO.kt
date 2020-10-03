package matrix

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import org.ejml.UtilEjml
import org.ejml.data.MatrixSparse
import org.ejml.data.MatrixType
import org.ejml.ops.MatrixIO
import java.io.*
import java.text.DecimalFormat

/**
 * Created by DEDZTBH on 2020/09/27.
 * Project KuantumCircuitSim
 */

object CMatrixIO {
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

    fun saveBin(A: CMatrix, fileName: String) {
        val fileStream = FileOutputStream(fileName)
        val stream = ObjectOutputStream(fileStream)
        try {
            stream.writeObject(A)
            stream.flush()
        } finally {
            // clean up
            fileStream.use {
                stream.close()
            }
        }
    }

    fun saveCsv(A: CMatrix, fileName: String) =
        CsvWriter().writeAll(A.data.asList().windowed(A.rowStride, A.rowStride), fileName)

    @Suppress("UNCHECKED_CAST")
    fun <T : CMatrix> loadBin(fileName: String): T {
        val fileStream = FileInputStream(fileName)
        val stream = ObjectInputStream(fileStream)
        val ret: T
        try {
            ret = stream.readObject() as T
            if (stream.available() != 0) {
                throw RuntimeException("File not completely read?")
            }
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
        stream.close()
        return ret
    }

    fun loadCsv(fileName: String): CMatrix =
        CMatrix(CsvReader().open(fileName) {
            readAllAsSequence().map {
                it.map { s ->
                    s.trim().toDouble()
                }.toDoubleArray()
            }.toMutableList().toTypedArray()
        })

    fun CMatrix.printFancy2(
        out: PrintStream = System.out,
        length: Int = MatrixIO.DEFAULT_LENGTH,
        allssket: List<String>
    ) {
//        printTypeSize(out, this)
        val format = DecimalFormat("#")
        val builder = StringBuilder(length)
        val cols = numCols
        val c = CNumber()
        var i = 0
        for (y in 0 until numRows) {
            for (x in 0 until cols) {
                get(y, x, c)
                var real = UtilEjml.fancyString(c.real, format, length, 4)
                var img = UtilEjml.fancyString(c.imaginary, format, length, 4)
                real += padSpace(builder, length - real.length)
                img = img + "i" + padSpace(builder, length - img.length)
                out.print("${allssket[i++]}: $real + $img")
                if (x < numCols - 1) {
                    out.print(" , ")
                }
            }
            out.println()
        }
    }

}