package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

class OptionsWithAdvancedIo(parser: ArgParser) : OptionsWithBasicIo(parser) {

  val output by
  parser.option(
    ArgType.String,
    fullName = "output",
    shortName = "o",
    description =
    "File where to write the data. The standard output is used otherwise."
  )

  val byteCount by
    parser.option(
        ArgType.Int,
      fullName = "byte-count",
      description = "Output values in binary mode, using \"x\" bytes for each number, truncating them if necessary."
    )

  val blockSize by
  parser.option(
    ArgType.Int,
    fullName = "block-size",
    description =
      "Block size in values (not in bytes). When option \"byte-count\" is present and output is binary,\n" +
      "this option allows to speed-up output by writing multiple values at a time."
  ).default(1024)

}
