package org.example

import org.example.encryption.definitions.VariableSizeWordEncryptionAlgo
import org.example.encryption.implementations.ShuffledTableOverlapEncryptionAlgo
import org.example.numbers.BaseSystem.Companion.zeroPad
import org.example.numbers.NumberRepresentationSystem.Companion.base10
import java.lang.IllegalArgumentException
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.math.pow

private val numberRepresentationSystem = base10
private val baseSystem = base10.baseSystem

fun nbValuesFromNbDigits(nbDigits: Int) = baseSystem.base.toDouble().pow(nbDigits).toInt()

fun listValues(nbDigits: Int, algo: VariableSizeWordEncryptionAlgo): Stream<String> {
  val max = nbValuesFromNbDigits(nbDigits)
  return StreamSupport.stream((0 until max).spliterator(), true).map {
    val tagDigits = zeroPad(nbDigits, baseSystem.extractDigits(it))
    algo.encrypt(tagDigits)
    numberRepresentationSystem.format(tagDigits)
  }
}

fun main(args: Array<String>) {
  if (args.size != 2)
    throw IllegalArgumentException("Pass the number of digits and the key in parameter")
  val nbDigits = args[0].toInt()
  val key = args[1].toLong()
  val encryptionAlgo = ShuffledTableOverlapEncryptionAlgo(baseSystem, key)
  val result = listValues(nbDigits, encryptionAlgo)//.collect(Collectors.toList())
  // val diffResult = result.indices.map { result[(it+1) % result.size] - result[it] }.toIntArray()
  /*
  val resultSet = result.toSet()
  result.indices.forEach {
    val x = numberRepresentationSystem.format(zeroPad(nbDigits, baseSystem.extractDigits(it)))
    if (!resultSet.contains(x)) {
      throw RuntimeException("Does not contain \"$x\" ($it) !")
    }
  }*/
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
