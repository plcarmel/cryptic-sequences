package net.plcarmel.crypticsequences.core.concurrency

interface ConcurrencyLayer {

  fun createThread(f: () -> Unit): Thread
  fun createSemaphore(nbPermits: Int): Semaphore

  fun <T> queue(maxSize: Int): Queue<T> = QueueImpl(maxSize)
  fun <T> createPipe(maxSize: Int): Pipe<T, T> = PipeImpl(this, maxSize)

  fun sleep(ms: Long)
}
