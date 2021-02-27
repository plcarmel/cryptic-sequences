package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.VariableSizeWordEncryptionAlgo
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem

/**
* @param strength
*  controls the number of times the encryption algorithm is executed
*  If the value is too low, the produced encrypted words will not exhibit good randomness properties.
*/
class ShuffledTableOverlapEncryptionAlgo(
  baseSystem: BaseSystem,
  key: Long,
  strength: Int = 10
) : VariableSizeWordEncryptionAlgo
  by
  MultiPassEncryptionAlgo(
      OverlapEncryptionAlgo(
        baseSystem,
        ShuffledTableEncryptionAlgo(baseSystem, key)
      ),
      { wordSize -> strength * wordSize }
  )
