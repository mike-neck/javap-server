package com.example

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ZipBase64Test {

  @Test
  fun deflate() {
    val fileName = "JavacTest.java"
    val javaCode = loadText(fileName)
    val deflater = Deflater(Deflater.DEFAULT_COMPRESSION)
    val byteArray = javaCode.toByteArray()
    deflater.setInput(byteArray)
    deflater.finish()
    val buffer = deflater.deflateToBuffer()
    deflater.end()

    val compressed = buffer.asByteBuffer().array()
    val base64 = Base64.getUrlEncoder().encodeToString(compressed)
    println(base64)
    println("before ${byteArray.size} -> mid ${compressed.size} -> after ${base64.toByteArray().size}")
  }

  @Test
  fun exp() {
    val javaCode = loadText("Exp.java")
    val byteArrayOutputStream = ByteArrayOutputStream()
    GZIPOutputStream(byteArrayOutputStream).use { stream ->
      stream.write(javaCode.toByteArray())
    }

    val base64 = Base64.getUrlEncoder().encodeToString(byteArrayOutputStream.toByteArray())

    println(base64)

    val deflater = Deflater(Deflater.DEFAULT_COMPRESSION, true)
    val byteArray = javaCode.toByteArray()
    deflater.setInput(byteArray)
    deflater.finish()
    val buffer = deflater.deflateToBuffer()
    deflater.end()

    val compressed = buffer.asByteBuffer().array()
    val anotherBase64 = Base64.getUrlEncoder().encodeToString(compressed)
    println(anotherBase64)
    println("before ${byteArray.size} -> mid ${compressed.size} -> after ${anotherBase64.toByteArray().size}")
  }

  private fun loadText(fileName: String): String {
    val stream = Thread.currentThread()
      .contextClassLoader
      .getResourceAsStream(fileName)
      ?: throw IllegalStateException("$fileName not found")
    return stream.use { it.bufferedReader().readText() }
  }

  private tailrec fun Deflater.deflateToBuffer(buffer: DataBuffer = DefaultDataBufferFactory(false).allocateBuffer(128)): DataBuffer {
    val array = ByteArray(128)
    val size = this.deflate(array)
    println(size)
    return if (size == 0) buffer
    else deflateToBuffer(buffer.write(array, 0, size))
  }

  @Test
  fun inflate() {
    val compressed = loadText("compressed.txt")
    val bytes = Base64.getUrlDecoder()
      .decode(
        compressed
          .replace('/','_')
          .replace('+', '-')
          .toByteArray())
    val buffer = GZIPInputStream(ByteArrayInputStream(bytes)).use {
      DefaultDataBufferFactory().wrap(it.readAllBytes()) 
    }
    val javaCode = buffer.toString(Charsets.UTF_8)
    println(javaCode)
  }

  @Test
  fun inflateToJavaCode() {
    val service = DeflaterService()
    val compressed = loadText("compressed.txt")

    val javaObject = service.inflateToJavaCode(compressed)

    assertEquals("/Exp.java", javaObject?.name)
  }
}
