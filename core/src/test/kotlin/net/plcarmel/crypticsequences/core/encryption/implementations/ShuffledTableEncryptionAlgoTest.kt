package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

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

}
