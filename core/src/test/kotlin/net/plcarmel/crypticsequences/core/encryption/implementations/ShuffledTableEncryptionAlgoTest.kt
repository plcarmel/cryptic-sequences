package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.*
import java.lang.reflect.Field
import java.util.*

internal class ShuffledTableEncryptionAlgoTest {

  companion object {

    val allTablesField: Field = ShuffledTableEncryptionAlgo::class.java.getDeclaredField("allTables")

    init {
      allTablesField.trySetAccessible()
    }
  }

  // max(round(log_base(256)), 2)
  @ParameterizedTest
  @CsvSource(
    "  2, 8",
    "  3, 5",
    "  4, 4",
    "  5, 3",
    "  6, 3",
    "  7, 3",
    "  8, 3",
    "  9, 3",
    " 10, 2",
    " 11, 2",
    " 12, 2",
    " 13, 2",
    " 14, 2",
    " 15, 2",
    " 16, 2",
    " 17, 2",
    "256, 2"
  )
  fun word_size_is_computed_correctly(base: Int, expectedWordSize: Int) {
    val baseSystem = mock(BaseSystem::class.java)
    `when`(baseSystem.base).thenReturn(base)
    assertEquals(expectedWordSize, ShuffledTableEncryptionAlgo(baseSystem, 0).wordSize)
  }

  private class RandomMock : Random(0) {
    private var i = 0
    override fun nextInt(bound: Int): Int = i++.coerceAtMost(bound-1)
  }

  private fun createBaseSystemMock(): BaseSystem {
    val baseSystem = mock(BaseSystem::class.java)
    `when`(baseSystem.base).thenReturn(10)
    `when`(baseSystem.nbValues(eq(1))).thenReturn(10)
    `when`(baseSystem.nbValues(eq(2))).thenReturn(100)
    return baseSystem
  }

  private fun createAlgo(): ShuffledTableEncryptionAlgo {
    return ShuffledTableEncryptionAlgo(createBaseSystemMock(), 0, rnd = RandomMock())
  }

  @Test
  fun tables_are_computed_correctly() {
    val algo = createAlgo()
    @Suppress("UNCHECKED_CAST") val allTables = allTablesField.get(algo) as Map<Int, IntArray>
    assertArrayEquals((99 downTo 0).map { it }.toIntArray(), allTables[2])
    assertArrayEquals((9 downTo 0).map { it }.toIntArray(), allTables[1])
  }

  @Test
  fun encrypt_is_implemented_correctly_for_word_size_1() {
    val baseSystem = createBaseSystemMock()
    val algo = ShuffledTableEncryptionAlgo(baseSystem, wordSize = 1, key = 0)
    allTablesField.set(algo, hashMapOf(1 to intArrayOf(3, 9, 7)))
    val input = byteArrayOf(0, 0, 0, 0)
    val tokenArray = byteArrayOf()
    `when`(
      baseSystem.combineDigitsFrom(
        eq(input) ?: tokenArray,
        start = eq(2),
        count = eq(1)
      )
    ).thenReturn(2)
    algo.encrypt(input, 2)
    verify(baseSystem)
      .combineDigitsFrom(eq(input) ?: tokenArray, start=eq(2), count=eq(1))
    verify(baseSystem)
      .extractDigitsAt(eq(input) ?: tokenArray, word=eq(7L), start=eq(2), count=eq(1))
  }

  @Test
  fun encrypt_is_implemented_correctly_for_word_size_2() {
    val baseSystem = mock(BaseSystem::class.java)
    val algo = ShuffledTableEncryptionAlgo(baseSystem, wordSize = 2, key = 0)
    allTablesField.set(algo, hashMapOf(2 to intArrayOf(33, 99, 77, 88)))
    val input = byteArrayOf(0, 0, 0, 0)
    val tokenArray = byteArrayOf()
    `when`(
      baseSystem.combineDigitsFrom(
        eq(input) ?: tokenArray,
        start = eq(1),
        count = eq(2)
      )
    ).thenReturn(3)
    algo.encrypt(input, 1)
    verify(baseSystem)
      .combineDigitsFrom(eq(input) ?: tokenArray, start=eq(1), count=eq(2))
    verify(baseSystem)
      .extractDigitsAt(eq(input) ?: tokenArray, word=eq(88L), start=eq(1), count=eq(2))
  }

}
