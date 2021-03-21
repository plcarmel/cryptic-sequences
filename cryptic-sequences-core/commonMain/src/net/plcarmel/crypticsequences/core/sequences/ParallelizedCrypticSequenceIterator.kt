package net.plcarmel.crypticsequences.core.sequences

import net.plcarmel.crypticsequences.core.concurrency.*
import net.plcarmel.crypticsequences.core.encryption.definitions.NumberBasedEncryptionAlgo

class ParallelizedCrypticSequenceIterator(
  encryptionAlgo: NumberBasedEncryptionAlgo,
  startAt: Long = 0,
  count: Long = encryptionAlgo.baseSystem.nbValues(encryptionAlgo.wordSize),
  concurrencyLayer: ConcurrencyLayer,
  nbThreads: Long = 1,
  bufferSize: Int = 2
) : Iterator<ByteArray>

  by

    (0 until nbThreads)
      .map({
        SequenceProducer(
          CrypticIterator(encryptionAlgo, startAt + it, jump = nbThreads, bound = startAt + count),
          concurrencyLayer,
          bufferSize
        ).apply(Thread::start)
      })
      .toList()
      .let(::Multiplexer)
      .let(::IteratorFromProducer)
