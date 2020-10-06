package operator

import Config
import kotlinx.coroutines.CoroutineScope

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

/**
 * Children class must have a constructor that takes an Config object and a coroutine scope
 */
interface Operator {
    suspend fun runCmd(cmd: String): Int
    suspend fun printResult()
    suspend fun done()

    companion object {
        @JvmStatic
        fun get(config: Config, scope: CoroutineScope) =
            Class.forName("operator.${if (config.sequential) "" else "P"}${config.operator}")
                .getDeclaredConstructor(Config::class.java, CoroutineScope::class.java)
                .newInstance(config, scope) as Operator
    }
}