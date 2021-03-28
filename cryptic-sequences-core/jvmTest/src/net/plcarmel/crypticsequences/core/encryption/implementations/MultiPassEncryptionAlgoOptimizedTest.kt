package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.numbers.GenericBaseSystem
import kotlin.random.Random

class MultiPassEncryptionAlgoOptimizedTest :
  MultiPassOverlapEncryptionAlgoTest({ base: Int, size: Int, strength: Int, key: Long ->
    MultiPassOverlapEncryptionAlgo(
      size,
      GenericBaseSystem(base),
      Random(key),
      ::OverlapEncryptionAlgoOptimized,
      nbPasses = strength
    )
  })
