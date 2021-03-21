package net.plcarmel.crypticsequences.core.concurrency

interface ConcurrencyLayer {

  fun createWorker(): Worker

  fun <T> futureOf(x: T): Future<T>

  fun <T> freeze(x: T): T = x

}
