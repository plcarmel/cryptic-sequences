package net.plcarmel.encryptedsequences.core.numbers

import kotlin.math.pow

class BaseSystem(val base: Int) {

  fun extractDigits(word: Long): IntArray {
    var w = word
    val digits = mutableListOf<Int>()
    while (w != 0L) {
      digits.add((w % base).toInt())
      w /= base
    }
    return digits.toIntArray()
  }

  fun combineDigits(digits: IntArray): Long {
    var m = 1L
    var s = 0L
    for (d in digits) {
      s += m*d.toLong()
      m *= base
    }
    return s
  }

  fun nbValues(wordSize: Int) = base.toDouble().pow(wordSize).toLong()

  companion object {

    fun zeroPad(wordSize: Int, word: IntArray): IntArray =
      word + (1 .. wordSize - word.size).map { 0 }

  }
}
