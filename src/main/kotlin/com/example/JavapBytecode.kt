package com.example

sealed class JavapBytecode {
  abstract val name: String
  abstract val outputs: String

  data class SucceededCode(override val name: String, override val outputs: String): JavapBytecode()
  data class FailedCode(override val name: String, override val outputs: String): JavapBytecode()

  companion object {
    fun success(name: String, contents: String): JavapBytecode = SucceededCode(name, contents)
    fun failed(name: String, contents: String): JavapBytecode = FailedCode(name, contents)
  }
}
