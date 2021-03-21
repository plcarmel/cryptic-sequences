package net.plcarmel.crypticsequences.core.concurrency

class JavaSemaphore(nbPermits: Int) : Semaphore {

  private val semaphore = java.util.concurrent.Semaphore(nbPermits)

  override fun acquire() = semaphore.acquire()

  override fun release() { semaphore.release() }
}
