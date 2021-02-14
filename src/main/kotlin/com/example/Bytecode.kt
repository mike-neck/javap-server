package com.example

class Bytecode(
  val name: String,
  val bytecode: ByteArray,
) {
  constructor(klass: Klass): this(klass.name.replace("/", ""), klass.bytes.toByteArray())

  override fun toString(): String = "bytecode[$name]"
}
