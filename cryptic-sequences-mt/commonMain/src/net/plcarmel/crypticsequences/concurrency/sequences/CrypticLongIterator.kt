@file:Suppress("MemberVisibilityCanBePrivate")

package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.numbers.BaseSystem

class CrypticLongIterator(
  val baseIterator: Iterator<ByteArray>,
  val baseSystem: BaseSystem
): Iterator<Long> {

  override fun hasNext(): Boolean = baseIterator.hasNext()

  override fun next(): Long = baseSystem.combineDigitsFrom(baseIterator.next())

}

