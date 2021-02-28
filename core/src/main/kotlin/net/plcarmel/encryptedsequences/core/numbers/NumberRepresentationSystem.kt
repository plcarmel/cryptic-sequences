package net.plcarmel.encryptedsequences.core.numbers

class NumberRepresentationSystem(
  @Suppress("MemberVisibilityCanBePrivate") val symbols: CharArray
) {

  @Suppress("MemberVisibilityCanBePrivate")
  val baseSystem =
    if (symbols.size.countOneBits() == 1) BinaryBaseSystem(symbols.size.countTrailingZeroBits())
    else GenericBaseSystem(symbols.size)

  private val symbolToDigitMap = symbols.indices.map { symbols[it] to it }.toMap()

  private fun digitToSymbol(digit: Int): Char = symbols[digit]
  private fun symbolToDigit(char: Char): Int = symbolToDigitMap[char]!!

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
  fun parseToLong(representation: String): Long =
    baseSystem.combineDigitsFrom(parse(representation))

  companion object {

    private val base32hexAlphabet = (('0'..'9') + ('A'..'V')).toCharArray()
    private val mimeAlphabet = (('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('+','/')).toCharArray()
    private val asciiAlphabet = (0 until 256).map(Int::toChar).toCharArray()

    @Suppress("unused")
    val octal = NumberRepresentationSystem(base32hexAlphabet.take(8).toCharArray())
    @Suppress("unused")
    val decimal = NumberRepresentationSystem(base32hexAlphabet.take(10).toCharArray())
    @Suppress("unused")
    val hexadecimal = NumberRepresentationSystem(base32hexAlphabet.take(16).toCharArray())
    @Suppress("unused")
    val base32hex = NumberRepresentationSystem(base32hexAlphabet)
    @Suppress("unused")
    val mime64 = NumberRepresentationSystem(mimeAlphabet)
    @Suppress("unused")
    val ascii = NumberRepresentationSystem(asciiAlphabet)

  }

}
