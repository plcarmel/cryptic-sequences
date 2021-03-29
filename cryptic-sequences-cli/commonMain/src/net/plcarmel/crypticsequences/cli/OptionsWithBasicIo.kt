package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.encryption.implementations.MultiPassOverlapEncryptionAlgo
import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import net.plcarmel.crypticsequences.core.numbers.BinaryBaseSystem
import net.plcarmel.crypticsequences.core.numbers.GenericBaseSystem
import net.plcarmel.crypticsequences.core.numbers.NumberRepresentationSystem
import net.plcarmel.crypticsequences.core.sequences.CrypticIterator
import kotlin.math.ceil
import kotlin.random.Random

open class OptionsWithBasicIo(parser: ArgParser) {

  private val base by
    parser.option(
        ArgType.Int,
      fullName = "base",
      description =
        "The base to use for generated values."
    ).default(10)

  protected val size by
    parser.option(
        ArgType.Int,
      fullName = "size",
      description =
        "The number of digits each generated value should have."
    ).default(3)

  private val key by
    parser.option(
        ArgType.String,
      fullName = "key",
      description =
        "The (up to) 64 bits key to use to encrypt the sequence, in base 16."
    ).default("")

  protected val start by
    parser.option(
        ArgType.Int,
      fullName = "start",
      description =
        "The index from which to start in the sequence."
    ).default(0)

  private val strength by
    parser.option(
        ArgType.Int,
      fullName = "strength",
      description =
        "Controls the number of time the encryption algorithm is applied. Default to 1.5 * size."
    )

  private val count by
    parser.option(
        ArgType.Int,
      fullName = "count",
      description =
        "The number of values to generate."
    )

  protected val baseSystem: BaseSystem
    get() =
      if (base.countOneBits() == 1) BinaryBaseSystem(base.countTrailingZeroBits())
      else GenericBaseSystem(base)

  protected val computedCount: Long
    get() = count?.toLong() ?: baseSystem.nbValues(size)

  protected fun createAlgo(): NumberBasedEncryptionAlgo {
    val random = Random(NumberRepresentationSystem.hexadecimal.parseToLong(key.toUpperCase()))
    return MultiPassOverlapEncryptionAlgo(
      baseSystem = baseSystem,
      wordSize = size,
      prng = random,
      nbPasses = ceil(1.5 * size).toInt(),
    )
  }

  private fun createIterator(algo: NumberBasedEncryptionAlgo): CrypticIterator {
    return CrypticIterator(algo, startAt = start.toLong(), count = computedCount)
  }

  private fun createIterator(): CrypticIterator = createIterator(createAlgo())

  val representationSystem
    get() =
      when {
        base == 2 -> NumberRepresentationSystem.binary
        base == 8 -> NumberRepresentationSystem.octal
        base == 10 -> NumberRepresentationSystem.decimal
        base == 16 -> NumberRepresentationSystem.hexadecimal
        base < 16 -> NumberRepresentationSystem(NumberRepresentationSystem.hexadecimal.symbols.take(base).toCharArray())
        base == 32 -> NumberRepresentationSystem.base32hex
        base < 32 -> NumberRepresentationSystem(NumberRepresentationSystem.base32hex.symbols.take(base).toCharArray())
        base == 64 -> NumberRepresentationSystem.mime64
        base < 64 -> NumberRepresentationSystem(NumberRepresentationSystem.mime64.symbols.take(base).toCharArray())
        else -> throw IllegalArgumentException("Base $base is not valid or is not supported")
      }

  fun printNumbersOnStdOut() {
    val iterator = createIterator()
    val representationSystem = representationSystem
    iterator.forEach { w ->
      w.let(representationSystem::format).let(::println)
    }
  }

}
