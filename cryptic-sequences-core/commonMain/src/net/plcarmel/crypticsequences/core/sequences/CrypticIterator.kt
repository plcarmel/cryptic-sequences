package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo

class CrypticIterator(
  val encryptionAlgo: NumberBasedEncryptionAlgo,
  var currentIndex: Long = 0,
  var count: Long = encryptionAlgo.baseSystem.nbValues(encryptionAlgo.wordSize)
): Iterator<ByteArray> {

  init {
    count =
      count
        .coerceAtMost(encryptionAlgo.baseSystem.nbValues(encryptionAlgo.wordSize) - currentIndex)
        .coerceAtLeast(0)
  }

  private val baseSystem = encryptionAlgo.baseSystem
  private val wordSize = encryptionAlgo.wordSize

  override fun hasNext(): Boolean {
    return count != 0L
  }

  override fun next(): ByteArray {
    if (count == 0L) throw NoSuchElementException()
    val result = ByteArray(size = wordSize)
    baseSystem.extractDigitsAt(result, currentIndex, 0, wordSize)
    encryptionAlgo.encrypt(result)
    currentIndex++
    count--
    return result
  }
}
