package net.plcarmel.crypticsequences.cli

import net.plcarmel.crypticsequences.core.concurrency.JvmConcurrencyLayer

fun main(args: Array<String>) =
  mainWithAdvancedIo(
    JavaOutputLayer.instance,
    JvmConcurrencyLayer.instance,
    args
  )
