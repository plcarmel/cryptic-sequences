@file:Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")

package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.numbers.BinaryBaseSystem

class CrypticBinaryBlockIterator(
  val baseIterator: CrypticLongIterator,
  val nbBytesPerWord: Int,
  val nbWordsPerBlock: Int
) : Iterator<ByteArray> {

  private val blockSizeInBytes = nbBytesPerWord * nbWordsPerBlock

  override fun hasNext(): Boolean = baseIterator.hasNext()

  private val base256 = BinaryBaseSystem(8)

  override fun next(): ByteArray {
    val block = ByteArray(size = blockSizeInBytes)
    var i = 0
    while (i < nbWordsPerBlock && baseIterator.hasNext()) {
      val word = baseIterator.next()
      base256.extractDigitsAt(
        target = block,
        word = word,
        start = i,
        count = nbBytesPerWord
      )
      i += nbBytesPerWord
    }
    return if (i == blockSizeInBytes) block else block.take(i).toByteArray()
  }


}
