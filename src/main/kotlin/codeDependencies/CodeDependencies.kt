package com.shioya86.rkotlin.codeDependencies

import java.nio.file.Paths

class CodeDependencies {
  // TODO: Wildcard and @fileList support
  fun getDeps(sourceFiles: List<String>): List<String> {
    return sourceFiles
      .map{ file -> Paths.get(file).toAbsolutePath().toString() }
      .sorted()
  }
}