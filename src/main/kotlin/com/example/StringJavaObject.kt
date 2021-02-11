package com.example

import java.net.URI
import javax.tools.JavaFileObject
import javax.tools.SimpleJavaFileObject

class StringJavaObject(
  private val javaCode: String, 
  javaName: String): 
  SimpleJavaFileObject(
    URI.create("string:///${javaName}.java"), JavaFileObject.Kind.SOURCE
) {
  override fun getCharContent(ignoreEncodingErrors: Boolean): CharSequence = javaCode
}