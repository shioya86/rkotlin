package com.shioya86.rkotlin

import com.shioya86.rkotlin.codeDependencies.CodeDependencies
import com.shioya86.rkotlin.parameterExtractor.ParameterExtractor
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest

private val userName: String
  get() {
    return System.getProperty("user.name")
  }

private var userTempDir: String = ""

private val myOwnTmpDir: String
  get() {
    var tmpRoot = if (userTempDir != "") userTempDir
      else System.getProperty("java.io.tmpdir")
    tmpRoot = Paths.get(tmpRoot, ".rkotlin-${userName}").toString()

    Files.createDirectories(Paths.get(tmpRoot))

    return tmpRoot
  }

private val kotlinVersion: List<String>
  get() {
    return getVersions(listOf("kotlin", "-version"))
  }

private val javaVersion: List<String>
  get() {
    return getVersions(listOf("java", "-version"))
  }

private const val compiler = "kotlinc"
private const val jarFile = "rkotlin.jar"

fun main(args: Array<String>) {
  val params = ParameterExtractor.parse(args)
  userTempDir = params.tempDir

  val files = params.sourceFiles
  val deps = CodeDependencies().getDeps(files)

  val workDir = getWorkPath(deps)
  val exe = buildPath(workDir, jarFile)

  if (anyNewerThan(deps, exe)) {
    rebuild(files, workDir)
  }

  if (params.buildOnly) {
    return
  }

  run(listOf("java", "-jar", buildPath(workDir, jarFile)))
}



fun getWorkPath(files: List<String>): String {
  val md = MessageDigest.getInstance("MD5")
  for (file in files) {
    md.update(Paths.get(file).toAbsolutePath().toString().toByteArray())
  }

  for (version in javaVersion + kotlinVersion) {
    md.update(version.toByteArray())
  }

  val digest = md.digest()
  val hash = digest.joinToString("") { "%02x".format(it) }

  return buildPath(myOwnTmpDir, "rkotlin-${hash}")
}

fun rebuild(root: List<String>, workDir: String) {
  val todo = root + listOf("-include-runtime", "-d", buildPath(workDir, jarFile))
  run(listOf(compiler) + todo)
}

fun getVersions(args: List<String>): List<String> {
  val processBuilder = ProcessBuilder(args)

  val process = try {
    processBuilder.start()
  } catch (e: IOException) {
    throw RuntimeException("Process start failed: ${e.message}", e)
  }

  val lines: MutableList<String> = mutableListOf()

  // 標準出力を表示
  BufferedReader(InputStreamReader(process.inputStream, Charset.defaultCharset())).use { r ->
    while (r.readLine().also { lines += it } != null) {}
  }

  // エラー出力を表示
  BufferedReader(InputStreamReader(process.errorStream, Charset.defaultCharset())).use { r ->
    while (r.readLine().also { lines += it } != null) {}
  }

  return lines
    .filterNotNull()
    .also{ process.destroy() }
}
fun run(args: List<String>): Int {
  val processBuilder = ProcessBuilder(args)

  val process = try {
    processBuilder.start()
  } catch (e: IOException) {
    throw RuntimeException("Process start failed: ${e.message}", e)
  }

  // 標準出力を表示
  BufferedReader(InputStreamReader(process.inputStream, Charset.defaultCharset())).use { r ->
    var line: String?
    while ((r.readLine().also { line = it }) != null) {
      println(line)
    }
  }

  // エラー出力を表示
  BufferedReader(InputStreamReader(process.errorStream, Charset.defaultCharset())).use { reader ->
    var line: String?
    while (reader.readLine().also { line = it } != null) {
      System.err.println(line)
    }
  }

  return process.waitFor().also { process.destroy() }
}