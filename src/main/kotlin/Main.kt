package com.shioya86.rkotlin

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

private val myOwnTmpDir: String
  get() {
    var tmpRoot = System.getProperty("java.io.tmpdir")

    tmpRoot = Paths.get(tmpRoot, ".rkotlin-${userName}").toString()

    Files.createDirectories(Paths.get(tmpRoot))

    return tmpRoot
  }

private val compiler = "kotlinc"

fun main(args: Array<String>) {
  val params = ParameterExtractor.parse(args)

  val root = params.myProg
  val workDir = getWorkPath(root)
  val target = buildExeName(root.removeSuffix(".kt")) + "Kt"

  rebuild(root, workDir)

  run(listOf("kotlin", "-cp", workDir, target))
}

fun buildExeName(exeBaseName: String): String {

  // 最初の文字を大文字に変換
  val capitalizedString = exeBaseName.take(1).uppercase() + exeBaseName.drop(1)

  // 最初の"."を"_"に変換し、二つ目以降の"."を取り除く
  val modifiedString = capitalizedString
    .replaceFirst(".", "_")
    .replace(".", "")

  return modifiedString
}
fun buildPath(vararg elements: String): String {
  return Paths.get("", *elements).toString()
}

fun getWorkPath(root: String): String {
  val md = MessageDigest.getInstance("MD5")
  md.update(root.toByteArray())
  md.update(Paths.get(root).toAbsolutePath().toString().toByteArray())

  val digest = md.digest()

  val hash = digest.joinToString("") { "%02x".format(it) }

  return buildPath(myOwnTmpDir, "rkotlin-${hash}")
}

fun rebuild(root: String, workDir: String) {
  val todo = listOf(root, "-d", workDir)
  run(listOf(compiler) + todo)
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

  return process.waitFor()
}