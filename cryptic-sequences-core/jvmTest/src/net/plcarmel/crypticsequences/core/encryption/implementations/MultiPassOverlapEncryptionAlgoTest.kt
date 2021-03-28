package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo
import net.plcarmel.crypticsequences.core.numbers.GenericBaseSystem
import net.plcarmel.crypticsequences.core.sequences.CrypticIterator
import net.plcarmel.crypticsequences.core.sequences.CrypticLongIterator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

abstract class MultiPassOverlapEncryptionAlgoTest(
  private var algoFactory: (base: Int, size: Int, strength: Int, key: Long) -> NumberBasedEncryptionAlgo
) {

  @ParameterizedTest
  @CsvSource(
    " 2, 13, 17, 74, 0x0000000000000000,     1903,     6394,     7341,     2315",
    " 2, 13, 17, 74, 0x77ea3858b80f8be1,     1538,     7831,     7520,     6351",
    " 3,  4,  9, 12, 0x0000000000000000,       75,       13,       15,       33",
    " 4,  2, 29,  2, 0x5c4da6f327c10bda,        7,        4,       10,        8",
    " 5,  3,  8,  3, 0x6c8730ad1a4da7a6,       34,       61,       95,       82",
    " 6,  5, 13,  3, 0x4557578f5657d25e,     6756,     5513,     2169,     7119",
    " 7,  8, 12,  3, 0x1dbc35fed53d9fcf,  4751206,   810043,  4114633,  4267753",
    " 8,  7,  7,  1, 0x4b26fff312695014,  1601527,   428061,  1091859,  1444832",
    " 9,  1, 28,  4, 0x7efa909dacdbe9c6,        2,        3,        4,        6",
    "10,  2,  4,  1, 0x0d8d73d1097a66a9,       72,       48,       34,        9",
    "12,  7,  5,  1, 0x225f1ccab811e81b,  9215149, 33925098,  2731191, 25489038",
    "11,  6,  4,  1, 0x26ed0866e0e7b4b8,  1042376,   613244,   347473,   328689",
    "14,  6, 28,  2, 0x3357abd4703a2dea,  3593085,   727469,  4010783,  6497236",
    "15,  3, 16,  4, 0x2c0455c7c15fe5ff,     1101,      429,     2858,      283",
    "16,  1, 22,  3, 0x089f1a0d557054aa,        1,        0,       11,       12"
  )
  fun test_that_algo_did_not_change(
    base: Int,
    size: Int,
    strength: Int,
    start: Long,
    key: Long,
    n0: Long?, n1: Long?, n2: Long?, n3: Long?
  ) {
    val algo = algoFactory(base, size, strength, key)
    val iterator = CrypticLongIterator(CrypticIterator(algo, start, 4), GenericBaseSystem(base))
    val next = { if (iterator.hasNext()) iterator.next() else null }
    assertEquals(n0, next())
    assertEquals(n1, next())
    assertEquals(n2, next())
    assertEquals(n3, next())
  }

}
