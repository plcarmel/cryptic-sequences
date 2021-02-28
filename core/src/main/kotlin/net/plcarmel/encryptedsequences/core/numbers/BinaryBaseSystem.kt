package net.plcarmel.encryptedsequences.core.numbers

class BinaryBaseSystem(private val nbBits: Int) : BaseSystem {

  override val base = 1 shl nbBits
  private val mask = base-1

  override fun extractDigitsAt(target: IntArray, word: Long, start: Int, count: Int) {
    val n = target.size
    var i = start
    var w = word
    while (w != 0L) {
      target[i++ % n] = w.toInt() and mask
      w = w shr nbBits
    }
    while (i - start != count) {
      target[i++ % n] = 0
    }
  }

  override fun combineDigitsFrom(source: IntArray, start: Int, count: Int): Long {
    val n = source.size
    var i = start
    var m = 0
    var s = 0L
    while(i - start != count) {
      s += source[i++ % n] shl m
      m += nbBits
    }
    return s
  }

  override fun nbValues(wordSize: Int) = 1L shl wordSize


}
