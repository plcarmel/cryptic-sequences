package net.plcarmel.crypticsequences.cli

import net.plcarmel.crypticsequences.core.concurrency.KotlinNativeConcurrencyLayer

val linux64Layer = PlatformSpecificLayer(PosixOutputLayer.instance, KotlinNativeConcurrencyLayer.instance)

fun main(args: Array<String>) =
  mainWithAdvancedIo(linux64Layer, args) { OptionsWithTestU01(args, it) }
