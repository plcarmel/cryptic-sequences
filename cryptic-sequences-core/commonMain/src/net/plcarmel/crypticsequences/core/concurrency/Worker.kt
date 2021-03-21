package net.plcarmel.crypticsequences.core.concurrency

interface Worker {

  fun <T> execute(body: () -> T): Future<T>

  fun shutdown()

}
