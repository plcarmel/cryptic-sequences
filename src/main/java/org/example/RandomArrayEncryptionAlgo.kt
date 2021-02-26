package org.example

import java.util.*
import kotlin.math.pow

class RandomArrayEncryptionAlgo(
  private val baseSystem: BaseSystem,
  @Suppress("MemberVisibilityCanBePrivate") val wordSize: Int,
  @Suppress("MemberVisibilityCanBePrivate") val seed: Long
) : WordEncryptionAlgo {

  override val base: Int
    get() = baseSystem.base

  private val rnd = Random(seed)
  private val n = base.toDouble().pow(wordSize).toInt()

  private val bijectionArray = (0 until n).shuffled(rnd).toIntArray()

  override fun encrypt(word: IntArray) {
    var workArray: IntArray? = null
    val largeWord = word.size > 2
    if (largeWord) {
      workArray = IntArray(size = 2)
      workArray[0] = word[0]
      workArray[1] = word[1]
    }
    val maxTwoDigitsWord = if (largeWord) workArray!! else word
    val digits = zeroPad(wordSize, baseSystem.extractDigits(bijectionArray[baseSystem.combineDigits(maxTwoDigitsWord)]))
    digits.indices.forEach { word[it] = digits[it] }
  }
}
