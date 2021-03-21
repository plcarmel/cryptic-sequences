package net.plcarmel.crypticsequences.core.concurrency

class FutureOf<T>(override val result: T) : Future<T>
