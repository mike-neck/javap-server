package com.example

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.HandlerMethod

@RestController
@RequestMapping("/javap")
class JavapController(
  private val deflaterService: DeflaterService,
  private val javacService: JavacService,
  private val fileService: FileService,
  private val javapService: JavapService,
) {

  private val logger = logger<JavapController>()

  @GetMapping(path = ["{base64}"], produces = ["application/json"])
  fun javap(@PathVariable("base64") base64: String): ResponseEntity<Map<String, List<JavapBytecode>>> {
    val javaCode = deflaterService.inflateToJavaCode(base64)
      ?: throw JavapFailureException(
        "invalid byte sequence or invalid base64 string",
        "incompatible gzip format",
        "inflate base64 code",
        HttpStatus.BAD_REQUEST,
      )

    val (compileErrors, bytecodes) = javacService.compileToBytes(javaCode)
    if (bytecodes.isEmpty()) {
      throw JavapFailureException(
        "failed to compile",
        compileErrors.joinToString(",\n") { "${it.type} ${it.message} at line=${it.lineNumber} col=${it.columnNumber}" },
        "javac code",
        HttpStatus.BAD_REQUEST,
      )
    }

    val paths = fileService.createTemporaryFiles(bytecodes)
    if (paths.isEmpty()) {
      throw JavapFailureException(
        "failed to write bytecode of $bytecodes",
        "server error",
        "create class file",
        HttpStatus.INTERNAL_SERVER_ERROR,
      )
    }

    val (success, contents) = javapService.runJavap(paths)
    return when (success) {
      true -> ResponseEntity.ok().body(
        mapOf(
          "contents" to contents,
        )
      )
      false -> {
        throw JavapFailureException(
          "failed to execute javap",
          "failed classes are ${contents.joinToString(",") { it.name }}",
          "javap classes",
          HttpStatus.INTERNAL_SERVER_ERROR
        )
      }
    }
  }

  @ExceptionHandler(JavapFailureException::class)
  fun handleException(exception: JavapFailureException, handler: HandlerMethod): ResponseEntity<Map<String, String>> {
    logger.info(
      "error at {}[path={}]",
      exception.process,
      handler.methodParameters.getOrNull(0),
      exception
    )
    return exception.toResponseEntity()
  }
}
