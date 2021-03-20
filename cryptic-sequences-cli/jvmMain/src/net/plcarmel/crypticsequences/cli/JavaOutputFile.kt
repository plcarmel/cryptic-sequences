package net.plcarmel.crypticsequences.cli

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter

class JavaOutputFile(private val outputStream: OutputStream) : OutputFile {

  companion object {
    val newLineSequence: ByteArray

    init {
      val stream = ByteArrayOutputStream()
      val writer = OutputStreamWriter(stream, Charsets.UTF_8)
      writer.write("\n")
      writer.flush()
      newLineSequence = stream.toByteArray()
    }
  }

  override val newLineSequence: ByteArray
    get() = Companion.newLineSequence

  override fun write(values: ByteArray, start: Int, count: Int) {
    outputStream.write(values, start, count);
  }

  override fun flush() {
    outputStream.flush()
  }

  override fun close() {
    outputStream.close()
  }

}
