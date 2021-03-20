package net.plcarmel.crypticsequences.cli

interface OutputFile {

  fun write(values: ByteArray, start: Int = 0, count: Int = values.size)
  fun flush() {}
  fun close() {}

  val newLineSequence: ByteArray
    get() = "\n".encodeToByteArray()

  fun print(str: String) {
    str.encodeToByteArray().let(this::write)
  }

  fun println() {
    write(newLineSequence)
    flush()
  }

  fun println(str: String) {
    print(str)
    println()
  }
}
