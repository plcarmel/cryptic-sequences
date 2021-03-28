package net.plcarmel.crypticsequences.core.bits

class BitQueueLong : BitQueue {

  private var _size = 0

  override var size: Int
    get() = _size
    private set(value) { _size = value }

  private var buffer = 0L

  override fun put(bits: Long, n: Int) {
    buffer = (buffer shl n) or (bits and ((1L shl n) - 1))
    size += n
  }

  override fun get(n: Int): Long {
    val r = (buffer shr (size - n)) and ((1L shl n) - 1)
    size -= n
    return r
  }

  override fun clear() {
    buffer = 0L
    size = 0
  }

}
