package net.plcarmel.crypticsequences.core.concurrency

class IteratorFromProducer<T>(private val producer: Producer<T>) : Iterator<T> {

  override fun hasNext(): Boolean = producer.hasNext

  override fun next(): T = producer.pop()

}
