package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.*

internal class MultiPassEncryptionAlgoTest {

  @ParameterizedTest
  @ValueSource(ints = [0, 1, 2, 3, 123])
  fun isImplementedCorrectly(nbPasses: Int) {
    val baseAlgo = mock(EncryptionAlgo::class.java)
    val input = byteArrayOf()
    MultiPassEncryptionAlgo(baseAlgo, nbPasses).encrypt(input, 789)
    verify(baseAlgo, times(nbPasses)).encrypt(input, 789)
  }

}
