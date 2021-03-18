package net.plcarmel.crypticsequences.core.encryption.definitions

interface EncryptionAlgo : NeedWordSize {

  fun encrypt(word: ByteArray, at: Int = 0)

}
