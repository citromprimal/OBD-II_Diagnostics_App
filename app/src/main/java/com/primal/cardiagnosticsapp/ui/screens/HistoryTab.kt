package com.primal.cardiagnosticsapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.sp
import com.primal.cardiagnosticsapp.R
import com.primal.cardiagnosticsapp.StatusText
import com.primal.cardiagnosticsapp.data.DtcScanHistoryRow
import com.primal.cardiagnosticsapp.data.DtcScanItemHistoryRow
import com.primal.cardiagnosticsapp.data.VehicleRow
import com.primal.cardiagnosticsapp.ui.model.ConnectionStatus
import com.primal.cardiagnosticsapp.ui.theme.localizedVinPlace

@Composable
fun HistoryTab(
    status: ConnectionStatus,
    vehicles: List<VehicleRow>,
    activeVehicleId: String?,
    onDeleteVehicle: (VehicleRow) -> Unit,
    loadHistory: (VehicleRow) -> Unit,
    history: List<DtcScanHistoryRow>,
    selectedScanId: String?,
    selectedScanItems: List<DtcScanItemHistoryRow>,
    onSelectScan: (DtcScanHistoryRow) -> Unit,
    onDeleteScan: (DtcScanHistoryRow) -> Unit
) {
    var expandedVehicleId by rememberSaveable { mutableStateOf<String?>(null) }
    var vehiclePendingDelete by remember { mutableStateOf<VehicleRow?>(null) }
    var scanPendingDelete by remember { mutableStateOf<DtcScanHistoryRow?>(null) }

    vehiclePendingDelete?.let { vehicle ->
        DeleteConfirmDialog(
            title = stringResource(R.string.delete_vehicle),
            message = stringResource(R.string.confirm_delete_vehicle) +
                    " ${vehicle.vin}?\n\n" +
                    stringResource(R.string.delete_vehicle_info),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                if (expandedVehicleId == vehicle.id) {
                    expandedVehicleId = null
                }
                onDeleteVehicle(vehicle)
                vehiclePendingDelete = null
            },
            onDismiss = {
                vehiclePendingDelete = null
            }
        )
    }

    scanPendingDelete?.let { scan ->
        DeleteConfirmDialog(
            title = stringResource(R.string.delete_scan),
            message = stringResource(R.string.confirm_delete_scan) +
                    " ${formatScanDateTime(scan.scannedAt)}?",
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                onDeleteScan(scan)
                scanPendingDelete = null
            },
            onDismiss = {
                scanPendingDelete = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppSectionCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.status),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    StatusText(status = status)
                }

                Text(
                    text = stringResource(R.string.conn_in_sett),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (vehicles.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = stringResource(R.string.no_vehicles),
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Column
        }

        vehicles.forEach { vehicle ->
            val isActive = vehicle.id == activeVehicleId

            AppSectionCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = vehicle.vin,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace
                            )

                            Text(
                                text = listOfNotNull(
                                    vehicle.manufacturer,
                                    localizedVinPlace(vehicle.country),
                                    vehicle.year?.toString()
                                ).joinToString(" • ").ifBlank { "--" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HistoryInfoChip("WMI", vehicle.wmi)
                                HistoryInfoChip(stringResource(R.string.region), localizedVinPlace(vehicle.region))
                                HistoryInfoChip(
                                    stringResource(R.string.vin_format),
                                    if (vehicle.is_valid_format) stringResource(R.string.valid) else stringResource(R.string.invalid)
                                )
                                HistoryInfoChip(
                                    stringResource(R.string.check_digit),
                                    when (vehicle.check_digit_valid) {
                                        true -> stringResource(R.string.valid)
                                        false -> stringResource(R.string.invalid)
                                        null -> stringResource(R.string.not_used)
                                    }
                                )
                            }

                            if (!vehicle.year_candidates.isNullOrEmpty()) {

                                val candidatesText = vehicle.year_candidates.joinToString(
                                    separator = " ${stringResource(R.string.or)} "
                                )

                                val note = stringResource(
                                    R.string.vin_year_is_cyclical,
                                    candidatesText
                                )

                                Text(
                                    text = note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isActive) {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = stringResource(R.string.active),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (expandedVehicleId == vehicle.id) {
                                    expandedVehicleId = null
                                } else {
                                    expandedVehicleId = vehicle.id
                                    loadHistory(vehicle)
                                }
                            }
                        ) {
                            Text(if (expandedVehicleId == vehicle.id) stringResource(R.string.hide_scans) else stringResource(R.string.show_scans))
                        }

                        OutlinedButton(
                            onClick = { vehiclePendingDelete = vehicle },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                    }

                    if (expandedVehicleId == vehicle.id) {
                        if (history.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_scans),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                history.forEach { scan ->
                                    HistoryRow(
                                        scan = scan,
                                        isSelected = selectedScanId == scan.id,
                                        items = if (selectedScanId == scan.id) selectedScanItems else emptyList(),
                                        onClick = { onSelectScan(scan) },
                                        onDelete = { scanPendingDelete = scan }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(
    scan: DtcScanHistoryRow,
    isSelected: Boolean,
    items: List<DtcScanItemHistoryRow>,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (isSelected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
            else
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)
        ),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onClick)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)
                        )
                    ) {
                        Text(
                            text = formatScanDateTime(scan.scannedAt),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryPill(stringResource(R.string.total), scan.totalCount, selected = false) { }
                SummaryPill(stringResource(R.string.critical), scan.criticalCount, selected = false) { }
                SummaryPill(stringResource(R.string.warning), scan.warningCount, selected = false) { }
                SummaryPill(stringResource(R.string.info), scan.infoCount, selected = false) { }
            }

            if (isSelected) {
                if (items.isEmpty()) {
                    Text(
                        text = stringResource(R.string.scan_dtc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items.forEach { item ->
                            HistoryDtcItemRow(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryDtcItemRow(
    item: DtcScanItemHistoryRow
) {
    val severityColor = when (item.severity) {
        "CRITICAL" -> MaterialTheme.colorScheme.error
        "WARNING" -> Color(0xFFB26A00)
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${item.code} (${item.severity})",
                style = MaterialTheme.typography.labelLarge,
                color = severityColor,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = item.description ?: "--",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = if (item.isGeneric == true) stringResource(R.string.sae_code) else stringResource(R.string.manufacturer_code),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            if (!item.likelyCauses.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.likely_causes) + ": ${item.likelyCauses.replace(" | ", ", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!item.recommendation.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.recommendation) + ": ${item.recommendation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun HistoryInfoChip(
    label: String,
    value: String?
) {
    if (value.isNullOrBlank()) return
    AppInfoChip(text = "$label: $value")
}

private fun formatScanDateTime(iso: String): String {
    return try {
        val odt = java.time.OffsetDateTime.parse(iso)
        val local = odt.atZoneSameInstant(java.time.ZoneId.systemDefault())
        local.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
    } catch (e: Exception) {
        iso
    }
}

@Composable
fun DeleteConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(dismissText)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}