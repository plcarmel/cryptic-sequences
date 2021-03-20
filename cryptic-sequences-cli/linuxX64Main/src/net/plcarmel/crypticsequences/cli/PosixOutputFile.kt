@file:OptIn(ExperimentalUnsignedTypes::class)

package net.plcarmel.crypticsequences.cli

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toCValues
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fflush
import platform.posix.fwrite

class PosixOutputFile(private val file : CPointer<FILE>) : OutputFile {

  companion object {
    val newLineSequence = "\n".encodeToByteArray()
  }

  override fun write(values: ByteArray, start: Int, count: Int) {
    fwrite(values.toCValues(), 1, values.size.toULong(), file)
  }

  override val newLineSequence: ByteArray
    get() = Companion.newLineSequence

  override fun flush() {
    fflush(file)
  }

  override fun close() {
    fclose(file)
  }
}
