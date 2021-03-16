package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import net.plcarmel.crypticsequences.core.numbers.BinaryBaseSystem
import net.plcarmel.crypticsequences.core.numbers.GenericBaseSystem
import net.plcarmel.crypticsequences.core.numbers.NumberRepresentationSystem
import net.plcarmel.crypticsequences.core.sequences.CrypticIterator
import java.lang.IllegalArgumentException

@kotlin.ExperimentalStdlibApi
class Options(parser: ArgParser) {

  fun createIterator(): CrypticIterator {
    val baseSystem =
      if (base.countOneBits() == 1) BinaryBaseSystem(base.countTrailingZeroBits())
      else GenericBaseSystem(base)
    return CrypticIterator(
      baseSystem,
      wordSize = size,
      key = NumberRepresentationSystem.mime64.parseToLong(key),
      nbPasses = strength,
      startIndex = start.toLong(),
      count = count?.toLong() ?: baseSystem.nbValues(size)
    )
  }

  val representationSystem
    get() =
      when (base) {
        2 -> NumberRepresentationSystem.binary
        8 -> NumberRepresentationSystem.octal
        10 -> NumberRepresentationSystem.decimal
        16 -> NumberRepresentationSystem.hexadecimal
        64 -> NumberRepresentationSystem.mime64
        else -> throw IllegalArgumentException("Base $base is not valid or is not supported")
      }

  private val base by
    parser.option(
        ArgType.Int,
      fullName = "base",
      description = "The base to use for generated values."
    ).default(10)

  private val size by
    parser.option(
        ArgType.Int,
      fullName = "size",
      description = "The number of digits each generated value should have."
    ).default(3)

  private val key by
    parser.option(
        ArgType.String,
      fullName = "key",
      description = "The 48 bits key to use to encrypt the sequence, in base 64 (up to 6 digits)."
    ).default("")

  private val start by
    parser.option(
        ArgType.Int,
      fullName = "start",
      description = "The index from which to start in the sequence."
    ).default(0)

  private val strength by
    parser.option(
        ArgType.Int,
      fullName = "strength",
      description = "Controls the number of time the encryption algorithm is applied."
    ).default(10)

  private val count by
    parser.option(
        ArgType.Int,
      fullName = "count",
      description = "The number of generated values."
    )

  val byteCount by
    parser.option(
        ArgType.Int,
      fullName = "byte-count",
      description = "Output values in binary mode, using \"x\" bytes for each number, truncating them if necessary."
    )

  val blockSize by
  parser.option(
    ArgType.Int,
    fullName = "block-size",
    description =
      "Block size in values (not in bytes). When option \"byte-count\" is present and output is binary,\n" +
      "this option allows to speed-up output by writing multiple values at a time."
  ).default(1024)

  val output by
    parser.option(
        ArgType.String,
      fullName = "output",
      shortName = "o",
      description = "File where to write the data. The standard input is used otherwise."
    )
}
