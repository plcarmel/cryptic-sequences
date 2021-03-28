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
  ): Iterator<Long> =
    ParallelizedCrypticSequenceIterator(
      algo,
      startAt = start.toLong(),
      count = computedCount,
      concurrencyLayer = concurrencyLayer,
      nbThreads = nbThreads ?: 1
    )

  protected fun createIterator(concurrencyLayer: ConcurrencyLayer): Iterator<Long> =
    createIterator(concurrencyLayer, createAlgo())

  private fun openOutput(outputLayer: OutputLayer) =
    this.output ?.let(outputLayer::open) ?: outputLayer.openStdOut()

  private fun printTextNumbers(platformSpecificLayer: PlatformSpecificLayer) {
    val output = openOutput(platformSpecificLayer.output)
    val representationSystem = representationSystem
    val wordSize = this.size
    createIterator(platformSpecificLayer.concurrency)
      .asSequence()
      .map { representationSystem.format(wordSize, it) }
      .forEach(output::println)
  }

  private fun writeBinaryBlocks(platformSpecificLayer: PlatformSpecificLayer) {
    val output = openOutput(platformSpecificLayer.output)
    CrypticBinaryBlockIterator(
      baseIterator = createIterator(platformSpecificLayer.concurrency),
      nbBytesPerWord = byteCount!!,
      nbWordsPerBlock = blockSize
    ).forEach(output::write)
  }

  open val task: (PlatformSpecificLayer) -> Unit
    get() = if (byteCount == null ) ::printTextNumbers else ::writeBinaryBlocks

  private val output by
    parser.option(
      ArgType.String,
      fullName = "output",
      shortName = "o",
      description =
      "File where to write the data. The standard output is used otherwise."
    )

  private val byteCount by
    parser.option(
        ArgType.Int,
      fullName = "byte-count",
      description = "Output values in binary mode, using \"x\" bytes for each number, truncating them if necessary."
    )

  private val blockSize by
    parser.option(
      ArgType.Int,
      fullName = "block-size",
      description =
        "Block size in values (not in bytes). When option \"byte-count\" is present and output is binary," +
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
