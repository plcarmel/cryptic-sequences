package net.plcarmel.encryptedsequences.core.numbers

import kotlin.math.pow

class GenericBaseSystem(override val base: Int) : BaseSystem {

  override fun extractDigitsAt(target: IntArray, word: Long, start: Int, count: Int) {
    val n = target.size
    var i = start
    var w = word
    while (w != 0L) {
      target[i++ % n] = (w % base).toInt()
      w /= base
    }
    while (i - start != count) {
      target[i++ % n] = 0
    }
  }

  override fun combineDigitsFrom(source: IntArray, start: Int, count: Int): Long {
    val n = source.size
    var i = start
    var m = 1L
    var s = 0L
    while(i - start != count) {
      s += m*source[i++ % n]
      m *= base
    }
    return s
  }

  override fun nbValues(wordSize: Int) = base.toDouble().pow(wordSize).toLong()

}
