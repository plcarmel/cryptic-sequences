package net.plcarmel.encryptedsequences.core.sequences

import net.plcarmel.encryptedsequences.core.encryption.definitions.FixedSizeWordEncryptionAlgo
import net.plcarmel.encryptedsequences.core.encryption.implementations.ShuffledTableOverlapEncryptionAlgo
import net.plcarmel.encryptedsequences.core.numbers.BaseSystem
import java.util.*
import java.util.Spliterator.*
import java.util.function.Consumer

class CrypticSequence(
  val encryptionAlgo: FixedSizeWordEncryptionAlgo,
  private var startIndex: Long = 0,
  private var count: Long = encryptionAlgo.baseSystem.nbValues(encryptionAlgo.wordSize)
): Spliterator<IntArray> {

  init {
    count = count.coerceAtMost(encryptionAlgo.baseSystem.nbValues(encryptionAlgo.wordSize))
  }

  constructor(
    baseSystem: BaseSystem,
    wordSize: Int,
    key: Long,
    nbPasses: Int,
    startIndex: Long = 0,
    count: Long = baseSystem.nbValues(wordSize)
  ) : this(
    ShuffledTableOverlapEncryptionAlgo(baseSystem, key = key,  wordSize = wordSize, nbPasses = nbPasses),
    startIndex,
    count
  )

  private val baseSystem = encryptionAlgo.baseSystem
  private val wordSize = encryptionAlgo.wordSize

  override fun tryAdvance(consumer: Consumer<in IntArray>?): Boolean {
    if (count == 0L) {
      return false
    }
    if (consumer != null) {
      val result = IntArray(size = wordSize)
      baseSystem.extractDigitsAt(result, startIndex, 0, wordSize)
      encryptionAlgo.encrypt(result)
      consumer.accept(result)
    }
    startIndex++
    count--
    return true
  }

  override fun trySplit(): Spliterator<IntArray>? {
    return null
  }

  override fun estimateSize(): Long {
    return count
  }

  override fun characteristics(): Int {
    return SIZED or SUBSIZED or DISTINCT or IMMUTABLE or NONNULL or ORDERED
  }

}
