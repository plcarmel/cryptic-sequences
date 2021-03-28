package net.plcarmel.crypticsequences.cli

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

abstract class BitQueueTest(private val bitQueue: BitQueue) {

  @BeforeEach
  fun clearQueue() {
    bitQueue.clear()
  }

  @ParameterizedTest
  @CsvSource("0x123456, 24, 12, 0x123, 0x456")
  fun size_is_correct_after_put_and_pop(
    push: Long,
    pushCount: Int,
    popCount: Int,
    expectedPopped: Long,
    expectedLeft: Long
  ) {
    bitQueue.put(push, pushCount)
    val popped = bitQueue.get(popCount)
    val left = bitQueue.get(pushCount - popCount)
    assertEquals(expectedPopped, popped)
    assertEquals(expectedLeft, left)
    assertEquals(bitQueue.size, 0)
  }

  @ParameterizedTest
  @CsvSource("0x123456, 24")
  fun add_bits_from_number_works_for_binary_numbers(x: Long, nbBits: Int) {
    bitQueue.addRandomBitsFromNumber(x, 1L shl nbBits)
    assertEquals(nbBits, bitQueue.size)
    assertEquals(x, bitQueue.get(nbBits))
  }

  @ParameterizedTest
  @CsvSource(
    "0x123, 12, 0x456, 12, 0x123456, 24",
    " 0x12,  8, 0x456, 12,  0x12456, 20"
  )
  fun put_put(x: Long, nx: Int, y: Long, ny: Int, z: Long, nz: Int) {
    bitQueue.put(x, nx)
    bitQueue.put(y, ny)
    assertEquals(nz, bitQueue.size)
    val r = bitQueue.get(nz)
    assertEquals(z, r)
  }
}
