package com.primal.cardiagnosticsapp.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class VehicleServiceTest {

    @Test
    fun `saveVehicle stores VIN information`() = runTest {
        val dataSource = mockk<VehicleDataSource>()

        val vinInfo = VinInfo(
            manufacturer = "Skoda",
            country = "Czech Republic",
            year = 2025,
            wmi = "TMB",
            region = "Europe",
            checkDigitValid = true,
            yearCandidates = listOf(2025),
            isValidFormat = true,
            isYearAmbiguous = false
        )

        val savedVehicle = VehicleRow(
            id = "vehicle-1",
            user_id = "user-1",
            vin = "TMBJJ7NE5L0123456",
            manufacturer = "Skoda",
            country = "Czech Republic",
            year = 2025,
            wmi = "TMB",
            region = "Europe",
            check_digit_valid = true,
            year_candidates = listOf(2025),
            is_valid_format = true,
            is_year_ambiguous = false
        )

        coEvery {
            dataSource.insertVehicle(
                userId = "user-1",
                vin = "TMBJJ7NE5L0123456",
                manufacturer = "Skoda",
                country = "Czech Republic",
                year = 2025,
                wmi = "TMB",
                region = "Europe",
                checkDigitValid = true,
                yearCandidates = listOf(2025),
                isValidFormat = true,
                isYearAmbiguous = false
            )
        } returns savedVehicle

        val service = VehicleService(dataSource)

        val result = service.saveVehicle(
            userId = "user-1",
            vin = "TMBJJ7NE5L0123456",
            info = vinInfo
        )

        assertEquals("TMBJJ7NE5L0123456", result.vin)
        assertEquals("Skoda", result.manufacturer)
        assertEquals("Czech Republic", result.country)
        assertEquals(2025, result.year)

        coVerify(exactly = 1) {
            dataSource.insertVehicle(
                userId = "user-1",
                vin = "TMBJJ7NE5L0123456",
                manufacturer = "Skoda",
                country = "Czech Republic",
                year = 2025,
                wmi = "TMB",
                region = "Europe",
                checkDigitValid = true,
                yearCandidates = listOf(2025),
                isValidFormat = true,
                isYearAmbiguous = false
            )
        }
    }
}