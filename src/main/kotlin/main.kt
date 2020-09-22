import operator.Operator
import util.read
import util.reader
import java.io.File

/**
 * Created by DEDZTBH on 2020/09/22.
 * Project KuantumCircuitSim
 */

const val DEFAULT_N = 5


fun main(args: Array<String>) {
    if (args.size < 2) throw IllegalArgumentException("Please provide input file and operator name")

    reader = File(args[0]).inputStream().bufferedReader()

    val operator = Operator.get(args[1], if (args.size > 2) args[2].toInt() else DEFAULT_N)

    var cmd = read()
    while (true) {
        if (cmd.isEmpty() || operator.runCmd(cmd) != 0) break
        cmd = read()
    }
    operator.done()
    operator.printResult()
}