package net.plcarmel.crypticsequences.core.concurrency

class ConcurrentQueueImpl<T>(concurrencyLayer: ConcurrencyLayer) : Queue<T> {

  private val buffer = mutableListOf<T>()
  private val bufferLock = concurrencyLayer.createSemaphore(1)

  override val size: Int
    get() = buffer.size

  override fun push(x: T) {
    bufferLock.acquire()
    try { buffer.add(0, x) }
    finally { bufferLock.release() }
  }

  override fun pop(): T {
    bufferLock.acquire()
    try { return buffer.removeAt(buffer.size - 1) }
    finally { bufferLock.release() }
  }

  override fun cancelPush() {
    buffer.removeAt(0)
  }

  override fun cancelPop(x: T) {
    buffer.add(x)
  }

}
