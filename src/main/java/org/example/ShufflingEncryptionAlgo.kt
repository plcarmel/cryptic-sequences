package org.example

/**
 * @param baseSystem
 *  the base of the digits to encrypt
 * @param digitPairBijection
 *  An bijection to shuffle digit pairs around
 * @param nbPasses
 *  the number of times the encryption algorithm is executed
 *  If the value is too low, the produced encrypted words will not exhibit good pseudo-random properties.
 *
 */
class ShufflingEncryptionAlgo(
  private val baseSystem: BaseSystem,
  private val digitPairBijection: WordEncryptionAlgo,
  @Suppress("MemberVisibilityCanBePrivate") val nbPasses: Int = 10
) : WordEncryptionAlgo {

  override val base: Int
    get() = baseSystem.base

  private val shuffledDigits =
    (0 until base)
      .map { intArrayOf(0, it) }
      .onEach(digitPairBijection::encrypt)
      .map(baseSystem::combineDigits)
      .let(Companion::getOrder)

  override fun encrypt(word: IntArray) {
    replaceDigits(word)
    repeat(nbPasses * word.size) { replaceDigitPairs(word) }
  }

  private fun replaceDigits(tag: IntArray) {
    tag.indices.forEach { tag[it] = shuffledDigits[tag[it]] }
  }

  private fun replaceDigitPairs(tag: IntArray) {
    val a = IntArray(2)
    if (tag.size > 1) {
      val n = tag.size
      tag.indices.forEach {
        val i = it
        val j = (it + 1) % n
        a[0] = tag[i]
        a[1] = tag[j]
        digitPairBijection.encrypt(a)
        tag[i] = a[0]
        tag[j] = a[1]
      }
    }
  }

  companion object {

    private fun getOrder(numbers: List<Int>): List<Int> =
      numbers
        .indices
        .map { Pair(it, numbers[it]) }
        .sortedBy { (_, n) -> n }
        .map { (i, _) -> i }

  }

}
