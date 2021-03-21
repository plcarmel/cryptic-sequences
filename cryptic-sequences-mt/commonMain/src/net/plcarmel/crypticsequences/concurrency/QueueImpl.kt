package net.plcarmel.crypticsequences.core.concurrency

class QueueImpl<T>(private val maxSize: Int) : Queue<T> {

  private val lastIndex = maxSize-1

  private val buffer = ArrayList<T?>(maxSize)
  private var pushAt = 0
  private var popAt = 0
  private var isFull = false

  init {
    repeat(maxSize) { buffer.add(null) }
  }

  class QueueFullException : Exception("Queue is full")

  private fun checkIfFull() {
    isFull = pushAt == popAt
  }

  private fun incPushAt() {
    if (++pushAt == maxSize) pushAt = 0
    checkIfFull()
  }

  private fun decPushAt() {
    if (--pushAt == -1) pushAt = lastIndex
    isFull = false
  }

  private fun incPopAt() {
    if (++popAt == maxSize) popAt = 0
    isFull = false
  }

  private fun decPopAt() {
    if (--popAt == -1) popAt = lastIndex
    checkIfFull()
  }

  override val size: Int
    get() {
      if (isEmpty) { return 0; }
      val n = pushAt - popAt
      return if (n > 0) n else n + maxSize
    }

  override val isEmpty: Boolean
    get() = !isFull && popAt == pushAt

  override fun push(x: T) {
    if (isFull) {
      throw QueueFullException()
    }
    buffer[pushAt] = x
    incPushAt()
  }

  override fun pop(): T {
    if (isEmpty) {
      throw NoSuchElementException()
    }
    val result = buffer[popAt]
    incPopAt()
    return result!!
  }

  override fun cancelPush() {
    decPushAt()
  }

  override fun cancelPop() {
    if (isFull) {
      throw QueueFullException()
    }
    decPopAt()
  }

}
