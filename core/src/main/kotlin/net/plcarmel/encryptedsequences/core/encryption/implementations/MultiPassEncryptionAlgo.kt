package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.EncryptionAlgo

class MultiPassEncryptionAlgo(
  private val baseAlgo: EncryptionAlgo,
  @Suppress("MemberVisibilityCanBePrivate") val nbPasses: Int
) : EncryptionAlgo {

  override val wordSize: Int
    get() = baseAlgo.wordSize

  override fun encrypt(word: ByteArray, at: Int) {
    repeat(nbPasses) { baseAlgo.encrypt(word, at) }
  }

}
