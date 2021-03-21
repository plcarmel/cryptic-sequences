package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo

class CrypticIterator(
  val encryptionAlgo: NumberBasedEncryptionAlgo,
  var currentIndex: Long,
  var jump: Long,
  var bound: Long
): Iterator<ByteArray> {

  constructor(
    encryptionAlgo: NumberBasedEncryptionAlgo,
    startAt: Long = 0,
    count: Long = encryptionAlgo.baseSystem.nbValues(encryptionAlgo.wordSize)
  ) : this(encryptionAlgo, startAt, 1, count)

  init {
    bound = bound.coerceAtMost(encryptionAlgo.baseSystem.nbValues(encryptionAlgo.wordSize))
  }

  private val baseSystem = encryptionAlgo.baseSystem
  private val wordSize = encryptionAlgo.wordSize

  override fun hasNext(): Boolean {
    return bound > 0L
  }

  override fun next(): ByteArray {
    if (bound <= 0L) throw NoSuchElementException()
    val result = ByteArray(size = wordSize)
    baseSystem.extractDigitsAt(result, currentIndex, 0, wordSize)
    encryptionAlgo.encrypt(result)
    currentIndex += jump
    bound -= jump
    return result
  }
}
