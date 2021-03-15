package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import net.plcarmel.crypticsequences.core.numbers.NumberRepresentationSystem
import net.plcarmel.crypticsequences.core.sequences.CrypticBinaryBlockIterator
import net.plcarmel.crypticsequences.core.sequences.CrypticLongIterator
import java.io.*

@kotlin.ExperimentalStdlibApi
fun printText(
  outputWriter: OutputStreamWriter,
  representationSystem: NumberRepresentationSystem,
  word: ByteArray
) {
  word.let(representationSystem::format).let(outputWriter::write)
  outputWriter.write("\n")
  outputWriter.flush()
}

@kotlin.ExperimentalStdlibApi
fun main(args: Array<String>) {
  val parser = ArgParser("cryptic-sequences-cli")
  val options = Options(parser)
  parser.parse(args)
  val iterator = options.createIterator()
  val output = options.output ?.let { FileOutputStream(it) } ?: FileOutputStream(FileDescriptor.out)
  val byteCount = options.byteCount
  output.use {
    if (byteCount == null ) {
      val outputWriter = OutputStreamWriter(output)
      val representationSystem = options.representationSystem
      iterator.forEach { w -> printText(outputWriter, representationSystem, w) }
    } else {
      CrypticBinaryBlockIterator(
        CrypticLongIterator(iterator),
        byteCount,
        options.blockSize
      ).forEach { output.write(it) }
    }
  }
}
