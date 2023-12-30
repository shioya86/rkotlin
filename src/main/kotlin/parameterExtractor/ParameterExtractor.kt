package com.shioya86.rkotlin.parameterExtractor

import kotlinx.cli.*

class ParameterExtractor {
  companion object {
    fun parse(args: Array<String>): ParameterType {
      val parser = ArgParser("rkotlin")

      val defaultDir = System.getenv("TMPDIR")
      val tmpDir by parser
        .option(ArgType.String, "tmpdir", description = "")
        .default(defaultDir)

      parser.parse(args)
      return ParameterType(tmpDir)
    }
  }
}