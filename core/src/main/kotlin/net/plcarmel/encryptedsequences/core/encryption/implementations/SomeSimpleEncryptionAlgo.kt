package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.EncryptionAlgo
import net.plcarmel.encryptedsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem

/**
* @param nbPasses
*  controls the number of times the encryption algorithm is executed
*  If the value is too low, the produced encrypted words will not exhibit good randomness properties.
*/
class SomeSimpleEncryptionAlgo(
  override val wordSize: Int,
  override val baseSystem: BaseSystem,
  key: Long,
  nbPasses: Int = 10
) : NumberBasedEncryptionAlgo {

  private val algo: EncryptionAlgo

  init {
    val shuffledTableAlgo = ShuffledTableEncryptionAlgo(baseSystem, key)
    algo =
      if (shuffledTableAlgo.wordSize >= wordSize)
        shuffledTableAlgo
      else
        MultiPassEncryptionAlgo(
          CombineEncryptionAlgo(
            listOf(
              OverlapEncryptionAlgo(wordSize, shuffledTableAlgo),
              ReverseEncryptionAlgo(wordSize),
              RotateEncryptionAlgo(wordSize)
            )
          ),
          nbPasses
        )
  }

  override fun encrypt(word: ByteArray, at: Int) {
    return algo.encrypt(word, at)
  }

}
