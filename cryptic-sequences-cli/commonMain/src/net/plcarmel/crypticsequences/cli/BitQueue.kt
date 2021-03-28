package net.plcarmel.crypticsequences.cli

interface BitQueue {

  val size: Int
  fun put(bits: Long, n: Int)
  fun get(n: Int): Long
  fun clear()

  fun addRandomBitsFromNumber(word: Long, nbValues: Long) {
    if (nbValues >= 2) {
      val n = nbValues.countNbCompleteBits()
      val mask = 1L shl n
      if (word and mask == 0L) { put(word, n) }
      else { addRandomBitsFromNumber(word xor mask, nbValues - mask) }
    }
  }

  companion object {

    private fun Long.countNbCompleteBits(): Int {
      val nbLeading = countLeadingZeroBits()
      val nbTrailing = countTrailingZeroBits()
      return if (64 - nbLeading - nbTrailing == 1) nbTrailing
      else 64 - nbLeading - 1
    }

  }

}
