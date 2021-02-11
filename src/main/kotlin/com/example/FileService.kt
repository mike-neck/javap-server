package com.example

import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Component
class FileService {

  private val logger = logger<FileService>()

  fun createTemporaryFile(fileContents: Pair<String, ByteArray>): Path? {
    return try {
      val javaFile = Files.createTempFile(fileContents.first, ".class")
      Files.write(javaFile, fileContents.second)
    } catch (e: IOException) {
      logger.info("createTemporaryFile for ${fileContents.first} is failed due to {}", e, e)
      null
    }
  }
}
