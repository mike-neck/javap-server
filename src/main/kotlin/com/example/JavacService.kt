package com.example

import java.io.StringWriter
import java.util.*
import javax.tools.JavaCompiler
import javax.tools.JavaFileObject

class JavacService(
  private val javaCompiler: JavaCompiler,
) {

  private val logger = logger<JavacService>()

  fun compileToBytes(javaObject: JavaFileObject): Pair<List<CompileError>, Pair<String, ByteArray>?> {
    val diagnosticListener = JavacDiagnosticListener()
    val standardFileManager = javaCompiler.getStandardFileManager(diagnosticListener, Locale.ENGLISH, Charsets.UTF_8)
    val fileManager = DelegateFileManager(standardFileManager)
    val writer = StringWriter()
    val task = javaCompiler.getTask(writer, fileManager, diagnosticListener, emptyList(), emptyList(), listOf(javaObject))
    return when (task.call()) {
      true -> {
        val klass = fileManager.list.firstOrNull()
        logger.info("compiling ${javaObject.name} is succeeded, generated ${klass?.name}")
        when (klass) {
          null -> throw IllegalStateException("unknown states: compiling ${javaObject.name} is succeeded but no bytecode is generated")
          else -> return emptyList<CompileError>() to (klass.name.replace("/", "") to klass.bytes.toByteArray())
        }
      }
      false -> {
        val diagnostics = diagnosticListener.diagnostics()
        logger.info("compiling ${javaObject.name} is failed, cause {}", diagnostics)
        diagnostics to null
      }
    }
  }
}
