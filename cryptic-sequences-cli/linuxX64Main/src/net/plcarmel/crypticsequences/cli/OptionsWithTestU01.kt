package net.plcarmel.crypticsequences.cli

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toCValues
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import net.plcarmel.crypticsequences.core.sequences.CrypticDoubleIterator
import testu01.*

@Suppress("unused")
class OptionsWithTestU01(val args: Array<String>, parser: ArgParser) : OptionsWithAdvancedIo(parser) {

  private enum class U01Test(val exec: (CValuesRef<unif01_Gen>) -> Unit) {
    SMALL_CRUSH(::bbattery_SmallCrush),
    CRUSH(::bbattery_Crush),
    BIG_CRUSH(::bbattery_BigCrush),
    PSEUDO_DIEHARD(::bbattery_pseudoDIEHARD)
  }

  private val test by
    parser.option(
      ArgType.Choice<U01Test>(),
      fullName = "test",
      description = "Name of the statistical tests to run"
    )

  private val time by parser.option(
    ArgType.Int, "time",
    description = "Measure the performance of the cypher. Takes the number of values to generate in parameter."
  )

  @ThreadLocal
  companion object {
    var globalVarIter: CrypticDoubleIterator? = null
  }

  private fun CrypticDoubleIterator.toTestU01Gen() : CPointer<unif01_Gen> {
    globalVarIter = this
    return unif01_CreateExternGen01(
      sequenceOf(*args).joinToString(" ").encodeToByteArray().toCValues(),
      staticCFunction<Double> { globalVarIter!!.next() }
    )!!
  }

  private fun createUnif01Generator(iterator: Iterator<Long>) =
    CrypticDoubleIterator(iterator, baseSystem, size).toTestU01Gen()

  private fun timeCypherTask(layer: PlatformSpecificLayer) {
      val iterator = createIterator(layer.concurrency)
      try { unif01_TimerSumGenWr(createUnif01Generator(iterator), time!!.toLong(),1) }
      finally { iterator.destroy() }
  }

  private fun testTask(layer: PlatformSpecificLayer) {
    val iterator = createIterator(layer.concurrency)
    try { test!!.exec(createUnif01Generator(iterator)) }
    finally { iterator.destroy() }
  }

  override val task: (PlatformSpecificLayer) -> Unit
    get() =
      when {
        time != null -> ::timeCypherTask
        test != null -> ::testTask
        else -> super.task
      }

}
