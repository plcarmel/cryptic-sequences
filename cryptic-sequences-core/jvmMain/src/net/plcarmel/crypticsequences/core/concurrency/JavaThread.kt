package net.plcarmel.crypticsequences.core.concurrency

class JavaThread(f: () -> Unit) : Thread {

  private val thread = Thread(f)

  override fun start() = thread.start()

  override fun join() = thread.join()

}
