package net.plcarmel.encryptedsequences.core.encryption.definitions

interface EncryptionAlgo : NeedWordSize {

  fun encrypt(word: ByteArray, at: Int = 0)

}
