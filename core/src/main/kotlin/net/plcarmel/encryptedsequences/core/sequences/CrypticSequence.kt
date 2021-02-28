package net.plcarmel.encryptedsequences.core.sequences

import net.plcarmel.encryptedsequences.core.encryption.definitions.VariableSizeWordEncryptionAlgo
import net.plcarmel.encryptedsequences.core.encryption.implementations.ShuffledTableOverlapEncryptionAlgo
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem
import java.util.*
import java.util.Spliterator.*
import java.util.function.Consumer

class CrypticSequence(
  @Suppress("MemberVisibilityCanBePrivate") val wordSize: Int,
  val encryptionAlgo: VariableSizeWordEncryptionAlgo,
  private var startIndex: Long = 0,
  private var endIndex: Long = encryptionAlgo.baseSystem.nbValues(wordSize)
): Spliterator<IntArray> {

  constructor(
    baseSystem: BaseSystem,
    wordSize: Int,
    key: Long,
    strength: Int,
    startIndex: Long = 0,
    endIndex: Long = baseSystem.nbValues(wordSize)
  ) : this(
    wordSize,
    ShuffledTableOverlapEncryptionAlgo(baseSystem, key, strength),
    startIndex,
    endIndex
  )

  private val baseSystem = encryptionAlgo.baseSystem

  override fun tryAdvance(consumer: Consumer<in IntArray>?): Boolean {
    if (startIndex == endIndex) {
      return false
    }
    if (consumer != null) {
      val result = IntArray(size = wordSize)
      baseSystem.extractDigitsAt(result, startIndex, 0, wordSize)
      encryptionAlgo.encrypt(result)
      consumer.accept(result)
    }
    startIndex++
    return true
  }

  override fun trySplit(): Spliterator<IntArray>? {
    if (estimateSize() <= 1) {
      return null
    }
    val otherEnd = endIndex
    endIndex = startIndex + estimateSize().toInt()/2
    return CrypticSequence(wordSize, encryptionAlgo, endIndex + 1, otherEnd)
  }

  override fun estimateSize(): Long {
    return (endIndex - startIndex)
  }

  override fun characteristics(): Int {
    return SIZED or SUBSIZED or DISTINCT or IMMUTABLE or NONNULL
  }

}
