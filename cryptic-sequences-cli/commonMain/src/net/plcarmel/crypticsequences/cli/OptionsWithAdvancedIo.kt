package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import net.plcarmel.crypticsequences.core.concurrency.ConcurrencyLayer
import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.sequences.ParallelizedCrypticSequenceIterator

class OptionsWithAdvancedIo(parser: ArgParser) : OptionsWithBasicIo(parser) {

  private fun createIterator(
    concurrencyLayer: ConcurrencyLayer,
    algo: NumberBasedEncryptionAlgo
  ): Iterator<ByteArray> {
    return ParallelizedCrypticSequenceIterator(
      algo,
      startAt = start.toLong(),
      count = computedCount,
      concurrencyLayer = concurrencyLayer,
      nbThreads = nbThreads?.toLong() ?: 1
    )
  }

  fun createIterator(concurrencyLayer: ConcurrencyLayer): Iterator<ByteArray> =
    createIterator(concurrencyLayer, createAlgo())

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

  private val nbThreads by
    parser.option(
      ArgType.Int,
      shortName = "j",
      description =
      "The number of thread to use."
    )
}
