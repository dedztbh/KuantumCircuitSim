package com.dedztbh.kuantum.common

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional

/**
 * Created by DEDZTBH on 2020/09/27.
 * Project KuantumCircuitSim
 */

const val DEFAULT_N = 5

const val CMD = "java -jar Kuantum.jar"

class Config(parser: ArgParser) {
    val input by parser.argument(ArgType.String, description = "Input file")
    val operator by parser.argument(ArgType.String, description = "Operator name")
    val N by parser.argument(ArgType.Int, description = "Number of qubits").optional().default(DEFAULT_N)
    val output by parser.option(
        ArgType.String,
        shortName = "o",
        description = "Output file to save circuit matrix (csv) if specified"
    )
        .default("")
    val input_matrix by parser.option(
        ArgType.String,
        shortName = "m",
        description = "Read circuit matrix (csv) as initial matrix if specified"
    ).default("")
    val no_t by parser.option(
        ArgType.Boolean,
        shortName = "q",
        description = "Do not print circuit matrix in commandline after simulation if present"
    ).default(false)
    val sequential by parser.option(
        ArgType.Boolean,
        shortName = "s",
        description = "Use sequential instead of concurrent implementation if present"
    ).default(false)
    val init_state by parser.option(
        ArgType.String,
        shortName = "i",
        description = "Read custom initial joint state from csv if specified"
    ).default("")
    val binary_matrix by parser.option(
        ArgType.Boolean,
        shortName = "b",
        description = "Use binary format instead of csv for read/save circuit matrix if present (EJML version only)"
    ).default(false)
    val disable_cache by parser.option(
        ArgType.Boolean,
        shortName = "d",
        description = "Disable cache to save memory (same gates will need to be recomputed every time)"
    ).default(false)
}