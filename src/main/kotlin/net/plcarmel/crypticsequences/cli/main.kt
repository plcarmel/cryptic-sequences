package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import net.plcarmel.encryptedsequences.core.numbers.NumberRepresentationSystem
import net.plcarmel.encryptedsequences.core.sequences.CrypticBinaryBlockIterator
import net.plcarmel.encryptedsequences.core.sequences.CrypticLongIterator
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter

fun printText(
  outputWriter: OutputStreamWriter,
  representationSystem: NumberRepresentationSystem,
  word: ByteArray
) {
  word.let(representationSystem::format).let(outputWriter::write)
  outputWriter.write("\n")
  outputWriter.flush()
}

fun main(args: Array<String>) {
  val parser = ArgParser("cryptic-sequences-cli")
  val options = Options(parser)
  parser.parse(args)
  val iterator = options.createIterator()
  val output = options.output?.let(::FileOutputStream) ?: System.out
  val useOutput =
    if (options.output == null) { body: (OutputStream) -> Unit -> body(output) }
    else output::use
  val byteCount = options.byteCount
  useOutput {
    if (byteCount == null ) {
      val outputWriter = OutputStreamWriter(output)
      val representationSystem = options.representationSystem
      iterator.forEach { w -> printText(outputWriter, representationSystem, w) }
    } else {
      CrypticBinaryBlockIterator(
        CrypticLongIterator(iterator),
        byteCount,
        options.blockSize
      ).forEach(output::write)
    }
  }
}
