package net.plcarmel.encryptedsequences.core.numbers

interface BaseSystem {

  val base: Int
  fun extractDigits(word: Long): IntArray
  fun combineDigits(digits: IntArray): Long
  fun nbValues(wordSize: Int): Long

}
