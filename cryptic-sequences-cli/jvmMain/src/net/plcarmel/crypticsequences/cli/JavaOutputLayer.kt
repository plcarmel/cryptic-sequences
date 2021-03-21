package net.plcarmel.crypticsequences.cli

import java.io.BufferedOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream

class JavaOutputLayer private constructor() : OutputLayer {

  companion object {

    val instance = JavaOutputLayer()

  }

  override fun openStdOut(): OutputFile =
    FileDescriptor.out.let(::FileOutputStream).let(::JavaOutputFile)

  override fun open(path: String): OutputFile =
    path.let(::FileOutputStream).let(::BufferedOutputStream).let(::JavaOutputFile)

}
