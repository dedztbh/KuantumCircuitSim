package com.dedztbh.kuantum.ejml.matrix

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import org.ejml.UtilEjml
import org.ejml.ops.MatrixIO
import java.io.*
import java.text.DecimalFormat

/**
 * Created by DEDZTBH on 2020/09/27.
 * Project KuantumCircuitSim
 */

fun padSpace(builder: java.lang.StringBuilder, length: Int): String {
    builder.delete(0, builder.length)
    for (i in 0 until length) {
        builder.append(' ')
    }
    return builder.toString()
}

fun saveCsv(A: CMatrix, fileName: String) =
    CsvWriter().writeAll(A.data.asList().windowed(A.rowStride, A.rowStride), fileName)


fun loadCsv(fileName: String): CMatrix =
    CMatrix(CsvReader().open(fileName) {
        readAllAsSequence().map {
            DoubleArray(it.size) { i -> it[i].toDouble() }
        }.toMutableList().toTypedArray()
    })

fun <T> saveBin(A: T, fileName: String) {
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

@Suppress("UNCHECKED_CAST")
fun <T> loadBin(fileName: String): T {
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

fun CMatrix.printFancy2(
    allssket: List<String>,
    out: PrintStream = System.out,
    length: Int = MatrixIO.DEFAULT_LENGTH,
) {
    val format = DecimalFormat("#")
    val builder = StringBuilder(length)
    val cols = numCols
    val c = CNum()
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