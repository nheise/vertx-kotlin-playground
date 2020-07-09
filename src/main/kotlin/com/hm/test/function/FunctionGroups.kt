package com.hm.test.function

class FunctionGroups(private val sharedData: String) {
  fun printWithHello(str: String) = print("hello $sharedData $str")
  fun printWithHallo(str: String) = print("hallo $sharedData $str")
}
