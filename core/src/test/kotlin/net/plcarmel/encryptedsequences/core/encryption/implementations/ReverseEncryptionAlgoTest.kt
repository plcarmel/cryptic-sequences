package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.numbers.BaseSystem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito

internal class ReverseEncryptionAlgoTest {

  companion object {
    private val baseSystem: BaseSystem = Mockito.mock(BaseSystem::class.java)
  }

  @ParameterizedTest
  @CsvSource(
    "          7, 0,  1,          7",
    "         12, 0,  2,         21",
    "        123, 0,  3,        321",
    " 1234567890, 0, 10, 0987654321",
    " 1234567890, 3,  4, 1237654890",
    " 1234567890, 0,  3, 3214567890",
    " 1234567890, 7,  3, 1234567098"
  )
  fun when_valid_arguments_expected_result(input: String, at: Int, count: Int, expected: String) {
    val bytes = input.toByteArray()
    ReverseEncryptionAlgo(count, baseSystem).encrypt(bytes, at)
    assertEquals(expected, String(bytes))
  }

}
