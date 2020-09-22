package util

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

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