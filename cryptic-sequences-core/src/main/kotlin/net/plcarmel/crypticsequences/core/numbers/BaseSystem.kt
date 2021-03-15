package net.plcarmel.crypticsequences.core.numbers

interface BaseSystem {

  val base: Int
  fun extractDigitsAt(target: ByteArray, word: Long, start: Int = 0, count: Int = target.size)
  fun combineDigitsFrom(source: ByteArray, start: Int = 0, count: Int = source.size): Long
  fun nbValues(wordSize: Int): Long

}
