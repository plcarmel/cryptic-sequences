package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo
import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import kotlin.random.Random

/**
* @param nbPasses
*  controls the number of times the encryption algorithm is executed
*  If the value is too low, the produced encrypted words will not exhibit good randomness properties.
*/
class SimpleEncryptionAlgo40(
  override val wordSize: Int,
  override val baseSystem: BaseSystem,
  prng: Random,
  nbPasses: Int = 10
) : NumberBasedEncryptionAlgo {

  private val algo: EncryptionAlgo

  init {
    val shuffledTableAlgo = ShuffledTableEncryptionAlgo(baseSystem, prng)
    algo =
        MultiPassEncryptionAlgo(
          CombineEncryptionAlgo(
            listOf(
              OverlapEncryptionAlgo(wordSize, shuffledTableAlgo),
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
