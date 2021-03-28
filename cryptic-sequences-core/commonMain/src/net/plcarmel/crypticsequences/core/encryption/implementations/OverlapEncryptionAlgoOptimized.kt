package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo

/**
 * Encryption algorithm that takes an base encryption algo operating on words of fixed size and extend it so it can
 * be applied to words of any size. Multiple passes of this algorithm are likely to be needed to achieve the
 * desired result. This is an optimized version of @see OverlapEncryptionAlgo.
 *
 * Note: Please do NOT use this to encrypt sensitive data as this is not a real vetted encryption algorithm !
 *
 * @param baseEncryption
 *  the base encryption that will be applied repetitively throughout the word
 *
 */
class OverlapEncryptionAlgoOptimized(
  override val wordSize: Int,
  private val baseEncryption: TableEncryptionAlgo
) : EncryptionAlgo {

  private val n = 1 + wordSize - baseEncryption.wordSize
  private val baseSystem = baseEncryption.baseSystem

  private val ws = baseEncryption.wordSize
  private val ws1 = ws-1
  private val lines: Array<IntArray>

  init {
    val base = baseSystem.base
    val m = baseSystem.nbValues(ws1).toInt()
    val table = baseEncryption.table
    lines = (0 until base).map { j -> (0 until m).map { i -> table[j*m+i] }.toIntArray() }.toTypedArray()
  }

  private val extractedDigits =
    (0 until baseSystem.nbValues(ws)).map { i ->
        val a = ByteArray(ws)
        baseSystem.extractDigitsAt(a, i, 0, ws)
        a
    }.toTypedArray()

  override fun encrypt(word: ByteArray, at: Int) {
    if (ws1 == 1) {
      var a = lines[word[1].toInt()]
      var b: IntArray
      for (i in 0 until n-1) {
        val x = a[word[i].toInt()].toLong()
        b = lines[word[i+2].toInt()]
        val ds = extractedDigits[x.toInt()]
        word[i] = ds[0]
        word[i+1] = ds[1]
        a = b
      }
      val x = a[word[n-1].toInt()].toLong()
      val ds = extractedDigits[x.toInt()]
      word[n-1] = ds[0]
      word[n] = ds[1]
    } else {
      var a = lines[word[ws1].toInt()]
      var b: IntArray
      for (i in 0 until n-1) {
          val x = a[baseSystem.combineDigitsFrom(word, i, ws1).toInt()].toLong()
          b = lines[word[i+1+ws1].toInt()]
          val ds = extractedDigits[x.toInt()]
          for (j in 0 until ws) { word[i+j] = ds[j] }
          a = b
      }
      val x = a[baseSystem.combineDigitsFrom(word, n-1, ws1).toInt()].toLong()
      val ds = extractedDigits[x.toInt()]
      for (j in 0 until ws) { word[n-1+j] = ds[j] }
    }
  }
}
