package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.concurrency.*
import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo

class ParallelizedCrypticSequenceIterator(
  private val encryptionAlgo: NumberBasedEncryptionAlgo,
  startAt: Long = 0,
  private var count: Long = encryptionAlgo.baseSystem.nbValues(encryptionAlgo.wordSize),
  private var concurrencyLayer: ConcurrencyLayer,
  nbThreads: Long = 1,
  private val bufferSize: Int = 65536
) : Iterator<Long> {

  private val indices = (0 until nbThreads.toInt())
  private val workers = indices.map { concurrencyLayer.createWorker() }.toTypedArray()
  private val nextBlocks = indices.map { concurrencyLayer.futureOf(longArrayOf()) }.toTypedArray()
  private val currentBlocks = indices.map { longArrayOf() }.toTypedArray()
  private var currentBlockIndex = 0
  private var currentBlock = 0

  var assignedIndex = startAt
  var assignedCount = count

  init {
    concurrencyLayer.freeze(concurrencyLayer)
    concurrencyLayer.freeze(encryptionAlgo)
    newIteration()
    newIteration()
  }

  private fun computeNewIndicesToAssign() : LongRange {
    val start = assignedIndex
    val count = assignedCount.coerceAtMost(bufferSize.toLong())
    assignedIndex += count
    assignedCount -= count
    return (start until start + count)
  }

  private fun launchNewComputation(i: Int, range: LongRange) {
    concurrencyLayer.freeze(range)
    nextBlocks[i] =
      if (range.first <= range.last) {
        workers[i].execute {
          val baseSystem = encryptionAlgo.baseSystem
          val wordSize = encryptionAlgo.wordSize
          val bytes = ByteArray(size = wordSize)
          concurrencyLayer.freeze(
            range.asSequence().map {
              baseSystem.extractDigitsAt(bytes, it, 0, encryptionAlgo.wordSize)
              encryptionAlgo.encrypt(bytes)
              baseSystem.combineDigitsFrom(bytes)
            }.toList().toLongArray()
          )
        }
      } else concurrencyLayer.futureOf(longArrayOf())
  }

  private fun newIteration() {
    indices.forEach { currentBlocks[it] = nextBlocks[it].result }
    indices.forEach { launchNewComputation(it, computeNewIndicesToAssign()) }
    currentBlock = 0
  }

  override fun hasNext(): Boolean {
    return count != 0L
  }

  private fun nextBlock() {
    currentBlock++
    currentBlockIndex = 0

  }

  override fun next(): Long {
    if (
      currentBlock <= indices.last
      && currentBlockIndex == currentBlocks[currentBlock].size
    ) {
      nextBlock()
    }
    if (currentBlock > indices.last) {
      newIteration()
    }
    val result = currentBlocks[currentBlock][currentBlockIndex++]
    count--
    if (!hasNext()) {
      workers.forEach(Worker::shutdown)
    }
    return result
  }


}
