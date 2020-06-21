package com.hm.test

import org.junit.jupiter.api.Test

import com.hm.test.Environment.ENV_VAR_1
import com.hm.test.Environment.ENV_VAR_2

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