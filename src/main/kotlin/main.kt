import kotlinx.cli.ArgParser
import kotlinx.coroutines.runBlocking
import operator.Operator
import java.io.File

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

fun main(args: Array<String>) = runBlocking {
    val parser = ArgParser("java -jar KuantumCircuitSim.jar")
    val config = Config(parser)
    parser.parse(args)

    reader = File(config.input).bufferedReader()

    val operator = Operator.get(config, this)

    var cmd = read()
    while (true) {
        if (cmd.isEmpty() || operator.runCmd(cmd.toUpperCase()) != 0) break
        cmd = read()
    }

    operator.done()
    operator.printResult()
}