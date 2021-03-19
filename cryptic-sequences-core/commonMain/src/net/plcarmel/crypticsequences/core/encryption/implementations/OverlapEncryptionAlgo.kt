package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo

/**
 * Encryption algorithm that takes an base encryption algo operating on words of fixed size and extend it so it can
 * be applied to words of any size. Multiple passes of this algorithm are likely to be needed to achieve the
 * desired result.
 *
 * Note: Please do NOT use this to encrypt sensitive data as this is not a real vetted encryption algorithm !
 *
 * @param baseEncryption
 *  the base encryption that will be applied repetitively throughout the word
 *
 */
class OverlapEncryptionAlgo(
  override val wordSize: Int,
  private val baseEncryption: EncryptionAlgo
) : EncryptionAlgo {

  private val n = 1 + wordSize - baseEncryption.wordSize

  override fun encrypt(word: ByteArray, at: Int) {
    for (it in at until (at + n)) {
      baseEncryption.encrypt(word, it)
    }
  }
}
