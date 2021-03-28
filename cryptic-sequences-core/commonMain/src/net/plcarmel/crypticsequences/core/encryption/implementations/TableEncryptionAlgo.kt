package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.numbers.BaseSystem

/**
 * Encryption algo that encrypts words of a given number of digits using the table provided
 *
 * @param baseSystem
 *  the size of individual digits
 *  It has an impact on the size of the generate table.
 *
 * @param wordSize
 *  the number of digits that this algo encrypts at a time
 *  This will impact the size of the generated table and should be kept small. However, it should vary with
 *  the base, because using a small number of digits with a small base will result in an extremely small effective
 *  key size.
 */
class TableEncryptionAlgo(
  override val wordSize: Int,
  override val baseSystem: BaseSystem,
  val table: IntArray
) : NumberBasedEncryptionAlgo {

  val base = baseSystem.base

  override fun encrypt(word: ByteArray, at: Int) {
    baseSystem
      .combineDigitsFrom(word, at, wordSize)
      .toInt()
      .let(table::get)
      .toLong()
      .let { baseSystem.extractDigitsAt(word, it, at, wordSize) }
  }

}
