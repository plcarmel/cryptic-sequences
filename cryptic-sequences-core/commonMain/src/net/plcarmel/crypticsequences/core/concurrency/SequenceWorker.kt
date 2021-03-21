package net.plcarmel.crypticsequences.core.concurrency

class SequenceWorker<T>(
  iterator: Iterator<T>,
  concurrencyLayer: ConcurrencyLayer,
  consumer: Consumer<T>
): Thread by
  concurrencyLayer.createThread({
    while (iterator.hasNext()) consumer.push(iterator.next())
    consumer.close()
  })
