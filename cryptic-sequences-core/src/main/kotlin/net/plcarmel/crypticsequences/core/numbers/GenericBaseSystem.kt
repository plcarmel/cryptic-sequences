package net.plcarmel.crypticsequences.core.numbers

import kotlin.math.pow

class GenericBaseSystem(override val base: Int) : BaseSystem {

  override fun extractDigitsAt(target: ByteArray, word: Long, start: Int, count: Int) {
    var i = start
    var w = word
    while (w != 0L && i - start != count) {
      target[i++] = (w % base).toByte()
      w /= base
    }
    while (i - start != count) {
      target[i++] = 0
    }
  }

  override fun combineDigitsFrom(source: ByteArray, start: Int, count: Int): Long {
    var i = start
    var m = 1L
    var s = 0L
    while(i - start != count) {
      s += m * source[i++].toLong()
      m *= base
    }
    return s
  }

  override fun nbValues(wordSize: Int) = base.toDouble().pow(wordSize).toLong()

  override fun addModulo(x: Byte, n: Int): Byte {
    return ((x + n) % base).toByte()
  }

}
