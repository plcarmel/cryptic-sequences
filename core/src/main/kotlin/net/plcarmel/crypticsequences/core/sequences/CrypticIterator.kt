package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.encryption.implementations.SimpleEncryptionAlgo1
import net.plcarmel.crypticsequences.core.numbers.BaseSystem

class CrypticIterator(
  val encryptionAlgo: NumberBasedEncryptionAlgo,
  private var startIndex: Long = 0,
  private var count: Long = encryptionAlgo.baseSystem.nbValues(encryptionAlgo.wordSize)
): Iterator<ByteArray> {

  constructor(
    baseSystem: BaseSystem,
    wordSize: Int,
    key: Long,
    nbPasses: Int,
    startIndex: Long = 0,
    count: Long = baseSystem.nbValues(wordSize)
  ) : this(
    SimpleEncryptionAlgo1(wordSize = wordSize, baseSystem = baseSystem, key = key, nbPasses = nbPasses),
    startIndex,
    count
  )

  init {
    count = count.coerceAtMost(encryptionAlgo.baseSystem.nbValues(encryptionAlgo.wordSize))
  }

  private val baseSystem = encryptionAlgo.baseSystem
  private val wordSize = encryptionAlgo.wordSize

  override fun hasNext(): Boolean {
    return count != 0L
  }

  override fun next(): ByteArray {
    val result = ByteArray(size = wordSize)
    baseSystem.extractDigitsAt(result, startIndex, 0, wordSize)
    encryptionAlgo.encrypt(result)
    startIndex++
    count--
    return result
  }
}
