package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import kotlin.random.Random

fun simpleEncryptionAlgo40(
  wordSize: Int,
  baseSystem: BaseSystem,
  prng: Random,
  nbPasses: Int = 10
) : NumberBasedEncryptionAlgo =
    if (wordSize <= computeDefaultTableDimensions(baseSystem))
      shuffledTableEncryptionAlgo(wordSize, baseSystem, prng)
    else
      NumberBasedEncryptionAlgoFrom(
        baseSystem,
        MultiPassEncryptionAlgo(
          CombineEncryptionAlgo(
            listOf(
              OverlapEncryptionAlgo(wordSize, shuffledCycleEncryptionAlgo(baseSystem, prng)),
              RotateEncryptionAlgo(wordSize)
            )
          ),
          nbPasses
        )
      )
