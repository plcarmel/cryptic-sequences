
package net.plcarmel.crypticsequences.cli

fun Long.shl64(n: Int): Long {
  var r = this
  val a = n and 0b111111
  r = r shl a
  val b = (a shr 6) and 0b1
  r = r shl b
  return r
}

fun Long.shr64(n: Int): Long {
  var r = this
  val a = n and 0b111111
  r = r shr a
  val b = (a shr 6) and 0b1
  r = r shr b
  return r
}
