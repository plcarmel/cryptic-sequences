@file:OptIn(ExperimentalCli::class)

package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import net.plcarmel.crypticsequences.core.numbers.NumberRepresentationSystem

private fun printTextUsingKotlin(
  representationSystem: NumberRepresentationSystem,
  word: ByteArray
) {
  word.let(representationSystem::format).let(::println)
}

fun mainWithBasicIo(args: Array<String>) {
  val parser = ArgParser("cryptic-sequences-cli")
  val options = OptionsWithBasicIo(parser)
  parser.parse(args)
  val iterator = options.createIterator()
  val representationSystem = options.representationSystem
  iterator.forEach { w -> printTextUsingKotlin(representationSystem, w) }
}
