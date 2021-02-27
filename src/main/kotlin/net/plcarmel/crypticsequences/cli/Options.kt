package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem
import net.plcarmel.encryptedsequences.core.numbers.NumberRepresentationSystem
import net.plcarmel.encryptedsequences.core.sequences.CrypticSequence
import java.lang.IllegalArgumentException

class Options(parser: ArgParser) {

  fun createSequence(): CrypticSequence {
    val baseSystem = BaseSystem(base)
    return CrypticSequence(
      baseSystem,
      wordSize = size,
      key = NumberRepresentationSystem.mime64.parseToLong(key),
      strength = strength,
      startIndex = start.toLong(),
      endIndex = count?.let { start.toLong() + it.toLong() } ?: baseSystem.nbValues(size)
    )
  }

  val representationSystem
    get() =
      when (base) {
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
      shortName = "b",
      description = "The base to use for generated values."
    ).default(10)

  private val size by
    parser.option(
        ArgType.Int,
      fullName = "size",
      shortName = "s",
      description = "The number of digits each generated value should have."
    ).default(3)

  private val key by
    parser.option(
        ArgType.String,
      fullName = "key",
      shortName = "k",
      description = "The 48 bits key to use to encrypt the sequence, in base 64 (up to 6 digits)."
    ).default("0")

  private val start by
    parser.option(
        ArgType.Int,
      fullName = "start",
      shortName = "a",
      description = "The index from which to start in the sequence."
    ).default(0)

  private val strength by
    parser.option(
        ArgType.Int,
      fullName = "strength",
      shortName = "r",
      description = "Controls the number of time the encryption algorithm is applied."
    ).default(10)

  private val count by
    parser.option(
        ArgType.Int,
      fullName = "count",
      shortName = "c",
      description = "The number of generated values."
    )

  val byteCount by
    parser.option(
        ArgType.Int,
      fullName = "byte-count",
      shortName = "x",
      description = "Output values in binary mode, using \"x\" bytes for each number, truncating them if necessary."
    )

  val output by
    parser.option(
        ArgType.String,
      fullName = "output",
      shortName = "o",
      description = "File where to write the data. The standard input is used otherwise."
    )
}
