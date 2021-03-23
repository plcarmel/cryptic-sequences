package net.plcarmel.crypticsequences.cli

import net.plcarmel.crypticsequences.core.concurrency.ConcurrencyLayer

class PlatformSpecificLayer(
  val output: OutputLayer,
  val concurrency: ConcurrencyLayer
)
