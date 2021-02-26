package org.example.numbers

class NumberRepresentationSystem(@Suppress("MemberVisibilityCanBePrivate") val symbols: CharArray) {

  @Suppress("MemberVisibilityCanBePrivate")
  val baseSystem = BaseSystem(base = symbols.size)

  private val symbolToDigitMap = symbols.indices.map { symbols[it] to it }.toMap()

  private fun digitToSymbol(digit: Int): Char = symbols[digit]
  private fun symbolToDigit(char: Char): Int = symbolToDigitMap[char]!!

  fun format(number: Int): String =
    baseSystem.extractDigits(number).let(::format)

  @Suppress("MemberVisibilityCanBePrivate")
  fun format(digits: IntArray): String =
    String(digits.reversed().map(::digitToSymbol).toCharArray())

  @Suppress("MemberVisibilityCanBePrivate")
  fun parse(representation: CharArray): IntArray =
    representation.map(::symbolToDigit).reversed().toIntArray()

  @Suppress("MemberVisibilityCanBePrivate")
  fun parse(representation: String): IntArray =
    representation.toCharArray().let(::parse)

  @Suppress("unused")
  fun parseToInt(representation: String): Int =
    baseSystem.combineDigits(parse(representation))

  companion object {

    private val base10symbols = ('0'..'9').toList().toCharArray()
    private val base16symbols = (('0'..'9') + ('A'..'F')).toCharArray()
    private val base64symbols = (('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('+','/')).toCharArray()

    @Suppress("unused")
    val base8 = NumberRepresentationSystem(base10symbols.take(8).toCharArray())
    @Suppress("unused")
    val base10 = NumberRepresentationSystem(base10symbols)
    @Suppress("unused")
    val base16 = NumberRepresentationSystem(base16symbols)
    @Suppress("unused")
    val base32 = NumberRepresentationSystem(base64symbols.take(32).toCharArray())
    @Suppress("unused")
    val base64 = NumberRepresentationSystem(base64symbols)
  }

}
