package com.example

import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.StringWriter
import java.util.*
import javax.tools.ToolProvider

class RunJavac {

  @Test
  fun run() {
    val compiler = ToolProvider.getSystemJavaCompiler()
    val javaCode = loadJavaCode()
    val javaObject = StringJavaObject(javaCode, "JavacTest")
    val diagnosticListener = JavacDiagnosticListener()
    val fileManager = DelegateFileManager(compiler.getStandardFileManager(diagnosticListener, Locale.ENGLISH, Charsets.UTF_8))
    val writer = StringWriter()
    val compilationTask = compiler.getTask(writer, fileManager, diagnosticListener, emptyList(), emptyList(), listOf(javaObject))
    if (compilationTask.call()) {
        fileManager.list.forEach { klass ->
          println(klass.name)
          klass.bytes.dump()
        }
        println(writer.toString())
    }
    diagnosticListener.diagnostics().forEach {
      println("[${it.code} ${it.type}]${it.message} line=${it.lineNumber}, col=${it.columnNumber}")
    }
  }

  private fun ByteArrayOutputStream.dump() {
    val bytes = this.toByteArray()
      .withIndex()
      .groupBy({ it.index / 16 }) { it.value }
      .toList()
      .asSequence()
      .map { it.second }
    bytes
      .map { 
        val str = it.joinToString(" ") { b -> "%02x".format(b) }
        val bs = it.joinToString(" ") { b -> String(byteArrayOf(b), Charsets.UTF_8) }
        "$str | $bs"
      }
      .forEach { println(it) }
  }

  companion object {
    private fun loadJavaCode(): String {
      val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("JavacTest.java")
        ?: throw IllegalStateException("JavacTest.java not found")
      return stream.use { it.bufferedReader().readText() }
    }
  }

}
