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
        ArgType.Boolean,
        shortName = "m",
        description = "Read input file as circuit matrix (binary) instead of commands if present"
    ).default(false)
}