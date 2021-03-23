package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser

fun mainWithBasicIo(args: Array<String>) {
  val parser = ArgParser("cryptic-sequences-cli")
  val options = OptionsWithBasicIo(parser)
  parser.parse(args)
  options.printNumbersOnStdOut()
}
