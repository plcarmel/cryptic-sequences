@file:OptIn(ExperimentalCli::class, ExperimentalUnsignedTypes::class)

package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import net.plcarmel.crypticsequences.core.sequences.CrypticBinaryBlockIterator
import net.plcarmel.crypticsequences.core.sequences.CrypticLongIterator
import kotlinx.cli.ExperimentalCli

fun mainWithAdvancedIo(outputSystem: OutputSystem, args: Array<String>) {
  val parser = ArgParser("cryptic-sequences-cli")
  val options = OptionsWithAdvancedIo(parser)
  parser.parse(args)
  val iterator = options.createIterator()
  val byteCount = options.byteCount
  val output = options.output ?.let(outputSystem::open) ?: outputSystem.openStdOut()
  try {
    if (byteCount == null ) {
      val representationSystem = options.representationSystem
      iterator.asSequence().map(representationSystem::format).forEach(output::println)
    } else {
      CrypticBinaryBlockIterator(
        CrypticLongIterator(iterator),
        byteCount,
        options.blockSize
      ).forEach(output::write)
    }
  } finally {
    output.close()
  }
}

