package net.plcarmel.crypticsequences.core.numbers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BinaryBaseSystemTest {

  @ParameterizedTest
  @CsvSource(
    " 2,  1,          2",
    " 2,  2,          4",
    " 2,  3,          8",
    " 4,  1,          4",
    " 4,  2,         16",
    " 4,  3,         64",
    " 8,  1,          8",
    " 8,  2,         64",
    " 8,  3,        512",
    " 8,  8,   16777216",
    "16,  1,         16",
    "16,  2,        256",
    "16,  3,       4096",
    "16,  8, 4294967296"
  )
  fun nb_values_are_computed_correctly(base: Int, wordSize: Int, expectedNbValues: Long) {
    assertEquals(1, base.countOneBits())
    assertEquals(expectedNbValues, BinaryBaseSystem(nbBits=base.countTrailingZeroBits()).nbValues(wordSize))
  }

  @ParameterizedTest
  @CsvSource(
    " 2,    0",
    " 2,    1",
    " 2,   12",
    " 2,  123",
    " 4,    0",
    " 4,    1",
    " 4,   12",
    " 4,  123",
    " 8,    1",
    " 8,   12",
    " 8,  123",
    " 8, 1234",
    "16,    1",
    "16,   12",
    "16,  123",
    "16, 1234",
    "16,    1",
    "16,   12",
    "16,  123",
    "16, 1234",
  )
  fun combine_digits_at_is_inverse_of_extract_digits_at(base: Int, value: Long) {
    assertEquals(1, base.countOneBits())
    val baseSystem = BinaryBaseSystem(nbBits = base.countTrailingZeroBits())
    val bytes = byteArrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0)
    baseSystem.extractDigitsAt(bytes, value, 1, 10)
    assertEquals(value, baseSystem.combineDigitsFrom(bytes, 1, 10))
  }

  @Test
  fun extract_digits_at_adds_padding() {
    val baseSystem = GenericBaseSystem(10)
    val bytes = byteArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1)
    baseSystem.extractDigitsAt(bytes, 4567, 4, 7)
    Assertions.assertArrayEquals(byteArrayOf(1, 1, 1, 1, 7, 6, 5, 4, 0, 0, 0, 1, 1, 1), bytes)
  }

  @ParameterizedTest
  @CsvSource(
    " 2,     127, 1, 1, 1, 1, 1, 1, 1, 0",
    " 2,     255, 1, 1, 1, 1, 1, 1, 1, 1",
    " 2,      70, 0, 1, 1, 0, 0, 0, 1, 0",
    " 2,      65, 1, 0, 0, 0, 0, 0, 1, 0",
    " 8, 7384794, 2, 3, 3, 7, 2, 1, 4, 3",
    " 8, 8050747, 3, 7, 0, 4, 5, 5, 6, 3",
    " 8, 9257120, 0, 4, 2, 0, 4, 2, 3, 4",
    " 8, 1237406, 6, 3, 6, 0, 6, 5, 4, 0",
    " 8, 6435778, 2, 0, 7, 1, 3, 4, 0, 3",
    " 8, 3565425, 1, 6, 5, 3, 6, 4, 5, 1"
  )
  fun extract_digits_at_is_implemented_correctly(
    base: Int,
    number: Long,
    b0: Byte, b1: Byte, b2: Byte, b3: Byte,
    b4: Byte, b5: Byte, b6: Byte, b7: Byte
  ) {
    val baseSystem = GenericBaseSystem(base)
    val bytes = byteArrayOf(7,7,7,7,7,7,7,7)
    baseSystem.extractDigitsAt(bytes, number, 0, 8)
    Assertions.assertArrayEquals(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7), bytes)
  }
}
