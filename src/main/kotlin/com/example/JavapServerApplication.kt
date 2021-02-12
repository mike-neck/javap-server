package com.example

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.tools.JavaCompiler
import javax.tools.ToolProvider
import kotlin.reflect.KClass

@SpringBootApplication
class JavapServerApplication {

  private val logger = logger<JavapServerApplication>()

  @Bean 
  fun javaCompiler(): JavaCompiler = ToolProvider.getSystemJavaCompiler()

  @Bean 
  fun javacService(javaCompiler: JavaCompiler): JavacService = JavacService(javaCompiler)

  @Bean
  fun webMvcConfigurer(@Value("\${javap.allowed.origin}") javapAllowedOrigin: String): WebMvcConfigurer = object : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
      logger.info("configuration for webMvcConfigurer allowed-origins=${javapAllowedOrigin}")
      registry.addMapping("/javap/*")
        .allowedOrigins(javapAllowedOrigin)
        .allowedMethods("GET", "HEAD", "POST")
    }
  }
}

fun main(args: Array<String>) {
  runApplication<JavapServerApplication>(*args)
}

inline fun <reified T: Any> logger(klass: KClass<T> = T::class): Logger = LoggerFactory.getLogger(klass.java)
