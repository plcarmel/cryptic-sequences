package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.*

class CrypticIteratorTest {

  companion object {

    private fun newEncryptionAlgoWithBaseSystemThatHasSevenValues(): NumberBasedEncryptionAlgo {
      val encryptionAlgo = mock(NumberBasedEncryptionAlgo::class.java)
      val baseSystem = mock(BaseSystem::class.java)
      `when`(encryptionAlgo.wordSize).thenReturn(3)
      `when`(encryptionAlgo.baseSystem).thenReturn(baseSystem)
      `when`(baseSystem.nbValues(eq(3))).thenReturn(7)
      return encryptionAlgo
    }

  }

  @ParameterizedTest
  @CsvSource(
    " 0, 10, 7",
    " 0,  5, 5",
    " 2, 10, 5",
    " 3,  5, 4",
    " 9,  3, 0"
  )
  fun count_is_coerced_and_computed_correctly(
    currentIndex: Long,
    desiredCount: Long,
    expectedCount: Long
  ) {
    val iterator =
      CrypticIterator(
        newEncryptionAlgoWithBaseSystemThatHasSevenValues(),
        startAt = currentIndex,
        count = desiredCount
      )
    assertEquals(expectedCount, iterator.count)
  }

  @Test
  fun when_count_is_zero_then_has_next_is_false_and_next_throws_exception() {
    val iterator =
      CrypticIterator(
        newEncryptionAlgoWithBaseSystemThatHasSevenValues(),
        startAt = 4567,
        count = 0
      )
    assertFalse(iterator.hasNext())
    assertThrows<NoSuchElementException> { iterator.next() }
  }

  @Test
  fun when_next_is_called_then_index_incremented_and_count_is_decremented_and_calls_are_made() {
    val encryptionAlgo = newEncryptionAlgoWithBaseSystemThatHasSevenValues()
    val iterator =
      CrypticIterator(
        encryptionAlgo,
        startAt = 1,
        count = 5
      )
    assertEquals(iterator.currentIndex, 1)
    assertEquals(iterator.count, 5)
    iterator.next()
    assertEquals(iterator.currentIndex, 2)
    assertEquals(iterator.count, 4)
    val tokenArray = ByteArray(0)
    verify(encryptionAlgo.baseSystem)
      .extractDigitsAt(eq(byteArrayOf(0, 0, 0)) ?: tokenArray, eq(1L), eq(0), eq(3))
    verify(encryptionAlgo)
      .encrypt(eq(byteArrayOf(0, 0, 0)) ?: tokenArray, eq(0))
  }

}
