@file:OptIn(ExperimentalCli::class, ExperimentalUnsignedTypes::class)
package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ExperimentalCli
import net.plcarmel.crypticsequences.core.concurrency.JavaConcurrencyLayer

fun main(args: Array<String>) =
  mainWithAdvancedIo(
    JavaOutputLayer.instance,
    JavaConcurrencyLayer.instance,
    args
  )
