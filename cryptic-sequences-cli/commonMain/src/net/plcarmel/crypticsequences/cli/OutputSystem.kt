package net.plcarmel.crypticsequences.cli

interface OutputSystem {

  fun openStdOut(): OutputFile
  fun open(path: String): OutputFile

}
