package com.primal.cardiagnosticsapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VinDecoderTest {
    @Test
    fun `parses valid 17 character VIN`() {
        val raw =
            "49 02 01 54 4D 42 4A 4A 37 4E 45 35 4C 30 31 32 33 34 35 36"

        val vin = Elm327Parser.tryParseVin(raw)

        assertEquals("TMBJJ7NE5L0123456", vin)
    }

    @Test
    fun `returns null when VIN response is empty`() {
        val vin = Elm327Parser.tryParseVin("")

        assertNull(vin)
    }

    @Test
    fun `returns null when VIN has fewer than 17 characters`() {
        val raw = "49 02 01 54 4D 42 4A"

        val vin = Elm327Parser.tryParseVin(raw)

        assertNull(vin)
    }
}
