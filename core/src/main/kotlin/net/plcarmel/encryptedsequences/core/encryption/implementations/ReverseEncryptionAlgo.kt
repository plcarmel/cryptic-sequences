package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.EncryptionAlgo

class ReverseEncryptionAlgo(override val wordSize: Int) : EncryptionAlgo {

  override fun encrypt(word: ByteArray, at: Int) {
    for (i in 0 until wordSize / 2) {
      swap(word, at + i, at + wordSize - 1 - i)
    }
  }

  companion object {

    fun swap(word: ByteArray, i: Int, j: Int) {
      val k = word[j]
      word[j] = word[i]
      word[i] = k
    }

  }

}
