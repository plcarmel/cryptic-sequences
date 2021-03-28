package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import net.plcarmel.crypticsequences.core.concurrency.ConcurrencyLayer
import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.sequences.CrypticBinaryBlockIterator
import net.plcarmel.crypticsequences.core.sequences.ParallelizedCrypticSequenceIterator

open class OptionsWithAdvancedIo(parser: ArgParser) : OptionsWithBasicIo(parser) {

  private fun createIterator(
    concurrencyLayer: ConcurrencyLayer,
    algo: NumberBasedEncryptionAlgo
  ): ParallelizedCrypticSequenceIterator =
    ParallelizedCrypticSequenceIterator(
      algo,
      startAt = start.toLong(),
      count = computedCount,
      concurrencyLayer = concurrencyLayer,
      nbThreads = nbThreads ?: 1
    )

  protected fun createIterator(concurrencyLayer: ConcurrencyLayer): ParallelizedCrypticSequenceIterator =
    createIterator(concurrencyLayer, createAlgo())

  private fun openOutput(outputLayer: OutputLayer) =
    this.output ?.let(outputLayer::open) ?: outputLayer.openStdOut()

  private fun printTextNumbers(platformSpecificLayer: PlatformSpecificLayer) {
    val output = openOutput(platformSpecificLayer.output)
    val representationSystem = representationSystem
    val wordSize = this.size
    val iterator = createIterator(platformSpecificLayer.concurrency)
    try {
    iterator
      .asSequence()
      .map { representationSystem.format(wordSize, it) }
      .forEach(output::println)
    } finally {
      iterator.destroy()
    }
  }

  private fun writeBinaryBlocks(platformSpecificLayer: PlatformSpecificLayer) {
    val output = openOutput(platformSpecificLayer.output)
    CrypticBinaryBlockIterator(
      baseIterator = createIterator(platformSpecificLayer.concurrency),
      baseSystem = baseSystem,
      wordSize = size,
      nbIntsPerBlock = blockSize
    ).forEach(output::write)
  }

  open val task: (PlatformSpecificLayer) -> Unit
    get() = if (binary) ::writeBinaryBlocks else ::printTextNumbers

  private val output by
    parser.option(
      ArgType.String,
      fullName = "output",
      shortName = "o",
      description =
      "File where to write the data. The standard output is used otherwise."
    )

  private val binary by
    parser.option(
      ArgType.Boolean,
      fullName = "binary",
      description = "Output random bits in binary mode, for the Dieharder test suite, for example.",
    ).default(false)

  private val blockSize by
    parser.option(
      ArgType.Int,
      fullName = "block-size",
      description =
        "Block size for binary output (number of 32 bits values)." +
        "This option allows to speed-up output by writing multiple values at a time."
    ).default(1024)

  private val nbThreads by
    parser.option(
      ArgType.Int,
      shortName = "j",
      description =
      "The number of thread to use."
    )
}
