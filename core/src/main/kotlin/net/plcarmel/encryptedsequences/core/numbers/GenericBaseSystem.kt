package net.plcarmel.encryptedsequences.core.numbers

import kotlin.math.pow

class GenericBaseSystem(override val base: Int) : BaseSystem {

  override fun extractDigits(word: Long): IntArray {
    var w = word
    val digits = mutableListOf<Int>()
    while (w != 0L) {
      digits.add((w % base).toInt())
      w /= base
    }
    return digits.toIntArray()
  }

  override fun combineDigits(digits: IntArray): Long {
    var m = 1L
    var s = 0L
    for (d in digits) {
      s += m*d.toLong()
      m *= base
    }
    return s
  }

  override fun nbValues(wordSize: Int) = base.toDouble().pow(wordSize).toLong()

}
