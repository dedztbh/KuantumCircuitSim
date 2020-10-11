package com.dedztbh.kuantum.common

import kotlinx.cli.ArgParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

fun main(args: Array<String>, lib: String) {
    val parser = ArgParser(CMD)
    val config = Config(parser)
    parser.parse(args)

    reader = File(config.input).bufferedReader()

    runBlocking(Dispatchers.Default) {
        val operator = Operator.get(config, this, lib)

        var cmd = read()
        while (true) {
            if (cmd.isEmpty() || operator.runCmd(cmd.toUpperCase()) != 0) break
            cmd = read()
        }

        operator.done()
        operator.printResult()
    }
}