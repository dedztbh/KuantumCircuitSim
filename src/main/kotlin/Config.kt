import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional


/**
 * Created by DEDZTBH on 2020/09/27.
 * Project KuantumCircuitSim
 */

const val DEFAULT_N = 5

class Config(parser: ArgParser) {
    val input by parser.argument(ArgType.String, description = "Input file")
    val operator by parser.argument(ArgType.String, description = "Operator name")
    val N by parser.argument(ArgType.Int, description = "Number of qubits").optional().default(DEFAULT_N)
    val output by parser.option(
        ArgType.String,
        shortName = "o",
        description = "Output file to save circuit matrix (binary) if specified"
    )
        .default("")
    val input_matrix by parser.option(
        ArgType.String,
        shortName = "m",
        description = "Read circuit matrix (binary) as initial matrix if specified, use an empty file for input if no extra commands"
    ).default("")
    val no_t by parser.option(
        ArgType.Boolean,
        shortName = "q",
        description = "Do not print circuit matrix in commandline after simulation if present"
    ).default(false)
}