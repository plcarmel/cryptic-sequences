package net.plcarmel.crypticsequences.core.numbers

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class GenericBaseSystemTest {

  @ParameterizedTest
  @CsvSource(
    " 2,  1,          2",
    " 2,  2,          4",
    " 2,  3,          8",
    " 3,  1,          3",
    " 3,  2,          9",
    " 3,  3,         27",
    " 4,  1,          4",
    " 4,  2,         16",
    " 4,  3,         64",
    "10,  1,         10",
    "10,  2,        100",
    "10,  3,       1000",
    "10,  8,  100000000",
    "16,  1,         16",
    "16,  2,        256",
    "16,  3,       4096",
    "16,  8, 4294967296"
  )
  fun nb_values_are_computed_correctly(base: Int, wordSize: Int, expectedNbValues: Long) {
    assertEquals(expectedNbValues, GenericBaseSystem(base).nbValues(wordSize))
  }

  @ParameterizedTest
  @CsvSource(
    " 2,    0",
    " 2,    1",
    " 2,   12",
    " 2,  123",
    " 3,    0",
    " 3,    1",
    " 3,   12",
    " 3,  123",
    " 5,    1",
    " 5,   12",
    " 5,  123",
    " 5, 1234",
    "10,    1",
    "10,   12",
    "10,  123",
    "10, 1234",
    "10,    1",
    "10,   12",
    "10,  123",
    "10, 1234",
  )
  fun combine_digits_at_is_inverse_of_extract_digits_at(base: Int, value: Long) {
    val baseSystem = GenericBaseSystem(base)
    val bytes = byteArrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0)
    baseSystem.extractDigitsAt(bytes, value, 1, 10)
    assertEquals(value, baseSystem.combineDigitsFrom(bytes, 1, 10))
  }

  @Test
  fun extract_digits_at_adds_padding() {
    val baseSystem = GenericBaseSystem(10)
    val bytes = byteArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1)
    baseSystem.extractDigitsAt(bytes, 4567, 4, 7)
    assertArrayEquals(byteArrayOf(1,1,1,1,7,6,5,4,0,0,0,1,1,1), bytes)
  }

  @ParameterizedTest
  @CsvSource(
    " 2,      127, 1, 1, 1, 1, 1, 1, 1, 0",
    " 2,      255, 1, 1, 1, 1, 1, 1, 1, 1",
    " 2,       70, 0, 1, 1, 0, 0, 0, 1, 0",
    " 2,       65, 1, 0, 0, 0, 0, 0, 1, 0",
    "10, 47384794, 4, 9, 7, 4, 8, 3, 7, 4",
    "10, 68050747, 7, 4, 7, 0, 5, 0, 8, 6",
    "10, 89257120, 0, 2, 1, 7, 5, 2, 9, 8",
    "10, 91237406, 6, 0, 4, 7, 3, 2, 1, 9",
    "10, 16435778, 8, 7, 7, 5, 3, 4, 6, 1",
    "10, 63565425, 5, 2, 4, 5, 6, 5, 3, 6"
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
    assertArrayEquals(byteArrayOf(b0,b1,b2,b3,b4,b5,b6,b7), bytes)
  }
}
