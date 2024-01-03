package com.shioya86.rkotlin.parameterExtractor

data class ParameterType(
  val tempDir: String,
  val sourceFiles: List<String>,
  val buildOnly: Boolean
)
