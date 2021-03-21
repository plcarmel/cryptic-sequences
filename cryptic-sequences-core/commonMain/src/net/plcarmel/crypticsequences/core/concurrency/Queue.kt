package net.plcarmel.crypticsequences.core.concurrency

interface Queue<T> {

  fun push(x: T)
  fun pop(): T
  val size: Int

  val isEmpty
    get() = size == 0

  fun cancelPush()
  fun cancelPop()

}
