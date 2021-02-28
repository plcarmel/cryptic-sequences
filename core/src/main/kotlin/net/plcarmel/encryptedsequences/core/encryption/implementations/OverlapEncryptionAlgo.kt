package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.FixedSizeWordEncryptionAlgo
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem

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
 */
class OverlapEncryptionAlgo(
  override val baseSystem: BaseSystem,
  private val baseEncryption: FixedSizeWordEncryptionAlgo,
  override val wordSize: Int
) : FixedSizeWordEncryptionAlgo {

  private val n = (1 + wordSize - baseEncryption.wordSize).coerceAtLeast(1)

  override fun encrypt(word: ByteArray, at: Int) {
    (at until (at + n)).forEach { baseEncryption.encrypt(word, it) }
  }
}
