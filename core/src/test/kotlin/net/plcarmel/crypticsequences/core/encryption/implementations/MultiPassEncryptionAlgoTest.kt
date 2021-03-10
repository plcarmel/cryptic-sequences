package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.*

internal class MultiPassEncryptionAlgoTest {

  @ParameterizedTest
  @ValueSource(ints = [0, 1, 2, 3, 123])
  fun is_implemented_correctly(nbPasses: Int) {
    val baseAlgo = mock(EncryptionAlgo::class.java)
    `when`(baseAlgo.wordSize).thenReturn(555)
    val input = byteArrayOf()
    val tested = MultiPassEncryptionAlgo(baseAlgo, nbPasses)
    tested.encrypt(input, 789)
    verify(baseAlgo, times(nbPasses)).encrypt(input, 789)
    Assertions.assertEquals(tested.wordSize, 555)
  }

}
