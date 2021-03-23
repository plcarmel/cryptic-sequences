package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import net.plcarmel.crypticsequences.core.concurrency.ConcurrencyLayer

class OptionsWithTestU01(parser: ArgParser) : OptionsWithAdvancedIo(parser) {

  private enum class U01Test {
    SMALLCRUSH
  }

  private val test by
    parser.option(
      ArgType.Choice<U01Test>(),
      fullName = "test",
      shortName = "t",
      description = "Name of the statistical tests to run"
    )

  private fun createUnif01Generator(concurrencyLayer: ConcurrencyLayer) =
    Unif01Iterator(createIterator(concurrencyLayer), baseSystem, size).toTestU01Gen()

  private fun smallCrunch(platformSpecificLayer: PlatformSpecificLayer) {
    testu01.bbattery_SmallCrush(createUnif01Generator(platformSpecificLayer.concurrency))
  }

  override val task
    get() =
      when(test) {
        null -> super.task
        U01Test.SMALLCRUSH -> ::smallCrunch
      }

}
