package net.plcarmel.crypticsequences.core.concurrency

class JvmConcurrencyLayer private constructor() : ConcurrencyLayer {

  companion object {
    val instance = JvmConcurrencyLayer()
  }

  override fun createWorker(): Worker =
    JvmWorker()

  override fun <T> futureOf(x: T): Future<T> =
    JvmFuture(java.util.concurrent.CompletableFuture.completedFuture(x))

}
