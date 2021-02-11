package com.example

import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.spi.*
import kotlin.test.Test

class RunJavap {

  private fun Writer.asPrintWriter(): PrintWriter = PrintWriter(this, true)

  @Test
  fun test() {
    val javap = ToolProvider.findFirst("javap").orElseThrow()

    val url = Thread.currentThread().contextClassLoader.getResource("JavacTest.class")
      ?:throw IllegalStateException("JavacTest.class not found")

    val writer = StringWriter()
    val printWriter = writer.asPrintWriter()
    val result = javap.run(printWriter, printWriter, "-p", "-v", url.toExternalForm())

    println("$url -> $result")
    writer.toString().lines().forEachIndexed { index, s -> 
      println("%4d: $s".format(index))
    }
  }
}
