package net.plcarmel.crypticsequences.core.concurrency

interface Consumer<T> {

  fun push(x: T)
  fun close()

}
