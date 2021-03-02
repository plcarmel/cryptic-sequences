package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.FixedSizeWordEncryptionAlgo
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem

class RotateEncryptionAlgo(
  override val wordSize: Int,
  override val baseSystem: BaseSystem
) : FixedSizeWordEncryptionAlgo {

  override fun encrypt(word: ByteArray, at: Int) {
    val first = word[at]
    for (it in at + 1 until at + wordSize) {
      word[it-1] = word[it]
    }
    word[at + wordSize - 1] = first
  }

}
