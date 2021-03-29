package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo

class IdentityEncryptionAlgo(override val wordSize: Int) : EncryptionAlgo {

  override fun encrypt(word: ByteArray, at: Int) { }

}
