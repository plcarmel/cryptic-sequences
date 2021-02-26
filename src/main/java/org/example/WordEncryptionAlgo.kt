package org.example

interface WordEncryptionAlgo {

  val base: Int

  fun encrypt(word: IntArray)

}
