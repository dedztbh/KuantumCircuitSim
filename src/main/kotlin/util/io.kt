package util

import java.io.BufferedReader
import java.util.*

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */
var _tokenizer: StringTokenizer = StringTokenizer("")
fun read(): String {
    while (!_tokenizer.hasMoreTokens())
        _tokenizer = StringTokenizer(reader.readLine() ?: return "", " ")
    return _tokenizer.nextToken()
}

fun readInt() = read().toInt()
fun readDouble() = read().toDouble()

lateinit var reader: BufferedReader