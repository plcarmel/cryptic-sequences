package net.plcarmel.crypticsequences.core.concurrency

class JavaConcurrencyLayer private constructor() : ConcurrencyLayer {

  companion object {

    val instance = JavaConcurrencyLayer()

  }

  override fun createThread(f: () -> Unit): Thread = JavaThread(f)

  override fun createSemaphore(nbPermits: Int): Semaphore = JavaSemaphore(nbPermits)

  // override fun <T> createPipe(maxSize: Int): Pipe<T, T> = JavaPipe(maxSize)

  override fun sleep(ms: Long) = java.lang.Thread.sleep(ms)
}
