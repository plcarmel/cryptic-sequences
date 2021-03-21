package net.plcarmel.crypticsequences.core.concurrency

interface Semaphore {

  fun acquire()
  fun release()

}
