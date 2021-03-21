package net.plcarmel.crypticsequences.core.concurrency

class PipeImpl<T>(concurrencyLayer: ConcurrencyLayer, maxSize: Int) : Pipe<T, T> {

  private val queue = concurrencyLayer.queue<T>(maxSize)
  private var isClosed: Boolean = false

  private val supplySideSemaphore = concurrencyLayer.createSemaphore(maxSize)
  private val demandSideSemaphore = concurrencyLayer.createSemaphore(0)

  override fun push(x: T) {
    supplySideSemaphore.acquire()
    try {
      queue.push(x)
      try {
        demandSideSemaphore.release()
      } catch (e: Throwable) {
        queue.cancelPush()
        throw e
      }
    } catch (e: Throwable) {
      supplySideSemaphore.release()
      throw e
    }
  }

  override fun pop(): T {
    demandSideSemaphore.acquire()
    try {
      val x = queue.pop()
      try {
        supplySideSemaphore.release()
        return x
      } catch (e: Exception) {
        queue.cancelPop()
        throw e
      }
    } catch (e: Exception) {
      demandSideSemaphore.release()
      throw e
    }
  }

  override val hasNext: Boolean
    get() = !queue.isEmpty || !isClosed

  override fun close() {
    isClosed = true
  }

}
