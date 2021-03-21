package net.plcarmel.crypticsequences.core.concurrency

interface Producer<T> {

  fun pop(): T
  val hasNext: Boolean

}
