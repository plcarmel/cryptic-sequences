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
) : Iterator<ByteArray> {

  private val indices = (0 until nbThreads.toInt())
  private val workers = indices.map { concurrencyLayer.createWorker() }.toTypedArray()
  private val nextBlocks = indices.map { concurrencyLayer.futureOf(listOf<ByteArray>()) }.toTypedArray()
  private val currentBlocks = indices.map { concurrencyLayer.futureOf(listOf<ByteArray>()) }.toTypedArray()
  private var currentBlockIndex = 0
  private var currentBlock = 0

  var assignedIndex = startAt
  var assignedCount = count

  init {
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
    nextBlocks[i] =
      if (range.first <= range.last) {
        workers[i].execute {
          val iterator = CrypticIterator(encryptionAlgo, range.first, range.last - range.first + 1)
          iterator.asSequence().toList()
        }
      } else concurrencyLayer.futureOf(listOf())
  }

  private fun newIteration() {
    indices.forEach { currentBlocks[it] = nextBlocks[it] }
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

  override fun next(): ByteArray {
    if (
      currentBlock <= indices.last
      && currentBlockIndex == currentBlocks[currentBlock].result.size
    ) {
      nextBlock()
    }
    if (currentBlock > indices.last) {
      newIteration()
    }
    val result = currentBlocks[currentBlock].result[currentBlockIndex++]
    count--
    if (!hasNext()) {
      workers.forEach(Worker::shutdown)
    }
    return result
  }


}
