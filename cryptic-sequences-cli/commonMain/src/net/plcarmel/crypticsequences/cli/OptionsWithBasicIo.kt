package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import net.plcarmel.crypticsequences.core.encryption.implementations.simpleEncryptionAlgo40
import net.plcarmel.crypticsequences.core.numbers.BinaryBaseSystem
import net.plcarmel.crypticsequences.core.numbers.GenericBaseSystem
import net.plcarmel.crypticsequences.core.numbers.NumberRepresentationSystem
import net.plcarmel.crypticsequences.core.sequences.CrypticIterator
import kotlin.random.Random

open class OptionsWithBasicIo(parser: ArgParser) {

  fun createIterator(): CrypticIterator {
    val baseSystem =
      if (base.countOneBits() == 1) BinaryBaseSystem(base.countTrailingZeroBits())
      else GenericBaseSystem(base)
    val random = Random(NumberRepresentationSystem.mime64.parseToLong(key))
    return CrypticIterator(
      simpleEncryptionAlgo40(
        baseSystem = baseSystem,
        wordSize = size,
        prng = random,
        nbPasses = strength,
      ),
      currentIndex = start.toLong(),
      count = count?.toLong() ?: baseSystem.nbValues(size)
    )
  }

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

  private val base by
    parser.option(
        ArgType.Int,
      fullName = "base",
      description =
        "The base to use for generated values."
    ).default(10)

  private val size by
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
        "The 48 bits key to use to encrypt the sequence, in base 64 (up to 6 digits)."
    ).default("")

  private val start by
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
        "Controls the number of time the encryption algorithm is applied"
    ).default(10)

  private val count by
    parser.option(
        ArgType.Int,
      fullName = "count",
      description =
        "The number of generated values."
    )
}
