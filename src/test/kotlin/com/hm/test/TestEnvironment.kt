package com.hm.test

import com.hm.test.Environment.ENV_VAR_1
import com.hm.test.Environment.ENV_VAR_2
import org.junit.jupiter.api.Test

class TestEnvironment {

    @Test
    fun testGetEnvironmentVariable() {
        assert(ENV_VAR_1 == "TestVar1")
    }

    @Test
    fun testGetDefaultWhenEnvironmentVariableNotPresent() {
        assert(ENV_VAR_2 == "default_value")
    }
}
