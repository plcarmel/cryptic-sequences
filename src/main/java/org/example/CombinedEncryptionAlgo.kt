package org.example

class CombinedEncryptionAlgo(private val algoList: List<WordEncryptionAlgo>) : WordEncryptionAlgo {

  override val base: Int
    get() = algoList[0].base

  override fun encrypt(word: IntArray) {
    algoList.forEach { it.encrypt(word) }
  }
}
