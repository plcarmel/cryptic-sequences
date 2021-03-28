package net.plcarmel.crypticsequences.core.encryption.implementations

/*
import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class TableEncryptionAlgoTest {

  companion object {

    private fun createBaseSystemMock(): BaseSystem {
      val baseSystem = Mockito.mock(BaseSystem::class.java)
      Mockito.`when`(baseSystem.base).thenReturn(10)
      Mockito.`when`(baseSystem.nbValues(Mockito.eq(1))).thenReturn(10)
      Mockito.`when`(baseSystem.nbValues(Mockito.eq(2))).thenReturn(100)
      return baseSystem
    }

  }

  @Test
  fun encrypt_is_implemented_correctly_for_word_size_1() {
    val baseSystem = createBaseSystemMock()
    val algo = TableEncryptionAlgo(wordSize = 1, baseSystem, intArrayOf(3, 9, 7))
    val input = byteArrayOf(0, 0, 0, 0)
    val tokenArray = byteArrayOf()
    Mockito.`when`(
      baseSystem.combineDigitsFrom(
        ArgumentMatchers.eq(input) ?: tokenArray,
        start = ArgumentMatchers.eq(2),
        count = ArgumentMatchers.eq(1)
      )
    ).thenReturn(2)
    algo.encrypt(input, 2)
    Mockito.verify(baseSystem)
      .combineDigitsFrom(
        ArgumentMatchers.eq(input) ?: tokenArray,
        start= ArgumentMatchers.eq(2),
        count= ArgumentMatchers.eq(1)
      )
    Mockito.verify(baseSystem)
      .extractDigitsAt(
        ArgumentMatchers.eq(input)
          ?: tokenArray,
        word= ArgumentMatchers.eq(7L),
        start= ArgumentMatchers.eq(2),
        count= ArgumentMatchers.eq(1)
      )
  }

  @Test
  fun encrypt_is_implemented_correctly_for_word_size_2() {
    val baseSystem = Mockito.mock(BaseSystem::class.java)
    val algo = TableEncryptionAlgo(wordSize = 2, baseSystem, intArrayOf(33, 99, 77, 88))
    val input = byteArrayOf(0, 0, 0, 0)
    val tokenArray = byteArrayOf()
    Mockito.`when`(
      baseSystem.combineDigitsFrom(
        Mockito.eq(input) ?: tokenArray,
        start = Mockito.eq(1),
        count = Mockito.eq(2)
      )
    ).thenReturn(3)
    algo.encrypt(input, 1)
    Mockito.verify(baseSystem)
      .combineDigitsFrom(Mockito.eq(input) ?: tokenArray, start= Mockito.eq(1), count= Mockito.eq(2))
    Mockito.verify(baseSystem)
      .extractDigitsAt(
        Mockito.eq(input) ?: tokenArray, word= Mockito.eq(88L), start= Mockito.eq(1), count= Mockito.eq(2)
      )
  }

}
*/
