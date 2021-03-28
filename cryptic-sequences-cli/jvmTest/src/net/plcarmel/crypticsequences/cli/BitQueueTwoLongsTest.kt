package net.plcarmel.crypticsequences.cli

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BitQueueTwoLongsTest : BitQueueTest(BitQueueTwoLongs()) {

  @Test
  fun restore_whole_bytes_numbers() {
    val bp = BitQueueTwoLongs()
    val word = 0x00110987654321
    bp.put(word, 48)
    val result = bp.get(48)
    assertEquals(word, result)
  }

  @Test
  fun split_between_whole_bytes () {
    val bp = BitQueueTwoLongs()
    val word = 0x00110987654321
    bp.put(word, 48)
    val result1 = bp.get(32)
    val result2 = bp.get(16)
    assertEquals(0x11098765, result1)
    assertEquals(0x4321, result2)
  }

  @Test
  fun split_within_byte () {
    val bp = BitQueueTwoLongs()
    val word = 0x00110987654321
    bp.put(word, 48)
    val result1 = bp.get(28)
    val result2 = bp.get(20)
    assertEquals(0x01109876, result1)
    assertEquals(0x00054321, result2)
  }

}
