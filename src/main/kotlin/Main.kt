package com.shioya86.rkotlin

import com.shioya86.rkotlin.parameterExtractor.ParameterExtractor


fun main(args: Array<String>) {
  val params = ParameterExtractor.parse(args)

  println(params)
}