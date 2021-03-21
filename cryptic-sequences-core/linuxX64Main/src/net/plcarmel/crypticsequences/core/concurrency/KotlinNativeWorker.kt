package net.plcarmel.crypticsequences.core.concurrency

import kotlin.native.concurrent.TransferMode

class KotlinNativeWorker : Worker {

  private val worker = kotlin.native.concurrent.Worker.start()

  override fun <T> execute(body: () -> T): Future<T> =
    worker.execute(TransferMode.UNSAFE, { body }) { it() }.let(::KotlinNativeFuture)

  override fun shutdown() {
    worker.requestTermination(false)
  }

}
