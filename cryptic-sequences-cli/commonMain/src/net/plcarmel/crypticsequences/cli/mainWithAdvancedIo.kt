package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser

fun mainWithAdvancedIo(
  platformSpecificLayer: PlatformSpecificLayer,
  args: Array<String>,
  makeOptions: (ArgParser) -> OptionsWithAdvancedIo
) {
  val parser = ArgParser("cryptic-sequences-cli")
  val options = makeOptions(parser)
  parser.parse(args)
  options.task(platformSpecificLayer)
}

