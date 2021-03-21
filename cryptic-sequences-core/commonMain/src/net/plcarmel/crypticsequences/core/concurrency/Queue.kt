package net.plcarmel.crypticsequences.core.concurrency

interface Queue<T> {

  fun push(x: T)
  fun pop(): T
  val size: Int

  fun cancelPush()
  fun cancelPop(x: T)

}
