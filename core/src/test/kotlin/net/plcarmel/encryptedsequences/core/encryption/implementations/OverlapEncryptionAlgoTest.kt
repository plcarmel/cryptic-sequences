package net.plcarmel.encryptedsequences.core.encryption.implementations

import net.plcarmel.encryptedsequences.core.encryption.definitions.EncryptionAlgo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.*

class OverlapEncryptionAlgoTest {

  @ParameterizedTest
  @CsvSource(
    "   x, 0, 1, 0,  ,  ,  ,  ",
    "  xx, 0, 2, 0,  ,  ,  ,  ",
    " xxx, 0, 2, 0, 1, 0,  ,  ",
    "xxxx, 0, 2, 0, 1, 2, 1, 0",
    "xxxx, 1, 2, 1, 2, 1, 0,  "
  )
  fun when_valid_arguments_then_expected_result(
    input: String,
    at: Int,
    count: Int,
    at1: Int?,
    at2: Int?,
    at3: Int?,
    at4: Int?,
    at5: Int?
  ) {
    val baseAlgo = mock(EncryptionAlgo::class.java)
    val inorder = inOrder(baseAlgo)
    val bytes = input.toByteArray()
    val tokenArray = ByteArray(0)
    OverlapEncryptionAlgo(count, baseAlgo).encrypt(bytes, at)
    listOfNotNull(at1, at2, at3, at4, at5)
      .forEach { inorder.verify(baseAlgo).encrypt(eq(bytes) ?: tokenArray, eq(it)) }
  }


}
