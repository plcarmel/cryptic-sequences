package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.FixedSizeWordEncryptionAlgo
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem
import java.util.*
import kotlin.math.ln
import kotlin.math.roundToInt

/**
 * Encryption algo that encrypts words of a given number of digits using a table generated by the Random number
 * generator of Java. Note that the implementation of the random number generator is the same among all versions of
 * the JVM, so no worries there.
 *
 * Note: Please do NOT use this to encrypt sensitive data as this is not a real vetted encryption algorithm and the
 * key size is too small anyway. It is just meant to obfuscate information to make it look more random to users.
 *
 * @param baseSystem
 *  the size of individual digits
 *  It has an impact on the size of the generate table.
 *
 * @param wordSize
 *  the number of digits that this algo encrypts at a time
 *  This will impact the size of the generate table and should be kept small. However, it should vary with
 *  the base, because using a small number of digits with a small base will result in an extremely small effective
 *  key size.
 */
class ShuffledTableEncryptionAlgo(
  override val baseSystem: BaseSystem,
  @Suppress("MemberVisibilityCanBePrivate") val key: Long, // 48 bits
  override val wordSize: Int = (ln(256.0)/ln(baseSystem.base.toDouble())).roundToInt().coerceAtLeast(2)
) : FixedSizeWordEncryptionAlgo {

  private val rnd = Random(key)
  private val n = baseSystem.nbValues(wordSize).toInt()

  // Fisher-Yates shuffle
  private val mainTable: IntArray = (0 until n).shuffled(rnd).toIntArray()

  private fun deriveTable(smallerWordSize: Int): IntArray =
    mainTable
      .take(baseSystem.nbValues(smallerWordSize).toInt())
      .let(Companion::getOrder)
      .toIntArray()

  private val allTables: Map<Int, IntArray> =
    ((1 until wordSize).map { it to deriveTable(it) } + listOf(wordSize to mainTable)).toMap()

  override fun encrypt(word: ByteArray, at: Int) {
    val nbDigits = word.size.coerceAtMost(wordSize)
    val table = allTables[nbDigits]!!
    baseSystem
      .combineDigitsFrom(word, at, nbDigits)
      .toInt()
      .let(table::get)
      .toLong()
      .let { baseSystem.extractDigitsAt(word, it, at, nbDigits) }
  }

  companion object {

    private fun getOrder(numbers: List<Int>): List<Int> =
      numbers
        .indices
        .map { Pair(it, numbers[it]) }
        .sortedBy { (_, n) -> n }
        .map { (i, _) -> i }

  }
}
