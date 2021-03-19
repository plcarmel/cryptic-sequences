package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import kotlin.random.Random

fun shuffledTableEncryptionAlgo(wordSize: Int, baseSystem: BaseSystem, prng: Random) =
  TableEncryptionAlgo(
    wordSize,
    baseSystem,
    table = (0 until baseSystem.nbValues(wordSize).toInt()).shuffled(prng).toIntArray()
  )
