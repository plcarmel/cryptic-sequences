package net.plcarmel.crypticsequences.core.concurrency

class JvmFuture<T>(private val jvmFuture: java.util.concurrent.Future<T>) : Future<T> {

  override val result: T
    get() = jvmFuture.get()

}
