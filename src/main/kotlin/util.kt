import java.io.BufferedReader
import java.util.*

/**
 * Created by DEDZTBH on 2020/09/25.
 * Project KuantumCircuitSim
 */

/** IO */
var _tokenizer: StringTokenizer = StringTokenizer("")
fun read(): String {
    while (!_tokenizer.hasMoreTokens())
        _tokenizer = StringTokenizer(reader.readLine() ?: return "", " ")
    return _tokenizer.nextToken()
}

fun readInt() = read().toInt()
fun readDouble() = read().toDouble()
fun readFloat() = read().toFloat()

lateinit var reader: BufferedReader


/** Algorithm */
fun <T : Comparable<T>> List<T>.upperBound(key: T): Int {
    var low = 0
    var high = size - 1
    while (low < high) {
        val mid = low + (high - low + 1) / 2
        if (this[mid] <= key) {
            low = mid
        } else {
            high = mid - 1
        }
    }
    return low
}