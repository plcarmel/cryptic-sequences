package net.plcarmel.crypticsequences.core.concurrency

import kotlin.native.concurrent.freeze

class KotlinNativeConcurrencyLayer private constructor() : ConcurrencyLayer {

  companion object {
    val instance = KotlinNativeConcurrencyLayer()
  }

  override fun createWorker(): Worker = KotlinNativeWorker()

  override fun <T> futureOf(x: T): Future<T> = FutureOf(x)

  override fun <T> freeze(x: T): T = x.freeze()

}
