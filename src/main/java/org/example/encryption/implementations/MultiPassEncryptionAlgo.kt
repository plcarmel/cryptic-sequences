package org.example.encryption.implementations

import org.example.encryption.definitions.VariableSizeWordEncryptionAlgo

class MultiPassEncryptionAlgo(
  private val baseAlgo: VariableSizeWordEncryptionAlgo,
  @Suppress("MemberVisibilityCanBePrivate") val nbRepetitions: (Int) -> Int
) : VariableSizeWordEncryptionAlgo {

  override val base: Int
    get() = baseAlgo.base

  override fun encrypt(word: IntArray) {
    repeat(nbRepetitions(word.size)) { baseAlgo.encrypt(word) }
  }

}
