package operator

import Config

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

/**
 * Children class must have a constructor that takes an Config object
 */
interface Operator {
    fun runCmd(cmd: String): Int
    fun printResult()
    fun done() {}

    companion object {
        fun get(config: Config) =
            Class.forName("operator.${if (config.concurrent) "P" else ""}${config.operator}")
                .getDeclaredConstructor(Config::class.java)
                .newInstance(config) as Operator
    }
}