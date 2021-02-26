package org.example

import java.lang.IllegalArgumentException
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.math.pow

private val baseSystem = BaseSystem(10)

private const val seed = 2L

private val maskRegex = Regex("\\d+")

fun randomize(algoA: ShufflingEncryptionAlgo, algoB: CesarEncryptionAlgo, tag: IntArray) {
  algoB.encrypt(tag)
  algoA.encrypt(tag)
  algoB.encrypt(tag)
}

fun zeroPad(n: Int, tag: IntArray): IntArray =
  tag + (1 .. n - tag.size).map { 0 }

fun nbValuesFromNbDigits(nbDigits: Int) = baseSystem.base.toDouble().pow(nbDigits).toInt()

fun listValues(algoA: ShufflingEncryptionAlgo, algoB: CesarEncryptionAlgo): Stream<String> {
  val n = algoB.mask.size
  val max = nbValuesFromNbDigits(n)
  return StreamSupport.stream((0 until max).spliterator(), false).map {
    val tagDigits = zeroPad(n, baseSystem.extractDigits(it))
    randomize(algoA, algoB, tagDigits)
    baseSystem.fromDigits(tagDigits)
  }
}

fun main(args: Array<String>) {
  if (args.size != 1)
    throw IllegalArgumentException("Pass the mask in parameter")
  val mask = args[0]
  if (!maskRegex.matches(mask))
    throw IllegalArgumentException("Invalid mask")
  val smallCryptor = ShufflingEncryptionAlgo(baseSystem, RandomArrayEncryptionAlgo(baseSystem, wordSize=2, seed))
  val cesarCryptor = CesarEncryptionAlgo(baseSystem.base, baseSystem.toDigits(mask))
  val result = listValues(smallCryptor, cesarCryptor).collect(Collectors.toList())
  val n = mask.length
  // val diffResult = result.indices.map { result[(it+1) % result.size] - result[it] }.toIntArray()
  val resultSet = result.toSet()
  result.indices.forEach {
    val x = baseSystem.fromDigits(zeroPad(n, baseSystem.extractDigits(it)))
    if (!resultSet.contains(x)) {
      throw RuntimeException("Does not contain \"$x\" ($it) !")
    }
  }
  //diffResult.forEach(::println)
  result.forEach(::println)
  /*
  FileOutputStream("result.bin").use { file ->
    result
      .flatMap { x -> listOf(((x and 0xFF00) shr 8), x and 0x00FF).stream() }
      .map(Int::toByte)
      .forEach { file.write(arrayOf(it).toByteArray()) }
  }
   */
}
