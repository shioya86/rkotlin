package com.shioya86.rkotlin

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.FileTime

fun buildPath(vararg elements: String): String {
  return Paths.get("", *elements).toString()
}

fun anyNewerThan(filenames: List<String>, buildTargetFilename: String): Boolean {
  val lastBuildTime = if (Files.exists(Paths.get(buildTargetFilename)))
      Files.getLastModifiedTime(Paths.get(buildTargetFilename))
    else
      FileTime.fromMillis(0)

  return filenames.any { f ->
    Files.getLastModifiedTime(Paths.get(f)) > lastBuildTime
  }
}