package net.plcarmel.encryptedsequences.core.numbers

fun zeroPad(wordSize: Int, word: IntArray): IntArray =
  word + (1 .. wordSize - word.size).map { 0 }
