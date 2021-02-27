package net.plcarmel.encryptedsequences.core.encryption.definitions

interface FixedSizeWordEncryptionAlgo : FixedSizeWordProcessor {

  fun subArray(src: IntArray, start: Int, count: Int): IntArray =
    (0 until count).map { src[(it + start) % src.size] }.toIntArray()

  fun copyToArrayAt(dst: IntArray, dstStart: Int, src: IntArray) =
    src.indices.forEach { dst[(dstStart + it) % dst.size] = src[it] }

  fun encrypt(word: IntArray, at: Int = 0)

}
