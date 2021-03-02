package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.numbers.BaseSystem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class RotateEncryptionAlgoTest {

  companion object {
    fun newBytes() = "0123456789".toByteArray()
  }

  @Test
  fun when_partial_word_size() {
    val bytes = newBytes()
    RotateEncryptionAlgo(4, mock(BaseSystem::class.java)).encrypt(bytes)
    assertEquals("1230456789", String(bytes))
  }

  @Test
  fun when_partial_word_size_at() {
    val bytes = newBytes()
    RotateEncryptionAlgo(4, mock(BaseSystem::class.java)).encrypt(bytes, 2)
    assertEquals("0134526789", String(bytes))
  }

  @Test
  fun when_partial_word_size_at_limit() {
    val bytes = newBytes()
    RotateEncryptionAlgo(4, mock(BaseSystem::class.java)).encrypt(bytes, 6)
    assertEquals("0123457896", String(bytes))
  }

  @Test
  fun when_full_word_size() {
    val bytes = newBytes()
    RotateEncryptionAlgo(10, mock(BaseSystem::class.java)).encrypt(bytes)
    assertEquals("1234567890", String(bytes))
  }

  @Test
  fun when_word_size_one() {
    val bytes = newBytes()
    RotateEncryptionAlgo(1, mock(BaseSystem::class.java)).encrypt(bytes)
    assertEquals("0123456789", String(bytes))
  }

  @Test
  fun when_one_byte_and_word_size_one() {
    val bytes = newBytes().take(1).toByteArray()
    RotateEncryptionAlgo(1, mock(BaseSystem::class.java)).encrypt(bytes)
    assertEquals("0", String(bytes))
  }
}
