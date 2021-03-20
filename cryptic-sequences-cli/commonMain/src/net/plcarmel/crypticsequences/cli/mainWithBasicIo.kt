@file:OptIn(ExperimentalCli::class)

package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli

fun mainWithBasicIo(args: Array<String>) {
  val parser = ArgParser("cryptic-sequences-cli")
  val options = OptionsWithBasicIo(parser)
  parser.parse(args)
  val iterator = options.createIterator()
  val representationSystem = options.representationSystem
  iterator.forEach { w ->
    w.let(representationSystem::format).let(::println)
  }
}
