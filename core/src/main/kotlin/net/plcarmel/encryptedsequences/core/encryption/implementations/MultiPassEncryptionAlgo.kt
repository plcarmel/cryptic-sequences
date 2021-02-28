package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.FixedSizeWordEncryptionAlgo

class MultiPassEncryptionAlgo(
  private val baseAlgo: FixedSizeWordEncryptionAlgo,
  @Suppress("MemberVisibilityCanBePrivate") val nbPasses: Int
) : FixedSizeWordEncryptionAlgo {

  override val wordSize: Int
    get() = baseAlgo.wordSize

  override fun encrypt(word: ByteArray, at: Int) {
    repeat(nbPasses) { baseAlgo.encrypt(word, at) }
  }

  override val baseSystem
    get() = baseAlgo.baseSystem

}
