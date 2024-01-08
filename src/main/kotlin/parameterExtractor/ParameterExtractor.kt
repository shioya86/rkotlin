package com.shioya86.rkotlin.parameterExtractor

import kotlinx.cli.*

class ParameterExtractor {
  companion object {
    fun parse(args: Array<String>): ParameterType {
      val parser = ArgParser("rkotlin")

      val defaultDir = System.getenv("TMPDIR")
      val sourceFiles by parser
        .argument(ArgType.String, "sourceFiles", description = "")
        .vararg()

      val tmpDir by parser
        .option(ArgType.String, "tmpdir", description = "")
        .default(defaultDir)

      val buildOnly by parser
        .option(ArgType.Boolean, "build-only", description = "")
        .default(false)

      val force by parser
        .option(ArgType.Boolean, "force", description = "")
        .default(false)

      val arguments by parser
        .option(ArgType.String, "args", description = "")
        .default("")

      parser.parse(args)

      return ParameterType(
        tmpDir,
        sourceFiles,
        buildOnly,
        force,
        arguments
      )
    }
  }
}