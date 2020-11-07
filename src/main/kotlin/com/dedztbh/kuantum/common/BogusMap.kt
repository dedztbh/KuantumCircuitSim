package com.dedztbh.kuantum.common

/**
 * Created by DEDZTBH on 2020/11/07.
 * Project KuantumCircuitSim
 */

/** O(1) space map! But please don't use it in any ways or warranty will void */
object BogusMap : MutableMap<Nothing, Nothing> {
    override val size
        get() = throw NotImplementedError()
    override val entries
        get() = throw NotImplementedError()
    override val keys
        get() = throw NotImplementedError()
    override val values
        get() = throw NotImplementedError()

    override fun containsKey(key: Nothing): Boolean = throw NotImplementedError()
    override fun containsValue(value: Nothing): Boolean = throw NotImplementedError()
    override fun get(key: Nothing): Nothing? = throw NotImplementedError()
    override fun isEmpty(): Boolean = throw NotImplementedError()
    override fun clear() = throw NotImplementedError()
    override fun put(key: Nothing, value: Nothing): Nothing? = throw NotImplementedError()
    override fun putAll(from: Map<out Nothing, Nothing>) = throw NotImplementedError()
    override fun remove(key: Nothing): Nothing? = throw NotImplementedError()
}

/** Fake constructor */
@Suppress("UNCHECKED_CAST")
fun <K, V> BogusMap(): MutableMap<K, V> = BogusMap as MutableMap<K, V>