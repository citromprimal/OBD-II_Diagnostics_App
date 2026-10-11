package com.primal.cardiagnosticsapp

import com.primal.cardiagnosticsapp.data.DtcDataSource
import com.primal.cardiagnosticsapp.data.DtcItem
import com.primal.cardiagnosticsapp.data.DtcService
import com.primal.cardiagnosticsapp.data.DtcSeverity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
import io.mockk.coVerifyOrder
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals

class DiagnosticsServiceTest {

    @Test
    fun `readStoredDtcs disables polling and sends command 03`() = runTest {
        val obdManager = mockk<ObdManager>()

        coEvery { obdManager.setPollingEnabled(false) } just Runs
        coEvery { obdManager.send("03") } just Runs

        val service = DiagnosticsService(obdManager)

        service.readStoredDtcs()

        coVerify { obdManager.setPollingEnabled(false) }
        coVerify { obdManager.send("03") }
    }

    @Test
    fun `readPendingDtcs disables polling and sends command 07`() = runTest {
        val obdManager = mockk<ObdManager>()

        coEvery { obdManager.setPollingEnabled(false) } just Runs
        coEvery { obdManager.send("07") } just Runs

        val service = DiagnosticsService(obdManager)

        service.readPendingDtcs()

        coVerify { obdManager.setPollingEnabled(false) }
        coVerify { obdManager.send("07") }
    }

    @Test
    fun `readPermanentDtcs disables polling and sends command 0A`() = runTest {
        val obdManager = mockk<ObdManager>()

        coEvery { obdManager.setPollingEnabled(false) } just Runs
        coEvery { obdManager.send("0A") } just Runs

        val service = DiagnosticsService(obdManager)

        service.readPermanentDtcs()

        coVerify { obdManager.setPollingEnabled(false) }
        coVerify { obdManager.send("0A") }
    }

    @Test
    fun `resumePolling enables polling`() = runTest {
        val obdManager = mockk<ObdManager>()

        coEvery { obdManager.setPollingEnabled(true) } just Runs

        val service = DiagnosticsService(obdManager)

        service.resumePolling()

        coVerify { obdManager.setPollingEnabled(true) }
    }

    @Test
    fun `readStoredDtcs disables polling before sending command`() = runTest {
        val obdManager = mockk<ObdManager>()

        coEvery { obdManager.setPollingEnabled(false) } just Runs
        coEvery { obdManager.send("03") } just Runs

        val service = DiagnosticsService(obdManager)

        service.readStoredDtcs()

        coVerifyOrder {
            obdManager.setPollingEnabled(false)
            obdManager.send("03")
        }
    }

    @Test(expected = RuntimeException::class)
    fun `readStoredDtcs propagates communication error`() = runTest {
        val obdManager = mockk<ObdManager>()

        coEvery { obdManager.setPollingEnabled(false) } just Runs
        coEvery { obdManager.send("03") } throws RuntimeException("Bluetooth error")

        val service = DiagnosticsService(obdManager)

        service.readStoredDtcs()
    }

    @Test
    fun `readVin disables polling and sends VIN commands`() = runTest {
        val obdManager = mockk<ObdManager>()

        coEvery { obdManager.setPollingEnabled(false) } just Runs
        coEvery { obdManager.send("0902") } just Runs
        coEvery { obdManager.send("0151") } just Runs

        val service = DiagnosticsService(obdManager)

        service.readVin()

        coVerify { obdManager.setPollingEnabled(false) }
        coVerify { obdManager.send("0902") }
        coVerify { obdManager.send("0151") }
    }

    @Test
    fun `readVin sends commands in correct order`() = runTest {
        val obdManager = mockk<ObdManager>()

        coEvery { obdManager.setPollingEnabled(false) } just Runs
        coEvery { obdManager.send(any()) } just Runs

        val service = DiagnosticsService(obdManager)

        service.readVin()

        coVerifyOrder {
            obdManager.setPollingEnabled(false)
            obdManager.send("0902")
            obdManager.send("0151")
        }
    }

    @Test(expected = RuntimeException::class)
    fun `readVin propagates communication error`() = runTest {
        val obdManager = mockk<ObdManager>()

        coEvery { obdManager.setPollingEnabled(false) } just Runs
        coEvery { obdManager.send("0902") } throws RuntimeException("Bluetooth error")

        val service = DiagnosticsService(obdManager)

        service.readVin()
    }

    @Test
    fun `saveScan stores DTC scan and returns scan id`() = runTest {
        val dataSource = mockk<DtcDataSource>()

        val dtcs = listOf(
            DtcItem(
                code = "P0300",
                severity = DtcSeverity.WARNING
            ),
            DtcItem(
                code = "P0420",
                severity = DtcSeverity.WARNING
            )
        )

        coEvery {
            dataSource.insertScan(
                userId = "user-1",
                vehicleId = "vehicle-1",
                dtcs = dtcs
            )
        } returns "scan-123"

        val service = DtcService(dataSource)

        val result = service.saveScan(
            userId = "user-1",
            vehicleId = "vehicle-1",
            dtcs = dtcs
        )

        assertEquals("scan-123", result)

        coVerify(exactly = 1) {
            dataSource.insertScan(
                userId = "user-1",
                vehicleId = "vehicle-1",
                dtcs = dtcs
            )
        }
    }
}