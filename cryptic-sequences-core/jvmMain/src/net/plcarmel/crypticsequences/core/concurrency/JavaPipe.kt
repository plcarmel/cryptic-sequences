package net.plcarmel.crypticsequences.core.concurrency

class JavaPipe<T>(maxSize: Int) : Pipe<T, T> {

  private val blockingQueue = java.util.concurrent.ArrayBlockingQueue<T>(maxSize)

  private var isClosed = false

  override fun push(x: T) {
    blockingQueue.add(x)
  }

  override fun close() {
    isClosed = true
  }

  override fun pop(): T {
    return blockingQueue.take()
  }

  override val hasNext: Boolean
    get() = !isClosed || !blockingQueue.isEmpty()

}
