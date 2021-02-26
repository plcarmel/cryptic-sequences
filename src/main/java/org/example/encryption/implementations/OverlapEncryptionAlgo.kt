package org.example.encryption.implementations

import org.example.encryption.definitions.FixedSizeWordEncryptionAlgo
import org.example.encryption.definitions.VariableSizeWordEncryptionAlgo
import org.example.numbers.BaseSystem

/**
 * Encryption algorithm that takes an base encryption algo operating on words of fixed size and extend it so it can
 * be applied to words of any size. Multiple passes of this algorithm are likely to be needed to achieve the
 * desired result.
 *
 * Note: Please do NOT use this to encrypt sensitive data as this is not a real vetted encryption algorithm !
 *
 * @param baseSystem
 *  the base of the digits to encrypt
 *
 * @param baseEncryption
 *  the base encryption that will be applied repetitively throughout the word
 *
 * @param nbPasses
 *  the number of times the encryption algorithm is executed
 *  If the value is too low, the produced encrypted words will not exhibit good pseudo-random properties.
 *
 */
class OverlapEncryptionAlgo(
  private val baseSystem: BaseSystem,
  private val baseEncryption: FixedSizeWordEncryptionAlgo,
) : VariableSizeWordEncryptionAlgo {

  override val base: Int
    get() = baseSystem.base

  override fun encrypt(word: IntArray) {
      word.indices.forEach { baseEncryption.encrypt(word, it) }
  }
}
