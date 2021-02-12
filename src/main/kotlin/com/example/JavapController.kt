package com.example

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/javap")
class JavapController(
  private val deflaterService: DeflaterService,
  private val javacService: JavacService,
  private val fileService: FileService,
  private val javapService: JavapService,
) {

  @GetMapping(path = ["{base64}"], produces = ["application/json"])
  fun javap(@PathVariable("base64") base64: String): ResponseEntity<Map<String, String>> {
    val javaCode = deflaterService.inflateToJavaCode(base64)
      ?: return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .body(mapOf(
          "error" to "invalid byte sequence or invalid base64 string",
          "cause" to "incompatible gzip format",
        ))

    val (compileErrors, byteCode) = javacService.compileToBytes(javaCode)
    if (byteCode == null) {
      return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .body(mapOf(
          "error" to "failed to compile",
          "cause" to compileErrors.joinToString(",\n") { "${it.type} ${it.message} at line=${it.lineNumber} col=${it.columnNumber}" },
        ))
    }

    val path = fileService.createTemporaryFile(byteCode) 
      ?: return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .body(mapOf(
          "error" to "failed to write bytecode of ${byteCode.first}",
          "cause" to "server error",
        ))

    val (success, contents) = javapService.runJavap(path)
    return when(success) {
      true -> ResponseEntity.ok().body(mapOf(
        "result" to "success",
        "contents" to contents,
      ))
      false -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .body(mapOf(
          "error" to "failed to execute javap",
          "cause" to contents,
        ))
    }
  }
}