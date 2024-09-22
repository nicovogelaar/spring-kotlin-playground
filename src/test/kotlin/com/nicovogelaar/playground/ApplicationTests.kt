package com.nicovogelaar.playground

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

class ApplicationTests {
    @Test
    fun `application context can start and stop`() {
        val context: ConfigurableApplicationContext =
            runApplication<Application>(
                "--server.port=8081",
            )

        assertTrue(context.isRunning, "Application context should be running")

        context.close()

        assertFalse(context.isRunning, "Application context should no longer be running")
    }
}
