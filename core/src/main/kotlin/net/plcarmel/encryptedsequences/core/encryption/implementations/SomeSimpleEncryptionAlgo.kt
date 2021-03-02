package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.FixedSizeWordEncryptionAlgo
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem

/**
* @param nbPasses
*  controls the number of times the encryption algorithm is executed
*  If the value is too low, the produced encrypted words will not exhibit good randomness properties.
*/
class SomeSimpleEncryptionAlgo(
  override val baseSystem: BaseSystem,
  key: Long,
  override val wordSize: Int,
  nbPasses: Int = 10
) : FixedSizeWordEncryptionAlgo {

  private val algo: FixedSizeWordEncryptionAlgo

  init {
    val shuffledTableAlgo = ShuffledTableEncryptionAlgo(baseSystem, key)
    algo =
      if (shuffledTableAlgo.wordSize >= wordSize)
        shuffledTableAlgo
      else
        MultiPassEncryptionAlgo(
          CombineEncryptionAlgo(
            listOf(
              OverlapEncryptionAlgo(baseSystem, shuffledTableAlgo, wordSize),
              ReverseEncryptionAlgo(wordSize, baseSystem),
              RotateEncryptionAlgo(wordSize, baseSystem)
            )
          ),
          nbPasses
        )
  }

  override fun encrypt(word: ByteArray, at: Int) {
    return algo.encrypt(word, at)
  }

}
