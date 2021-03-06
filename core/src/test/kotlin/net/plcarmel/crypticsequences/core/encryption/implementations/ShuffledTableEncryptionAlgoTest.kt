package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.lang.reflect.Field
import java.util.*

internal class ShuffledTableEncryptionAlgoTest {

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

  @Test
  fun tables_are_computed_correctly() {
    val baseSystem = mock(BaseSystem::class.java)
    `when`(baseSystem.base).thenReturn(10)
    `when`(baseSystem.nbValues(eq(1))).thenReturn(10)
    `when`(baseSystem.nbValues(eq(2))).thenReturn(100)
    val algo = ShuffledTableEncryptionAlgo(baseSystem, 0, rnd = RandomMock())
    val field: Field =
      ShuffledTableEncryptionAlgo::class
        .java
        .getDeclaredField("allTables")
    field.trySetAccessible()
    @Suppress("UNCHECKED_CAST") val allTables = field.get(algo) as Map<Int, IntArray>
    assertArrayEquals((99 downTo 0).map { it }.toIntArray(), allTables[2])
    assertArrayEquals((9 downTo 0).map { it }.toIntArray(), allTables[1])
  }
}
