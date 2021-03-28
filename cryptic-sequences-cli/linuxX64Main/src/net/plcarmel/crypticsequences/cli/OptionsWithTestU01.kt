package net.plcarmel.crypticsequences.cli

import kotlinx.cinterop.CValuesRef
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import net.plcarmel.crypticsequences.core.concurrency.ConcurrencyLayer
import testu01.*

@Suppress("unused")
class OptionsWithTestU01(parser: ArgParser) : OptionsWithAdvancedIo(parser) {

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
    ArgType.Boolean, "time",
    description = "Measure the performance of the cypher"
  )

  private fun createUnif01Generator(concurrencyLayer: ConcurrencyLayer) =
    Unif01Iterator(createIterator(concurrencyLayer), baseSystem, size).toTestU01Gen()

  private fun timeCypherTask(layer: PlatformSpecificLayer) =
      unif01_TimerSumGenWr(createUnif01Generator(layer.concurrency), 1000000,1)

  private fun testTask(layer: PlatformSpecificLayer) =
    layer
        .let(PlatformSpecificLayer::concurrency)
        .let(this::createUnif01Generator)
        .let { test!!.exec(it) }

  override val task: (PlatformSpecificLayer) -> Unit
    get() =
      when {
        time != null -> ::timeCypherTask
        test != null -> ::testTask
        else -> super.task
      }

}
