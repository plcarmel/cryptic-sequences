package net.plcarmel.crypticsequences.core.concurrency

class BufferedConsumer<T>(
  private val output: Consumer<List<T>>,
  private val packSize: Int = 1 shl 16
) : Consumer<T> {

  var pack = ArrayList<T>(packSize)

  private fun pushPack() {
    output.push(pack)
    pack = ArrayList(packSize)
  }

  override fun push(x: T) {
    pack.add(x)
    if (pack.size == packSize) {
      pushPack()
    }
  }

  override fun close() {
    pushPack()
    output.close()
  }

}
