package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem
import net.plcarmel.encryptedsequences.core.numbers.BinaryBaseSystem
import net.plcarmel.encryptedsequences.core.numbers.NumberRepresentationSystem
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter

private val base256 = BinaryBaseSystem(8)

fun printBinary(
  output: OutputStream,
  baseSystem: BaseSystem,
  nbBytes: Int,
  word: IntArray
) {
  val bytes = IntArray(nbBytes)
  word.let(baseSystem::combineDigitsFrom).let { base256.extractDigitsAt(bytes, it, count = nbBytes) }
  output.write(bytes.map(Int::toByte).toByteArray(), 0, nbBytes)
}

fun printText(
  outputWriter: OutputStreamWriter,
  representationSystem: NumberRepresentationSystem,
  word: IntArray
) {
  word.let(representationSystem::format).let(outputWriter::write)
  outputWriter.write("\n")
  outputWriter.flush()
}

fun main(args: Array<String>) {
  val parser = ArgParser("cryptic-sequences-cli")
  val options = Options(parser)
  parser.parse(args)
  val sequence = options.createSequence()
  val output = options.output?.let(::FileOutputStream) ?: System.out
  val useOutput =
    if (options.output == null) { body: (OutputStream) -> Unit -> body(output) }
    else output::use
  useOutput {
    val byteCount = options.byteCount
    sequence.forEachRemaining(
      if (byteCount == null ) {
        val outputWriter = OutputStreamWriter(output)
        val representationSystem = options.representationSystem
        { w: IntArray -> printText(outputWriter, representationSystem, w) }
      } else {
          { w:IntArray -> printBinary(output, sequence.encryptionAlgo.baseSystem, byteCount, w) }
      }
    )
  }
}
