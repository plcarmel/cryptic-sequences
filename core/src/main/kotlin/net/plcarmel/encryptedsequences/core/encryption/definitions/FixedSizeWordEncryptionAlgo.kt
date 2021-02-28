package net.plcarmel.encryptedsequences.core.encryption.definitions

interface FixedSizeWordEncryptionAlgo : FixedSizeWordProcessor {

  fun encrypt(word: ByteArray, at: Int = 0)

}
