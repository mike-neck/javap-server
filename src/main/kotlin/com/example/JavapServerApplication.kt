package com.example

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import javax.tools.JavaCompiler
import javax.tools.ToolProvider
import kotlin.reflect.KClass

@SpringBootApplication
class JavapServerApplication {
  @Bean 
  fun javaCompiler(): JavaCompiler = ToolProvider.getSystemJavaCompiler()

  @Bean 
  fun javacService(javaCompiler: JavaCompiler): JavacService = JavacService(javaCompiler)
}

fun main(args: Array<String>) {
  runApplication<JavapServerApplication>(*args)
}

inline fun <reified T: Any> logger(klass: KClass<T> = T::class): Logger = LoggerFactory.getLogger(klass.java)

