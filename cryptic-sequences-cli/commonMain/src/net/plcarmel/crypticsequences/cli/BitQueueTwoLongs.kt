package net.plcarmel.crypticsequences.cli

class BitQueueTwoLongs : BitQueue {

  private val buffer = Array(2) { BitQueueLong() }
  private var putAt = 0
  private var getAt = 0

  override val size: Int
    get() = buffer[0].size + buffer[1].size

  override fun put(bits: Long, n: Int) {
    if (n > 126 - size) {
      throw RuntimeException("Overflow")
    }
    val n1 = n.coerceAtMost(63 - buffer[putAt].size)
    val n2 = n - n1
    buffer[putAt].put(bits shr n2, n1)
    if (buffer[putAt].size == 63) {
      putAt = putAt xor 1
      buffer[putAt].put(bits, n2)
    }
  }

  override fun get(n: Int): Long {
    if (n > size) {
      throw RuntimeException("Underflow")
    }
    val n1 = n.coerceAtMost(buffer[getAt].size)
    var r = buffer[getAt].get(n1)
    val n2 = n - n1
    if (buffer[getAt].size == 0) {
      getAt = getAt xor 1
      r = (r shl n2) or buffer[getAt].get(n2)
    }
    return r
  }

  override fun clear() {
    buffer[0].clear()
    buffer[1].clear()
    getAt = 0
    putAt = 0
  }

}
