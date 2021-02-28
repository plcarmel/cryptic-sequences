package net.plcarmel.encryptedsequences.core.numbers

class BinaryBaseSystem(private val nbBits: Int) : BaseSystem {

  override val base = 1 shl nbBits
  private val mask = base-1

  override fun extractDigitsAt(target: ByteArray, word: Long, start: Int, count: Int) {
    var i = start
    var w = word
    while (w != 0L && i - start != count) {
      target[i++] = (w.toInt() and mask).toByte()
      w = w shr nbBits
    }
    while (i - start != count) {
      target[i++] = 0
    }
  }

  override fun combineDigitsFrom(source: ByteArray, start: Int, count: Int): Long {
    var i = start
    var m = 0
    var s = 0L
    while(i - start != count) {
      s += source[i++].toLong() shl m
      m += nbBits
    }
    return s
  }

  override fun nbValues(wordSize: Int) = 1L shl (nbBits * wordSize)

}
