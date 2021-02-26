package org.example.numbers

class BaseSystem(val base: Int) {

  fun extractDigits(x: Int): IntArray {
    var y = x
    val digits = mutableListOf<Int>()
    while (y != 0) {
      digits.add(y % base)
      y /= base
    }
    return digits.toIntArray()
  }

  fun combineDigits(digits: IntArray): Int {
    var m = 1
    var s = 0
    for (d in digits) {
      s += m*d
      m *= base
    }
    return s
  }

  companion object {

    fun zeroPad(n: Int, tag: IntArray): IntArray =
      tag + (1 .. n - tag.size).map { 0 }

  }
}
