package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.bits.BitQueueTwoLongs
import net.plcarmel.crypticsequences.core.numbers.BaseSystem

class CrypticBinaryIntIterator(
  private val baseIterator: Iterator<Long>,
  baseSystem: BaseSystem,
  wordSize: Int
): Iterator<Int> {

  private val nbValues = baseSystem.nbValues(wordSize)
  private val bitQueue = BitQueueTwoLongs()

  private fun replenishBitQueue() {
    while (bitQueue.size < 32 && baseIterator.hasNext()) {
      bitQueue.addRandomBitsFromNumber(baseIterator.next(), nbValues)
    }
  }

  override fun hasNext(): Boolean {
    if (bitQueue.size < 32) replenishBitQueue()
    return bitQueue.size >= 32
  }

  override fun next(): Int {
    hasNext()
    return bitQueue.get(32).toInt()
  }

}
