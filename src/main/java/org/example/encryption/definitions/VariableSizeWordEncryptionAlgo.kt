package org.example.encryption.definitions

interface VariableSizeWordEncryptionAlgo : WordProcessor {

  fun encrypt(word: IntArray)
}
