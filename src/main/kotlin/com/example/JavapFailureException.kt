package com.example

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

class JavapFailureException(
  message: String,
  private val detail: String,
  val process: String,
  private val httpStatus: HttpStatus,
): Exception(message) {
  override val message: String get() = "${super.message}"

  fun toResponseEntity(): ResponseEntity<Map<String, String>> =
    ResponseEntity.status(httpStatus)
      .contentType(MediaType.APPLICATION_JSON)
      .body(mapOf(
        "error" to message,
        "cause" to detail,
      ))
}
