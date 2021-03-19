package net.plcarmel.crypticsequences.core.encryption.implementations

import net.plcarmel.crypticsequences.core.numbers.BaseSystem
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.random.Random

fun computeDefaultTableDimensions(baseSystem: BaseSystem): Int =
  (ln(256.0) / ln(baseSystem.base.toDouble())).roundToInt().coerceAtLeast(2)

fun createTableFromCycle(cycle: List<Int>): IntArray {
  val n = cycle.size
  val table = IntArray(n)
  for (i in 0 until n-1) {
    table[cycle[i]] = cycle[i+1]
  }
  if (n != 0) {
    table[cycle[n-1]] = cycle[0]
  }
  return table
}

fun createTable(n: Int, prng: Random): IntArray =
  createTableFromCycle((0 until n).shuffled(prng)) // Fisher-Yates shuffle

fun shuffledCycleEncryptionAlgo(
  baseSystem: BaseSystem,
  prng: Random,
  wordSize: Int = computeDefaultTableDimensions(baseSystem)
) = TableEncryptionAlgo(wordSize, baseSystem, createTable(baseSystem.nbValues(wordSize).toInt(), prng))
