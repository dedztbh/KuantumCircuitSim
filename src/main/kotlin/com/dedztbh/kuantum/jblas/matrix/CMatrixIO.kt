package com.dedztbh.kuantum.jblas.matrix

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import java.io.PrintStream
import java.io.PrintWriter
import java.text.DecimalFormat
import kotlin.math.absoluteValue

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
        s.append("\n")
    }
    return s.toString()
}

@JvmField
val fmt = DecimalFormat("#.#########")
fun CNum.toStringFancy(): String {
    val r = real()
    val i = imag()
    val rstr = "${if (r < 0) "" else ' '}${fmt.format(r)}"
    val istr = " ${if (i < 0) '-' else '+'} ${fmt.format(i.absoluteValue)}i"
    return rstr.padEnd(12) + istr.padEnd(15)
}

fun CMatrix.printFancy(
    allssket: List<String>? = null,
    o: PrintStream = System.out
) = PrintWriter(o, false).apply {
    for (i in 0 until rows) {
        allssket?.let { print("${it[i]}: ") }
        for (j in 0 until columns) {
            print(get(i, j).toStringFancy())
            if (j < columns - 1) print(", ")
        }
        println()
    }
}.flush()