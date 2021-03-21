package net.plcarmel.crypticsequences.core.concurrency

interface Pipe<TOut, TIn> : Consumer<TOut>, Producer<TIn>
