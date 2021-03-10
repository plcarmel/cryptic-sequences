package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.*

internal class CombineEncryptionAlgoTest {

  @ParameterizedTest
  @ValueSource(ints = [1, 2, 3, 123])
  fun is_implemented_correctly(nbMocks: Int) {
    val mocks = (0 until nbMocks).map { mock(EncryptionAlgo::class.java) }
    `when`(mocks[0].wordSize).thenReturn(555)
    val input = byteArrayOf()
    val tested = CombineEncryptionAlgo(mocks)
    tested.encrypt(input, 789)
    val inOrder = inOrder(*mocks.toTypedArray())
    mocks.forEach { inOrder.verify(it).encrypt(input, 789) }
    assertEquals(tested.wordSize, 555)
  }

}
