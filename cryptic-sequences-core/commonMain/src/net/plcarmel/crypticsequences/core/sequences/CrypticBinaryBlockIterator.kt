@file:Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")

package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import net.plcarmel.crypticsequences.core.numbers.BinaryBaseSystem

class CrypticBinaryBlockIterator(
  val baseIterator: Iterator<Long>,
  val baseSystem: BaseSystem,
  val wordSize: Int,
  val nbIntsPerBlock: Int
) : Iterator<ByteArray> {

  private val binaryIntIterator = CrypticBinaryIntIterator(baseIterator, baseSystem, wordSize)

  private val blockSizeInBytes = 4 * nbIntsPerBlock

  override fun hasNext(): Boolean = baseIterator.hasNext()

  private val base256 = BinaryBaseSystem(8)

  override fun next(): ByteArray {
    val block = ByteArray(size = blockSizeInBytes)
    var i = 0
    while (i < nbIntsPerBlock && binaryIntIterator.hasNext()) {
      val word = baseIterator.next()
      base256.extractDigitsAt(
        target = block,
        word = word,
        start = i,
        count = 4
      )
      i += 4
    }
    return if (i == blockSizeInBytes) block else block.take(i).toByteArray()
  }


}
