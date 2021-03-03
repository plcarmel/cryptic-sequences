package net.plcarmel.encryptedsequences.core.encryption.implementations

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class RotateEncryptionAlgoTest {

  @ParameterizedTest
  @CsvSource(
    "          7, 0,  1,          7",
    "         12, 0,  2,         21",
    "        123, 0,  3,        231",
    " 1234567890, 0, 10, 2345678901",
    " 1234567890, 3,  4, 1235674890",
    " 1234567890, 0,  3, 2314567890",
    " 1234567890, 7,  3, 1234567908"
  )
  fun when_valid_arguments_expected_result(input: String, at: Int, count: Int, expected: String) {
    val bytes = input.toByteArray()
    RotateEncryptionAlgo(count).encrypt(bytes, at)
    assertEquals(expected, String(bytes))
  }
}
