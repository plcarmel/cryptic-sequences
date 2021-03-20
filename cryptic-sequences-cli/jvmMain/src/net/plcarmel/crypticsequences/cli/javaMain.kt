package net.plcarmel.crypticsequences.cli

@OptIn(ExperimentalUnsignedTypes::class)
fun main(args: Array<String>) {
  mainWithAdvancedIo(JavaOutputSystem.instance, args)
}
