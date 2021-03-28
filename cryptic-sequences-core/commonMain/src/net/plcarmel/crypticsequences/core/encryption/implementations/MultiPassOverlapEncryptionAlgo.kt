package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo
import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import kotlin.random.Random

class MultiPassOverlapEncryptionAlgo(
  wordSize: Int,
  baseSystem: BaseSystem,
  prng: Random,
  overlapAlgoFactory: (wordSize: Int, TableEncryptionAlgo) -> EncryptionAlgo = ::OverlapEncryptionAlgoOptimized,
  nbPasses: Int = 10
)
  : NumberBasedEncryptionAlgo

  by if (wordSize <= computeDefaultTableDimensions(baseSystem))
    ShuffledTableEncryptionAlgo(wordSize, baseSystem, prng)
  else
    NumberBasedEncryptionAlgoFrom(
      baseSystem,
      MultiPassEncryptionAlgo(
            overlapAlgoFactory(wordSize, shuffledCycleEncryptionAlgo(baseSystem, prng)),
        nbPasses
      )
    )
