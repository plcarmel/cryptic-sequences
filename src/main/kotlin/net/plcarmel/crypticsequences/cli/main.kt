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
  val bytes = word.let(baseSystem::combineDigits).let(base256::extractDigits)
  val bytesPadded = (if (nbBytes > bytes.size) bytes + IntArray(nbBytes - bytes.size) else bytes)
  output.write(bytesPadded.map(Int::toByte).toByteArray(), 0, nbBytes)
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
    sequence.forEachRemaining(
      if (options.byteCount == null ) {
        val outputWriter = OutputStreamWriter(output)
        val representationSystem = options.representationSystem
        { w: IntArray -> printText(outputWriter, representationSystem, w) }
      } else {
        { w:IntArray -> printBinary(output, sequence.encryptionAlgo.baseSystem, options.byteCount!!, w) }
      }
    )
  }
}
