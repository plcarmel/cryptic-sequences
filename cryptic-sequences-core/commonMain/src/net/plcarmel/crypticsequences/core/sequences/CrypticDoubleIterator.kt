package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.bits.BitQueueTwoLongs
import net.plcarmel.crypticsequences.core.numbers.BaseSystem

class CrypticDoubleIterator(
  private val baseIterator: Iterator<Long>,
  baseSystem: BaseSystem,
  wordSize: Int
): Iterator<Double> {

  private val nbValues = baseSystem.nbValues(wordSize)
  private val bitQueue = BitQueueTwoLongs()

  private fun replenishBitQueue() {
    while (bitQueue.size < 53 && baseIterator.hasNext()) {
      bitQueue.addRandomBitsFromNumber(baseIterator.next(), nbValues)
    }
  }

  override fun hasNext(): Boolean {
    if (bitQueue.size < 53) replenishBitQueue()
    return bitQueue.size >= 53
  }

  override fun next(): Double {
    hasNext()
    return minValue * bitQueue.get(53)
  }

  companion object {
    const val minValue = 1.1102230246251565E-16
  }

}
