package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo
import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.numbers.BaseSystem

class NumberBasedEncryptionAlgoFrom(
  override val baseSystem: BaseSystem,
  private val algo: EncryptionAlgo
) : NumberBasedEncryptionAlgo {

  override fun encrypt(word: ByteArray, at: Int) = algo.encrypt(word, at)
  override val wordSize: Int = algo.wordSize
}
