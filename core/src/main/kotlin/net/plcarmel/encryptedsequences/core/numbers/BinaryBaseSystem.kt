package net.plcarmel.encryptedsequences.core.numbers

class BinaryBaseSystem(private val nbBits: Int) : BaseSystem {

  override val base = 1 shl nbBits
  private val mask = base-1

  override fun extractDigits(word: Long): IntArray {
    var w = word
    val digits = mutableListOf<Int>()
    while (w != 0L) {
      digits.add(w.toInt() and mask)
      w = w shr nbBits
    }
    return digits.toIntArray()
  }

  override fun combineDigits(digits: IntArray): Long {
    var m = 0
    var s = 0L
    for (d in digits) {
      s += d.toLong() shl m
      m += nbBits
    }
    return s
  }

  override fun nbValues(wordSize: Int) = 1L shl (wordSize shl 2)


}
