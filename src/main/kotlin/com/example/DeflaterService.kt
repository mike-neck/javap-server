package com.example

import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.util.*
import java.util.zip.*

@Component
class DeflaterService {

  private val logger: Logger = logger<DeflaterService>()

  fun inflateToJavaCode(base64: String): StringJavaObject? {
    return try {
      val replacedBase64 = base64.replace('+', '-').replace('/', '_')
      val bytes = Base64.getUrlDecoder().decode(replacedBase64)
      val decompressed = bytes.inflate()
      val javaCode = decompressed.toString(Charsets.UTF_8)
      val javaName = javaCode.lines()
        .asSequence()
        .filter { it.contains("class ") }
        .map { it.indexOf("class ") to it }
        .map { it.second.substring(it.first + "class ".length) }
        .map { when (val index = it.indexOf(' ')) {
          -1 -> it
          else -> it.substring(0, index)
        } }
        .map { when (it.contains('{')) {
          true -> it.substring(0, it.indexOf('{'))
          false -> it
        } }
        .firstOrNull() ?: throw IllegalArgumentException("javaCode has no class\n${javaCode}")

      StringJavaObject(javaCode, javaName)

    } catch (e: Exception) {
      logger.info("error while inflating user code", e)

      null
    }
  }

  private fun ByteArray.inflate(): ByteArray {
    return GZIPInputStream(ByteArrayInputStream(this)).use { 
      it.readAllBytes()
    }
  }
}
