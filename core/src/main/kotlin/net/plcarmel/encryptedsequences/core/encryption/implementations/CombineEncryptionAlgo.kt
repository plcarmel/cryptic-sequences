package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.EncryptionAlgo

class CombineEncryptionAlgo(private val list: List<EncryptionAlgo>) : EncryptionAlgo {

  override fun encrypt(word: ByteArray, at: Int) {
    for (it in list) {
      it.encrypt(word, at)
    }
  }

  override val wordSize: Int = list[0].wordSize
}
