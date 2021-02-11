package com.example

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.URI
import javax.tools.JavaFileObject
import javax.tools.SimpleJavaFileObject

class Klass(
  private val classUri: URI
  ): SimpleJavaFileObject(classUri, JavaFileObject.Kind.CLASS) {
  val bytes: ByteArrayOutputStream = ByteArrayOutputStream()
  override fun getName(): String = classUri.rawSchemeSpecificPart
  override fun openOutputStream(): OutputStream = bytes
}
