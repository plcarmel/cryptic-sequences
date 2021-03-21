package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import kotlin.random.Random

class ShuffledTableEncryptionAlgo(wordSize: Int, baseSystem: BaseSystem, prng: Random)

  : NumberBasedEncryptionAlgo

  by TableEncryptionAlgo(
    wordSize,
    baseSystem,
    table = (0 until baseSystem.nbValues(wordSize).toInt()).shuffled(prng).toIntArray()
  )
