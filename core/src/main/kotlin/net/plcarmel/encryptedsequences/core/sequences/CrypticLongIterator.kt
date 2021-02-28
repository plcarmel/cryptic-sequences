@file:Suppress("MemberVisibilityCanBePrivate")

package net.plcarmel.encryptedsequences.core.sequences

class CrypticLongIterator(
  val baseIterator: CrypticIterator
): Iterator<Long> {

  val baseSystem = baseIterator.encryptionAlgo.baseSystem

  override fun hasNext(): Boolean = baseIterator.hasNext()

  override fun next(): Long = baseSystem.combineDigitsFrom(baseIterator.next())

}

