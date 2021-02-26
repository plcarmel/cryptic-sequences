package org.example

import java.lang.IllegalArgumentException

class BaseSystem(val base: Int) {

  private val digits =
      when {
        base <= 10 -> base10digits
        base <= 16 -> base16digits
        base <= 64 -> base64digits
        else -> throw IllegalArgumentException("Bases greater than 64 are not supported")
      }

  private val charToDigitMap =
    when {
      base <= 16 -> base16charToDigitMap
      base <= 64 -> base64charToDigitMap
      else -> throw IllegalArgumentException("Bases greater than 64 are not supported")
    }

  fun toDigits(value: String) =
    value.map { toDigit(it) }.toIntArray()

  fun fromDigits(value: IntArray) =
    String(value.map { fromDigit(it) }.toCharArray())

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

  private fun fromDigit(digit: Int): Char = digits[digit]
  private fun toDigit(char: Char): Int = charToDigitMap[char]!!

  companion object {

    private val base10digits = ('0'..'9').toList().toCharArray()
    private val base16digits = (('0'..'9') + ('A'..'F')).toCharArray()
    private val base64digits = (('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('+','/')).toCharArray()

    private val base16charToDigitMap = base16digits.indices.map { base16digits[it] to it }.toMap()
    private val base64charToDigitMap = base64digits.indices.map { base64digits[it] to it }.toMap()

  }
}
