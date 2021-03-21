package net.plcarmel.crypticsequences.core.concurrency

class KotlinNativeFuture<T>(private val future: kotlin.native.concurrent.Future<T>) : Future<T> {

  override val result: T
    get() = future.result

}
