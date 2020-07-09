package com.hm.test.function

import org.junit.jupiter.api.Test

internal class FunctionGroupsTest {

  @Test
  fun testIt() {
    val fg = FunctionGroups("World")

    val printHello = fg::printWithHello
    val printHallo = fg::printWithHallo

    printHallo("test")
    printHello("test me")
  }
}
