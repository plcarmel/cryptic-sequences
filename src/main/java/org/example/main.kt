package org.example

import java.io.FileOutputStream
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.*
import kotlin.math.pow


private const val seed = 0x12c4f99a0f80 // randomly generated

private val rnd = Random(seed)

private val maskRegex = Regex("\\d+")

private val shuffledDigits = (0..9).shuffled(rnd)
private val shuffledDigitPairs = (0..99).shuffled(rnd)

private const val zeroCode = '0'.toByte().toInt()

private fun digitsOf(value: String) =
  value.chars().map { c -> c - zeroCode }.toArray()

private fun digitsToString(value: IntArray) =
  value.map { it + zeroCode }.map { it.toByte() }.toByteArray().let { String(it) }

private fun randomizeDigits(value: String) =
  digitsOf(value).map { shuffledDigits[it] }.toIntArray().let(::digitsToString)

private fun randomizeDigitPairs(value: String): String {
  if (value.length == 1) {
    return value
  }
  val digits = digitsOf(value)
  val n = digits.size
  digits.indices.forEach {
    val x = shuffledDigitPairs[digits[it] * 10 + digits[(it + 1) % n]];
    digits[it] = x / 10
    digits[(it+1)%n] = x % 10
  }
  return digitsToString(digits)
}

fun applyMask(mask: String, value: String): String {
  return value.indices.map { (digitsOf(value)[it] + digitsOf(mask)[it]) % 10 }.joinToString("")
}

fun randomize(mask: String, value: String): String =
  applyMask(mask, value).let(::randomizeDigits).let { acc ->
    (1 .. mask.length * 100).fold(acc) { v,_ -> randomizeDigitPairs(v) }
  }

fun Int.toMask(n: Int) = String.format("%0${n}d", this)

fun listValues(mask: String): List<String> {
  val n = mask.length
  val max = 10.0.pow(n.toDouble()).toInt()
  return (0 until max).map { randomize(mask, it.toMask(n)) }
}

fun main(args: Array<String>) {
  if (args.size != 1)
    throw IllegalArgumentException("Pass the mask in parameter")
  val mask = args[0]
  if (!maskRegex.matches(mask))
    throw IllegalArgumentException("Invalid mask")
  val result = listValues(mask).map(String::toInt).toIntArray()
  val n = mask.length
  val diffResult = result.indices.map { result[(it+1) % result.size] - result[it] }.toIntArray()
  val resultSet = result.toSet()
  result.indices.forEach {
    if (!resultSet.contains(it)) {
      throw RuntimeException("Does not contain $it !")
    }
  }
  //diffResult.forEach(::println)
  FileOutputStream("result.bin").use { file -> result.forEach(file::write) }
}
