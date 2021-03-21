package net.plcarmel.crypticsequences.core.concurrency

import java.util.concurrent.Callable
import java.util.concurrent.Executors.newSingleThreadExecutor

class JvmWorker : Worker {

  private val executorService = newSingleThreadExecutor()

  override fun <T> execute(body: () -> T): Future<T> {
    return JvmFuture(executorService.submit(Callable { body() }))
  }

  override fun shutdown() {
    executorService.shutdown()
  }


}
