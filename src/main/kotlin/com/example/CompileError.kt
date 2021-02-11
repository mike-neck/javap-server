package com.example

data class CompileError(
  val type: String,
  val code: String,
  val lineNumber: Long,
  val columnNumber: Long,
  val message: String
)