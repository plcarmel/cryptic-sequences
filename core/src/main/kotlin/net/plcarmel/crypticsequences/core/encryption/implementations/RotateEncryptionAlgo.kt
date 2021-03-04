package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo

class RotateEncryptionAlgo(override val wordSize: Int) : EncryptionAlgo {

  override fun encrypt(word: ByteArray, at: Int) {
    val first = word[at]
    for (it in at + 1 until at + wordSize) {
      word[it-1] = word[it]
    }
    word[at + wordSize - 1] = first
  }

}
