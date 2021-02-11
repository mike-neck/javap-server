package com.example

import java.net.URI
import javax.tools.FileObject
import javax.tools.ForwardingJavaFileManager
import javax.tools.JavaFileManager
import javax.tools.JavaFileObject

class DelegateFileManager(
  delegate: JavaFileManager,
  val list: MutableList<Klass> = mutableListOf()
): ForwardingJavaFileManager<JavaFileManager>(delegate) {
  override fun getJavaFileForOutput(
    location: JavaFileManager.Location?,
    className: String?,
    kind: JavaFileObject.Kind?,
    sibling: FileObject?
  ): JavaFileObject {
    if (className == null) {
      throw IllegalArgumentException("invalid java file class name")
    }
    val klass = Klass(URI.create("class-file:///$className"))
    list += klass
    return klass
  }
}
