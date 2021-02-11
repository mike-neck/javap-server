package com.example

import java.util.*
import javax.tools.Diagnostic
import javax.tools.DiagnosticListener
import javax.tools.JavaFileObject

class JavacDiagnosticListener(
  private val diagnostics: MutableList<CompileError> = mutableListOf())
  : DiagnosticListener<JavaFileObject> {

  fun diagnostics(): List<CompileError> = diagnostics.toList()

  override fun report(diagnostic: Diagnostic<out JavaFileObject>?) {
    if (diagnostic == null) return
    val compileError = CompileError(
      diagnostic.kind.name,
      diagnostic.code,
      diagnostic.lineNumber,
      diagnostic.columnNumber,
      diagnostic.getMessage(Locale.ENGLISH)
    )
    diagnostics += compileError
  }
}