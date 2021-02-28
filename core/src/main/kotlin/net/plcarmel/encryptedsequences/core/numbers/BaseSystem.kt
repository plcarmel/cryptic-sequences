package net.plcarmel.encryptedsequences.core.numbers

interface BaseSystem {

  val base: Int
  fun extractDigitsAt(target: IntArray, word: Long, start: Int = 0, count: Int = target.size)
  fun combineDigitsFrom(source: IntArray, start: Int = 0, count: Int = source.size): Long
  fun nbValues(wordSize: Int): Long

}
