package net.plcarmel.crypticsequences.cli

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toCValues
import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import testu01.unif01_Gen

class Unif01Iterator(
  private val baseIterator: Iterator<Long>,
  baseSystem: BaseSystem,
  wordSize: Int
): Iterator<Double> {

  private val nbValues = baseSystem.nbValues(wordSize)
  private val bitQueue = BitQueueTwoLongs()

  private fun replenishBitQueue() {
    while (bitQueue.size < 53 && baseIterator.hasNext()) {
      bitQueue.addRandomBitsFromNumber(baseIterator.next(), nbValues)
      // bitQueue.put(baseIterator.next(), 56)
    }
  }

  override fun hasNext(): Boolean {
    if (bitQueue.size < 53) replenishBitQueue()
    return bitQueue.size >= 53
  }

  override fun next(): Double {
    hasNext()
    return minDouble * bitQueue.get(53)
  }

  fun toTestU01Gen() : CPointer<unif01_Gen> {
    globalVarIter = this
    return testu01.unif01_CreateExternGen01(
      "Unif01Iterator".encodeToByteArray().toCValues(),
      staticCFunction<Double> { globalVarIter!!.next() }
    )!!
  }

  @ThreadLocal
  companion object {
    const val minDouble = 1.1102230246251565E-16
    var globalVarIter: Unif01Iterator? = null
  }

}
