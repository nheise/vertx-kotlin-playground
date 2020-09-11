package com.hm.test.mocking

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SharedServiceTest {

    @MockK
    private lateinit var mockService: SharedService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun simpleCall() {
        val service = sharedService()
        assertEquals("something", service.giveMeSomething())
    }

    @Test
    fun mockedCall() {
        val service = sharedService(mockService)
        every { service.giveMeSomething() } returns "some mock thing"
        assertEquals("some mock thing", service.giveMeSomething())
    }
}
