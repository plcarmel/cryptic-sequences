package net.plcarmel.crypticsequences.cli

import net.plcarmel.crypticsequences.core.concurrency.JvmConcurrencyLayer

val javaLayer = PlatformSpecificLayer(JavaOutputLayer.instance, JvmConcurrencyLayer.instance)

fun main(args: Array<String>) = mainWithAdvancedIo(javaLayer, args, ::OptionsWithAdvancedIo)
