package net.plcarmel.crypticsequences.core.concurrency

class SequenceProducer<T>(iterator: Iterator<T>, concurrencyLayer: ConcurrencyLayer, bufferSize: Int)
  : Producer<List<T>>, Thread
{
  private val buffer = concurrencyLayer.createPipe<List<T>>(maxSize = bufferSize)
  private val worker = SequenceWorker(iterator, concurrencyLayer, BufferedConsumer(buffer))

  override fun pop(): List<T> = buffer.pop()

  override val hasNext: Boolean
    get() = buffer.hasNext

  override fun start() = worker.start()

  override fun join() = worker.join()
}
