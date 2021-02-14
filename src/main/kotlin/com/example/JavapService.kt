package com.example

import org.springframework.stereotype.Component
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.nio.file.Path
import java.util.spi.*

@Component
class JavapService {

  private val logger = logger<JavapService>()

  private val javap get() = ToolProvider.findFirst("javap").orElseThrow { IllegalStateException("javap not available") }

  fun runJavap(javaFiles: Iterable<Path>, options: (Path) -> List<String> = { listOf("-v", "-p", it.toString()) }): Pair<Boolean, List<JavapBytecode>> {
    val results = javaFiles.map { runJavap(it, options(it)) }
      .partition { it is JavapBytecode.SucceededCode }
    return if (results.second.isEmpty()) {
      true to results.first
    } else {
      false to results.second
    }
  }

  @Suppress("ThrowableNotThrown")
  fun runJavap(javaFile: Path, options: List<String> = listOf("-v", "-p", javaFile.toString())): JavapBytecode {
    val out = StringWriter()
    val stdout = out.asPrintWriter()
    val err = StringWriter()
    val stderr = err.asPrintWriter()
    val fileName = javaFile.fileName.toString()

    return when(javap.run(stdout, stderr, *options.toTypedArray())) {
      0 -> {
        logger.info("javap for {} is succeeded", javaFile)
        val javapcode = out.toString()
          .lineSequence()
          .map {
            if (it.contains(javaFile.toString())) {
              it.replace(
                javaFile.toString(),
                fileName
              )
            } else it
          }
          .joinToString("\n")
        JavapBytecode.success(fileName, javapcode)
      }
      else -> {
        logger.info("javap for {} is failed due to stdout={}, stderr={}", javaFile, out.toString(), err.toString())
        JavapBytecode.failed(fileName, out.toString())
      }
    }
  }

  private fun Writer.asPrintWriter(): PrintWriter = PrintWriter(this)
}
