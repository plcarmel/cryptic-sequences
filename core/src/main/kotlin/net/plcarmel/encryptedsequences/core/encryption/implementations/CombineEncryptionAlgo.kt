package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.FixedSizeWordEncryptionAlgo
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem

class CombineEncryptionAlgo(val list: List<FixedSizeWordEncryptionAlgo>) : FixedSizeWordEncryptionAlgo {

  override fun encrypt(word: ByteArray, at: Int) {
    for (it in list) {
      it.encrypt(word, at)
    }
  }

  override val wordSize: Int = list[0].wordSize
  override val baseSystem: BaseSystem = list[0].baseSystem
}
