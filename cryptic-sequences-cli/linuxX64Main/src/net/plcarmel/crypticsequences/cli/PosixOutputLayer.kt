package net.plcarmel.crypticsequences.cli

import platform.posix.fopen
import platform.posix.stdout

class PosixOutputLayer private constructor() : OutputLayer {

  companion object {
    val instance = PosixOutputLayer()
  }

  override fun openStdOut(): OutputFile =
    PosixOutputFile(file = stdout ?: throw RuntimeException("Failed to access stdout"))

  override fun open(path: String): OutputFile =
    PosixOutputFile(file = fopen(path, "w") ?: throw RuntimeException("Failed to open file \"${path}\""))

}
