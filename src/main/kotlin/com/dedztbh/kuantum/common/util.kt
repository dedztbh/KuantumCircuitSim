package com.dedztbh.kuantum.common

import java.io.BufferedReader
import java.util.*
import kotlin.math.pow

/**
 * Created by DEDZTBH on 2020/09/25.
 * Project KuantumCircuitSim
 */

/** IO */
@JvmField
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

fun allStates(n: Int) =
    Array(2.0.pow(n).toInt()) {
        List(n) { i -> (it shr i) and 1 }.asReversed()
    }


/** Data Structure */
class TODOMap<K, V> : MutableMap<K, V> {
    override val size
        get() = throw NotImplementedError()
    override val entries
        get() = throw NotImplementedError()
    override val keys
        get() = throw NotImplementedError()
    override val values
        get() = throw NotImplementedError()

    override fun containsKey(key: K): Boolean = throw NotImplementedError()
    override fun containsValue(value: V): Boolean = throw NotImplementedError()
    override fun get(key: K): V? = throw NotImplementedError()
    override fun isEmpty(): Boolean = throw NotImplementedError()
    override fun clear() = throw NotImplementedError()
    override fun put(key: K, value: V): V? = throw NotImplementedError()
    override fun putAll(from: Map<out K, V>) = throw NotImplementedError()
    override fun remove(key: K): V? = throw NotImplementedError()
}