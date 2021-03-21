@file:OptIn(ExperimentalCli::class, ExperimentalUnsignedTypes::class)

package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import net.plcarmel.crypticsequences.core.sequences.CrypticBinaryBlockIterator
import net.plcarmel.crypticsequences.core.sequences.CrypticLongIterator
import kotlinx.cli.ExperimentalCli
import net.plcarmel.crypticsequences.core.concurrency.ConcurrencyLayer

fun mainWithAdvancedIo(
  outputLayer: OutputLayer,
  concurrencyLayer: ConcurrencyLayer,
  args: Array<String>
) {
  val parser = ArgParser("cryptic-sequences-cli")
  val options = OptionsWithAdvancedIo(parser)
  parser.parse(args)
  val iterator = options.createIterator(concurrencyLayer)
  val byteCount = options.byteCount
  val output = options.output ?.let(outputLayer::open) ?: outputLayer.openStdOut()
  try {
    if (byteCount == null ) {
      val representationSystem = options.representationSystem
      iterator.asSequence().map(representationSystem::format).forEach(output::println)
    } else {
      CrypticBinaryBlockIterator(
        CrypticLongIterator(iterator, options.baseSystem),
        byteCount,
        options.blockSize
      ).forEach(output::write)
    }
  } finally {
    output.close()
  }
}

