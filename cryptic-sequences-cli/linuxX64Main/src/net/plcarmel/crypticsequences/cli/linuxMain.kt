@file:OptIn(ExperimentalCli::class, ExperimentalUnsignedTypes::class)

package net.plcarmel.crypticsequences.cli

import kotlinx.cli.ExperimentalCli

fun main(args: Array<String>) =
  mainWithBasicIo(args)
  //mainWithAdvancedIo(PosixOutputLayer.instance, args)

