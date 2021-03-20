package net.plcarmel.crypticsequences.cli

import java.io.FileDescriptor
import java.io.FileOutputStream

class JavaOutputSystem private constructor() : OutputSystem {

  companion object {

    val instance = JavaOutputSystem()

  }

  override fun openStdOut(): OutputFile =
    FileDescriptor.out.let(::FileOutputStream).let(::JavaOutputFile)

  override fun open(path: String): OutputFile =
    path.let(::FileOutputStream).let(::JavaOutputFile)

}
