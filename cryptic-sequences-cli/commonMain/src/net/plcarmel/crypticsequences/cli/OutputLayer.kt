package net.plcarmel.crypticsequences.cli

interface OutputLayer {

  fun openStdOut(): OutputFile
  fun open(path: String): OutputFile

}
