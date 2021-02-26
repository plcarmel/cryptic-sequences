package org.example

class CesarEncryptionAlgo(
  override val base: Int,
  val mask: IntArray
) : WordEncryptionAlgo {

  override fun encrypt(word: IntArray) {
      word.indices.forEach { word[it] = (mask[it] + word[it]) % base }
  }

}
