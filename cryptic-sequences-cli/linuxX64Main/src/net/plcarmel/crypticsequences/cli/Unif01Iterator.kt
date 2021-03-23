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

  private val n = (baseSystem.nbValues(wordSize)-1).toDouble()

  override fun hasNext() = baseIterator.hasNext()

  override fun next(): Double = baseIterator.next().toDouble() / n

  fun toTestU01Gen() : CPointer<unif01_Gen> {
    globalVarIter = this
    return testu01.unif01_CreateExternGen01(
      "Unif01Iterator".encodeToByteArray().toCValues(),
      staticCFunction<Double> { globalVarIter!!.next() }
    )!!
  }

  @ThreadLocal
  companion object {
    var globalVarIter: Unif01Iterator? = null
  }

}
