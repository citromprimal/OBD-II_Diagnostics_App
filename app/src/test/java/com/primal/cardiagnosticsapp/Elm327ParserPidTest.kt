package com.primal.cardiagnosticsapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class Elm327ParserPidTest {

    @Test
    fun `parses RPM correctly`() {
        // ((0x1A * 256) + 0xF8) / 4 = 1726 RPM
        val result = Elm327Parser.parseFrame("41 0C 1A F8")

        assertNotNull(result)
        assertEquals(1726.0, result!!.rpm!!, 0.001)
    }

    @Test
    fun `parses speed correctly`() {
        // 0x64 = 100 km/h
        val result = Elm327Parser.parseFrame("41 0D 64")

        assertNotNull(result)
        assertEquals(100, result!!.speedKph)
    }

    @Test
    fun `parses coolant temperature correctly`() {
        // 0x7D = 125, 125 - 40 = 85 °C
        val result = Elm327Parser.parseFrame("41 05 7D")

        assertNotNull(result)
        assertEquals(85, result!!.coolantC)
    }

    @Test
    fun `parses MAP correctly`() {
        // 0x64 = 100 kPa
        val result = Elm327Parser.parseFrame("41 0B 64")

        assertNotNull(result)
        assertEquals(100, result!!.mapKpa)
    }

    @Test
    fun `parses IAT correctly`() {
        // 0x46 = 70, 70 - 40 = 30 °C
        val result = Elm327Parser.parseFrame("41 0F 46")

        assertNotNull(result)
        assertEquals(30, result!!.iatC)
    }

    @Test
    fun `returns null for unsupported frame`() {
        val result = Elm327Parser.parseFrame("NO DATA")

        assertNull(result)
    }

    @Test
    fun `parses MAF correctly`() {
        // ((0x01 * 256) + 0xF4) / 100 = 5.00 g/s
        val result = Elm327Parser.parseFrame("41 10 01 F4")

        assertNotNull(result)
        assertEquals(5.0f, result!!.mafGps!!, 0.001f)
    }

    @Test
    fun `calculates boost from MAP and BARO`() {
        // MAP = 150 kPa, BARO = 100 kPa
        // boost = 50 kPa
        val result = Elm327Parser.parseFrame(
            "41 0B 96 41 33 64"
        )

        assertNotNull(result)
        assertEquals(150, result!!.mapKpa)
        assertEquals(100, result.baroKpa)
        assertEquals(50.0, result.boostKpa!!, 0.001)
    }

    @Test
    fun `parses control module voltage`() {
        // 0x36B0 = 14000 / 1000 = 14.0 V
        val result = Elm327Parser.parseFrame("41 42 36 B0")

        assertNotNull(result)
        assertEquals(14.0f, result!!.controlModuleVoltage!!, 0.001f)
    }

    @Test
    fun `parses throttle percentage`() {
        // 0x80 = 128, cca 50.2 %
        val result = Elm327Parser.parseFrame("41 11 80")

        assertNotNull(result)
        assertEquals(
            128 * 100.0 / 255.0,
            result!!.throttlePct!!,
            0.001
        )
    }

    @Test
    fun `parses timing advance`() {
        // 0x80 = 128
        // 128 / 2 - 64 = 0 degrees
        val result = Elm327Parser.parseFrame("41 0E 80")

        assertNotNull(result)
        assertEquals(0.0f, result!!.timingAdvanceDeg!!, 0.001f)
    }

    @Test
    fun `parses multiple PID values from one frame`() {
        val raw = """
        41 0C 1A F8
        41 0D 64
        41 05 7D
        41 0B 96
        41 33 64
        41 10 01 F4
    """.trimIndent()

        val result = Elm327Parser.parseFrame(raw)

        assertNotNull(result)

        // RPM = ((0x1A * 256) + 0xF8) / 4 = 1726
        assertEquals(1726.0, result!!.rpm!!, 0.001)

        // Speed = 0x64 = 100 km/h
        assertEquals(100, result.speedKph)

        // Coolant = 0x7D - 40 = 85 °C
        assertEquals(85, result.coolantC)

        // MAP = 150 kPa
        assertEquals(150, result.mapKpa)

        // BARO = 100 kPa
        assertEquals(100, result.baroKpa)

        // Boost = MAP - BARO = 50 kPa
        assertEquals(50.0, result.boostKpa!!, 0.001)

        // MAF = 5.0 g/s
        assertEquals(5.0f, result.mafGps!!, 0.001f)

        assertNotNull(result.fuelRateLph)

        assertNotNull(result.fuelConsumptionL100)
    }

    @Test
    fun `boost is never negative when MAP is lower than BARO`() {
        val raw = """
        41 0B 5A
        41 33 64
    """.trimIndent()

        val result = Elm327Parser.parseFrame(raw)

        assertNotNull(result)

        // MAP = 90 kPa
        assertEquals(90, result!!.mapKpa)

        // BARO = 100 kPa
        assertEquals(100, result.baroKpa)

        // Negative boost is clipped to 0
        assertEquals(0.0, result.boostKpa!!, 0.001)
    }

    @Test
    fun `fuel consumption is null when speed is zero`() {
        val raw = """
        41 0D 00
        41 10 01 F4
    """.trimIndent()

        val result = Elm327Parser.parseFrame(raw)

        assertNotNull(result)
        assertEquals(0, result!!.speedKph)
        assertEquals(5.0f, result.mafGps!!, 0.001f)

        assertNull(result.fuelConsumptionL100)
    }
}