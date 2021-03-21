package net.plcarmel.crypticsequences.cli

import net.plcarmel.crypticsequences.core.concurrency.KotlinNativeConcurrencyLayer

fun main(args: Array<String>) =
  // mainWithBasicIo(args)
  mainWithAdvancedIo(PosixOutputLayer.instance, KotlinNativeConcurrencyLayer.instance, args)

