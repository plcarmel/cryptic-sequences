package net.plcarmel.crypticsequences.cli

import net.plcarmel.crypticsequences.core.concurrency.JavaConcurrencyLayer

fun main(args: Array<String>) =
  mainWithAdvancedIo(
    JavaOutputLayer.instance,
    JavaConcurrencyLayer.instance,
    args
  )
