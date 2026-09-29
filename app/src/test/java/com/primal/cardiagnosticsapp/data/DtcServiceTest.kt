package com.primal.cardiagnosticsapp.data

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DtcServiceTest {

    @Test
    fun `returns true when new scan has same DTC codes as latest scan`() = runTest {
        val dataSource = mockk<DtcDataSource>()

        val latestScan = DtcScanHistoryRow(
            id = "scan-1",
            userId = "user-1",
            vehicleId = "vehicle-1",
            scannedAt = "2026-09-29T12:00:00",
            totalCount = 2,
            criticalCount = 0,
            warningCount = 2,
            infoCount = 0
        )

        val latestItems = listOf(
            DtcScanItemHistoryRow(
                id = "item-1",
                userId = "user-1",
                scanId = "scan-1",
                code = "P0300",
                severity = "WARNING"
            ),
            DtcScanItemHistoryRow(
                id = "item-2",
                userId = "user-1",
                scanId = "scan-1",
                code = "P0420",
                severity = "WARNING"
            )
        )

        coEvery {
            dataSource.getScanHistory("user-1", "vehicle-1")
        } returns listOf(latestScan)

        coEvery {
            dataSource.getScanItems("user-1", "scan-1")
        } returns latestItems

        val service = DtcService(dataSource)

        val newDtcs = listOf(
            DtcItem(
                code = "P0420",
                severity = DtcSeverity.WARNING
            ),
            DtcItem(
                code = "P0300",
                severity = DtcSeverity.WARNING
            )
        )

        val result = service.isDuplicateOfLatestScan(
            userId = "user-1",
            vehicleId = "vehicle-1",
            dtcs = newDtcs
        )

        assertTrue(result)
    }

    @Test
    fun `returns false when latest scan contains different DTC codes`() = runTest {
        val dataSource = mockk<DtcDataSource>()

        val latestScan = DtcScanHistoryRow(
            id = "scan-1",
            userId = "user-1",
            vehicleId = "vehicle-1",
            scannedAt = "2026-09-29T12:00:00",
            totalCount = 1,
            criticalCount = 0,
            warningCount = 1,
            infoCount = 0
        )

        val latestItems = listOf(
            DtcScanItemHistoryRow(
                id = "item-1",
                userId = "user-1",
                scanId = "scan-1",
                code = "P0300",
                severity = "WARNING"
            )
        )

        coEvery {
            dataSource.getScanHistory("user-1", "vehicle-1")
        } returns listOf(latestScan)

        coEvery {
            dataSource.getScanItems("user-1", "scan-1")
        } returns latestItems

        val service = DtcService(dataSource)

        val newDtcs = listOf(
            DtcItem(
                code = "P0420",
                severity = DtcSeverity.WARNING
            )
        )

        val result = service.isDuplicateOfLatestScan(
            userId = "user-1",
            vehicleId = "vehicle-1",
            dtcs = newDtcs
        )

        assertFalse(result)
    }

    @Test
    fun `returns false when no previous scan exists`() = runTest {
        val dataSource = mockk<DtcDataSource>()

        coEvery {
            dataSource.getScanHistory("user-1", "vehicle-1")
        } returns emptyList()

        val service = DtcService(dataSource)

        val result = service.isDuplicateOfLatestScan(
            userId = "user-1",
            vehicleId = "vehicle-1",
            dtcs = listOf(
                DtcItem(
                    code = "P0300",
                    severity = DtcSeverity.WARNING
                )
            )
        )

        assertFalse(result)
    }

    @Test
    fun `ignores case and whitespace when comparing DTC codes`() = runTest {
        val dataSource = mockk<DtcDataSource>()

        val latestScan = DtcScanHistoryRow(
            id = "scan-1",
            userId = "user-1",
            vehicleId = "vehicle-1",
            scannedAt = "2026-09-29T12:00:00",
            totalCount = 1,
            criticalCount = 0,
            warningCount = 1,
            infoCount = 0
        )

        val latestItems = listOf(
            DtcScanItemHistoryRow(
                id = "item-1",
                userId = "user-1",
                scanId = "scan-1",
                code = " p0300 ",
                severity = "WARNING"
            )
        )

        coEvery {
            dataSource.getScanHistory("user-1", "vehicle-1")
        } returns listOf(latestScan)

        coEvery {
            dataSource.getScanItems("user-1", "scan-1")
        } returns latestItems

        val service = DtcService(dataSource)

        val newDtcs = listOf(
            DtcItem(
                code = "P0300",
                severity = DtcSeverity.WARNING
            )
        )

        val result = service.isDuplicateOfLatestScan(
            userId = "user-1",
            vehicleId = "vehicle-1",
            dtcs = newDtcs
        )

        assertTrue(result)
    }
}