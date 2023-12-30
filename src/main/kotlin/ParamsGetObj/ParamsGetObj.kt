package com.shioya86

import kotlinx.cli.*

class ArgParser {
  companion object {
    fun parse(args: List<String>): ArgType {
      val tmpDir by parser
    }
  }
}