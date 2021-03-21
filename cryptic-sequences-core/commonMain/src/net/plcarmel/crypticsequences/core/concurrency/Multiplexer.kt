package net.plcarmel.crypticsequences.core.concurrency

class Multiplexer<T>(private val producers: List<Producer<List<T>>>) : Producer<T> {

  private val n = producers.size

  private val currentLists = producers.indices.map { listOf<T>() }.toTypedArray()
  private val currentIndices = producers.indices.map { 0 }.toTypedArray()

  private var i = 0

  private val producer
    inline get() = producers[i]

  private val list
    inline get() = currentLists[i]

  private var index
    inline get() = currentIndices[i]
    inline set(value) { currentIndices[i] = value }

  private fun nextProducer() {
    if (++i == n) i = 0
  }

  private fun popPack() {
    if (producer.hasNext) {
      currentLists[i] = producer.pop()
      currentIndices[i] = 0
    }
  }

  override fun pop(): T {
    if (index == list.size) {
      popPack()
    }
    val result = list[index++]
    nextProducer()
    return result
  }

  override val hasNext: Boolean
    get() = index != list.size || producer.hasNext

}
