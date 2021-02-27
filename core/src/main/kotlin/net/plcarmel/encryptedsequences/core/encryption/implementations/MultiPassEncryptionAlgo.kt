package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.VariableSizeWordEncryptionAlgo

class MultiPassEncryptionAlgo(
  private val baseAlgo: VariableSizeWordEncryptionAlgo,
  @Suppress("MemberVisibilityCanBePrivate") val nbRepetitions: (Int) -> Int
) : VariableSizeWordEncryptionAlgo {

  override fun encrypt(word: IntArray) {
    repeat(nbRepetitions(word.size)) { baseAlgo.encrypt(word) }
  }

  override val baseSystem
    get() = baseAlgo.baseSystem

}
