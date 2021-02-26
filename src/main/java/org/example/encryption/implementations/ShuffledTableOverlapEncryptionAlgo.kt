package org.example.encryption.implementations

import org.example.encryption.definitions.VariableSizeWordEncryptionAlgo
import org.example.numbers.BaseSystem

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
