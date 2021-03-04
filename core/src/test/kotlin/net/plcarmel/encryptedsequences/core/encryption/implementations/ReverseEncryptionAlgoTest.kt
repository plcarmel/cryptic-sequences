package net.plcarmel.encryptedsequences.core.encryption.implementations

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ReverseEncryptionAlgoTest {

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
  fun when_valid_arguments_then_expected_result(input: String, at: Int, count: Int, expected: String) {
    val bytes = input.toByteArray()
    ReverseEncryptionAlgo(count).encrypt(bytes, at)
    assertEquals(expected, String(bytes))
  }

}
