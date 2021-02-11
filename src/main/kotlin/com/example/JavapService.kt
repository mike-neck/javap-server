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

  @Suppress("ThrowableNotThrown")
  fun runJavap(javaFile: Path, options: List<String> = listOf("-v", "-p", javaFile.toString())): Pair<Boolean, String> {
    val javap = ToolProvider.findFirst("javap").orElseThrow { IllegalStateException("javap not available") }
    val out = StringWriter()
    val stdout = out.asPrintWriter()
    val err = StringWriter()
    val stderr = err.asPrintWriter()

    return when(javap.run(stdout, stderr, *options.toTypedArray())) {
      0 -> {
        logger.info("javap for {} is succeeded", javaFile)
        true to out.toString()
      }
      else -> {
        logger.info("javap for {} is failed due to stdout={}, stderr={}", javaFile, out.toString(), err.toString())
        false to err.toString()
      }
    }
  }

  private fun Writer.asPrintWriter(): PrintWriter = java.io.PrintWriter(this)
}
