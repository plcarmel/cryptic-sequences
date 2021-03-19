package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.encryption.definitions.EncryptionAlgo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.*

class OverlapEncryptionAlgoTest {

  @ParameterizedTest
  @CsvSource(
    "  xx, 0, 2, 0,  ,  ",
    " xxx, 0, 3, 0, 1,  ",
    "xxxx, 0, 4, 0, 1, 2",
    "xxxx, 1, 3, 1, 2,  "
  )
  fun when_valid_arguments_then_expected_result(
    input: String,
    at: Int,
    count: Int,
    at1: Int?,
    at2: Int?,
    at3: Int?,
  ) {
    val baseAlgo = mock(EncryptionAlgo::class.java)
    `when`(baseAlgo.wordSize).thenReturn(2)
    val bytes = input.toByteArray()
    val tokenArray = ByteArray(0)
    OverlapEncryptionAlgo(count, baseAlgo).encrypt(bytes, at)
    val inorder = inOrder(baseAlgo)
    listOfNotNull(at1, at2, at3)
      .forEach { inorder.verify(baseAlgo).encrypt(eq(bytes) ?: tokenArray, eq(it)) }
    inorder.verifyNoMoreInteractions()
  }


}
