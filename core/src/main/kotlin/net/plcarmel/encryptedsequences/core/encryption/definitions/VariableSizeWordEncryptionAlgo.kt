package net.plcarmel.encryptedsequences.core.encryption.definitions

interface VariableSizeWordEncryptionAlgo : WordProcessor {

  fun encrypt(word: IntArray)
}
