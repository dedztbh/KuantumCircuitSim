package com.dedztbh.kuantum.jblas.matrix

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import java.io.PrintStream
import java.io.PrintWriter

/**
 * Created by DEDZTBH on 2020/09/27.
 * Project KuantumCircuitSim
 */

fun saveCsv(A: CMatrix, fileName: String) {
    val rowStride = A.columns * 2
    CsvWriter().writeAll(A.transpose().data.asList().windowed(rowStride, rowStride), fileName)
}

fun loadCsv(fileName: String): CMatrix =
    CsvReader().open(fileName) {
        val m = readAllAsSequence().toList()
        CMatrix(m[0].size / 2, m.size).apply {
            data = m.flatMap { it.map { it.toDouble() } }.toDoubleArray()
        }.transpose()
    }

fun CMatrix.toStringFancy(
    allssket: List<String>? = null
): String {
    val s = StringBuilder()
    for (i in 0 until rows) {
        allssket?.let { s.append("${it[i]}: ") }
        for (j in 0 until columns) {
            s.append(get(i, j))
            if (j < columns - 1) s.append(", ")
        }
        if (i < rows - 1) s.append("\n")
    }
    return s.toString()
}

fun CMatrix.printFancy(
    allssket: List<String>? = null,
    o: PrintStream = System.out
) = PrintWriter(o, false).apply {
    for (i in 0 until rows) {
        allssket?.let { print("${it[i]}: ") }
        for (j in 0 until columns) {
            print(get(i, j))
            if (j < columns - 1) print(", ")
        }
        if (i < rows - 1) print("\n")
    }
}.flush()