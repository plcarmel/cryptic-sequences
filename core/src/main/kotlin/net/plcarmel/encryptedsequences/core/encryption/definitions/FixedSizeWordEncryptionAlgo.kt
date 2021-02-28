package net.plcarmel.encryptedsequences.core.encryption.definitions

interface FixedSizeWordEncryptionAlgo : FixedSizeWordProcessor {

  fun encrypt(word: IntArray, at: Int = 0)

}
