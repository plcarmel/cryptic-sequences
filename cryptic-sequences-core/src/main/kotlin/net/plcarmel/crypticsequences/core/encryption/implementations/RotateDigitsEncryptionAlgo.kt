package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.numbers.BaseSystem

class RotateDigitsEncryptionAlgo(
  override val wordSize: Int,
  override val baseSystem: BaseSystem
) : NumberBasedEncryptionAlgo {

  override fun encrypt(word: ByteArray, at: Int) {
    for (it in at until at + wordSize) {
      word[it] = baseSystem.addModulo(word[it], 1)
    }
  }

}
