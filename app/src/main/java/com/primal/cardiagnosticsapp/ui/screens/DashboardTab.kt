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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.primal.cardiagnosticsapp.R
import com.primal.cardiagnosticsapp.StatusText
import com.primal.cardiagnosticsapp.data.DtcItem
import com.primal.cardiagnosticsapp.data.DtcSeverity
import com.primal.cardiagnosticsapp.data.DynamicVehicleData
import com.primal.cardiagnosticsapp.data.StaticVehicleData
import com.primal.cardiagnosticsapp.ui.model.ConnectionStatus
import com.primal.cardiagnosticsapp.ui.theme.AppLanguage
import com.primal.cardiagnosticsapp.ui.theme.localizedVinPlace
import java.util.Locale

private enum class DashboardMode {
    ALL,
    STATIC,
    DYNAMIC
}

private enum class DashboardDtcCategory {
    STORED,
    PENDING,
    PERMANENT
}

@Composable
fun DashboardTab(
    status: ConnectionStatus,
    staticData: StaticVehicleData,
    dynamicData: DynamicVehicleData
) {
    var mode by rememberSaveable { mutableStateOf(DashboardMode.ALL) }

    var dtcCategory by rememberSaveable { mutableStateOf(DashboardDtcCategory.STORED) }

    val activeDtcs = when (dtcCategory) {
        DashboardDtcCategory.STORED -> staticData.dtcs
        DashboardDtcCategory.PENDING -> staticData.pendingDtcs
        DashboardDtcCategory.PERMANENT -> staticData.permanentDtcs
    }

    val activeEmptyText = when (dtcCategory) {
        DashboardDtcCategory.STORED -> stringResource(R.string.no_stored_dtcs)
        DashboardDtcCategory.PENDING -> stringResource(R.string.no_pending_dtcs)
        DashboardDtcCategory.PERMANENT -> stringResource(R.string.no_permanent_dtcs)
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
                    .padding(16.dp),
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = mode == DashboardMode.ALL,
                onClick = { mode = DashboardMode.ALL },
                label = { Text(stringResource(R.string.tab_all)) }
            )
            FilterChip(
                selected = mode == DashboardMode.STATIC,
                onClick = { mode = DashboardMode.STATIC },
                label = { Text(stringResource(R.string.tab_static)) }
            )
            FilterChip(
                selected = mode == DashboardMode.DYNAMIC,
                onClick = { mode = DashboardMode.DYNAMIC },
                label = { Text(stringResource(R.string.tab_dynamic)) }
            )
        }

        // ---- STATIC SUMMARY ----
        if (mode == DashboardMode.ALL || mode == DashboardMode.STATIC) {
            Text(stringResource(R.string.static_data), style = MaterialTheme.typography.titleMedium)

            AppSectionCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.vin),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = staticData.vin ?: "--",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoChip(stringResource(R.string.manufacturer), staticData.vinInfo?.manufacturer)
                        InfoChip(stringResource(R.string.country), localizedVinPlace(staticData.vinInfo?.country))
                        InfoChip(stringResource(R.string.year), staticData.vinInfo?.year?.toString())
                        InfoChip(stringResource(R.string.fuel_type), staticData.fuelType)
                    }

                    Divider()

                    Text(
                        text = stringResource(R.string.dtc_summary),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = dtcCategory == DashboardDtcCategory.STORED,
                            onClick = { dtcCategory = DashboardDtcCategory.STORED },
                            label = { Text(stringResource(R.string.stored_dtcs)) }
                        )
                        FilterChip(
                            selected = dtcCategory == DashboardDtcCategory.PENDING,
                            onClick = { dtcCategory = DashboardDtcCategory.PENDING },
                            label = { Text(stringResource(R.string.pending_dtcs)) }
                        )
                        FilterChip(
                            selected = dtcCategory == DashboardDtcCategory.PERMANENT,
                            onClick = { dtcCategory = DashboardDtcCategory.PERMANENT },
                            label = { Text(stringResource(R.string.permanent_dtcs)) }
                        )
                    }

                    DtcSummarySection(
                        dtcs = activeDtcs,
                        emptyText = activeEmptyText
                    )
                }
            }
        }

        // ---- DYNAMIC SUMMARY ----
        if (mode == DashboardMode.ALL || mode == DashboardMode.DYNAMIC) {
            Text(stringResource(R.string.dynamic_data), style = MaterialTheme.typography.titleMedium)

            AppSectionCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AppInnerBlock {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.dynamic_data),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LiveRow(stringResource(R.string.speed), dynamicData.speedKph?.let { "$it km/h" } ?: "--")
                            LiveRow(stringResource(R.string.rpm), dynamicData.rpm?.let { it.toInt().toString() } ?: "--")
                            LiveRow(stringResource(R.string.coolant), dynamicData.coolantC?.let { "$it °C" } ?: "--")
                            LiveRow(stringResource(R.string.throttle), dynamicData.throttlePct?.let { "${it.toInt()} %" } ?: "--")
                            LiveRow(stringResource(R.string.accelerator_pedal), dynamicData.acceleratorPedalPct?.let { String.format(Locale.US, "%.1f %%", it) } ?: "--")
                            LiveRow(stringResource(R.string.engine_load), dynamicData.engineLoadPct?.let { String.format(Locale.US, "%.1f %%", it) } ?: "--")
                            LiveRow(stringResource(R.string.boost), dynamicData.boostPsi?.let { String.format(Locale.US, "%.1f PSI", it) } ?: "--")
                        }
                    }

                    AppInnerBlock {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.fuel),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LiveRow(stringResource(R.string.fuel_level), formatFuelLevel(dynamicData.fuelLevelPct))
                            LiveRow(
                                stringResource(R.string.fuel_consumption),
                                formatFuelConsumption(
                                    speedKph = dynamicData.speedKph?.toFloat(),
                                    mafGps = dynamicData.mafGps
                                )
                            )
                            LiveRow(stringResource(R.string.fuel_rate), formatFuelRate(dynamicData.mafGps))
                            LiveRow(stringResource(R.string.fuel_pressure), dynamicData.fuelPressureKpa?.let { "$it kPa" } ?: "--")
                        }
                    }

                    AppInnerBlock {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.airflow_pressure),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LiveRow(stringResource(R.string.map), dynamicData.mapKpa?.let { "$it kPa" } ?: "--")
                            LiveRow(stringResource(R.string.maf), dynamicData.mafGps?.let { String.format(Locale.US, "%.1f g/s", it) } ?: "--")
                            LiveRow(stringResource(R.string.iat), dynamicData.iatC?.let { "$it °C" } ?: "--")
                            LiveRow(
                                stringResource(R.string.timing_advance),
                                dynamicData.timingAdvanceDeg?.let { String.format(Locale.US, "%.1f°", it) } ?: "--"
                            )
                            LiveRow(stringResource(R.string.baro), dynamicData.baroKpa?.let { "$it kPa" } ?: "--")
                            LiveRow(stringResource(R.string.voltage), dynamicData.controlModuleVoltage?.let { String.format(Locale.US, "%.2f V", it) } ?: "--")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun InfoChip(label: String, value: String?) {
    val shown = value?.takeIf { it.isNotBlank() } ?: "--"
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)),
        tonalElevation = 0.dp
    ) {
        Text(
            text = "$label: $shown",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SummaryPill(
    label: String,
    value: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    }

    val fg = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val shape = RoundedCornerShape(999.dp)

    val icon = when (label.lowercase()) {
        "total", "celkovo" -> Icons.AutoMirrored.Filled.List
        "critical", "kritické" -> Icons.Filled.Error
        "warning", "varovanie" -> Icons.Filled.Warning
        "info" -> Icons.Filled.Info
        "active" -> Icons.Filled.CheckCircle
        else -> null
    }

    Surface(
        shape = shape,
        color = bg,
        tonalElevation = 0.dp,
        border = BorderStroke(
            1.dp,
            if (selected)
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = fg
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = fg
            )

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = fg
            )
        }
    }
}

@Composable
fun AllClearBox(
    text: String = stringResource(R.string.no_stored_dtcs)
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null)
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatFuelLevel(value: Float?): String {
    return value?.let { "${it.toInt()} %" } ?: "--"
}

private fun formatFuelConsumption(speedKph: Float?, mafGps: Float?): String {
    val lPer100 = calculateFuelConsumptionLPer100(speedKph, mafGps)
    return lPer100?.let { String.format(Locale.US, "%.1f L/100km", it) } ?: "--"
}

private fun formatFuelRate(mafGps: Float?): String {
    val lph = calculateFuelRateLph(mafGps)
    return lph?.let { String.format(Locale.US, "%.1f L/h", it) } ?: "--"
}

private fun calculateFuelRateLph(
    mafGps: Float?,
    afr: Float = 14.7f,
    fuelDensityGPerL: Float = 745f
): Float? {
    if (mafGps == null || mafGps <= 0f) return null
    return (mafGps * 3600f) / (afr * fuelDensityGPerL)
}

private fun calculateFuelConsumptionLPer100(
    speedKph: Float?,
    mafGps: Float?,
    afr: Float = 14.7f,
    fuelDensityGPerL: Float = 745f
): Float? {
    if (speedKph == null || speedKph <= 0f) return null
    if (mafGps == null || mafGps <= 0f) return null

    val litersPerHour = (mafGps * 3600f) / (afr * fuelDensityGPerL)
    return (litersPerHour / speedKph) * 100f
}

fun parseFuelType(raw: String, language: AppLanguage): String? {
    val compact = raw
        .replace("\r", "")
        .replace("\n", "")
        .replace(" ", "")
        .uppercase()

    val i = compact.indexOf("4151")
    if (i < 0 || compact.length < i + 6) return null

    return when (compact.substring(i + 4, i + 6).toIntOrNull(16)) {
        1 -> if (language == AppLanguage.SLOVAK) "Benzín" else "Gasoline"
        2 -> if (language == AppLanguage.SLOVAK) "Metanol" else "Methanol"
        3 -> if (language == AppLanguage.SLOVAK) "Etanol" else "Ethanol"
        4 -> if (language == AppLanguage.SLOVAK) "Diesel" else "Diesel"
        5 -> if (language == AppLanguage.SLOVAK) "LPG" else "LPG"
        6 -> if (language == AppLanguage.SLOVAK) "CNG" else "CNG"
        7 -> if (language == AppLanguage.SLOVAK) "Propán" else "Propane"
        8 -> if (language == AppLanguage.SLOVAK) "Elektrické" else "Electric"

        15 -> if (language == AppLanguage.SLOVAK) {
            "Hybridný benzín"
        } else {
            "Hybrid gasoline"
        }

        16 -> if (language == AppLanguage.SLOVAK) {
            "Hybridný etanol"
        } else {
            "Hybrid ethanol"
        }

        17 -> if (language == AppLanguage.SLOVAK) {
            "Hybridný diesel"
        } else {
            "Hybrid diesel"
        }

        18 -> if (language == AppLanguage.SLOVAK) {
            "Hybridné elektrické"
        } else {
            "Hybrid electric"
        }

        19 -> if (language == AppLanguage.SLOVAK) {
            "Hybrid elektrický a spaľovací"
        } else {
            "Hybrid running electric and combustion"
        }

        20 -> if (language == AppLanguage.SLOVAK) {
            "Rekuperačný hybrid"
        } else {
            "Hybrid regenerative"
        }

        else -> null
    }
}

@Composable
private fun DtcSummarySection(
    dtcs: List<DtcItem>,
    emptyText: String
) {
    val critical = dtcs.count { it.severity == DtcSeverity.CRITICAL }
    val warning = dtcs.count { it.severity == DtcSeverity.WARNING }
    val info = dtcs.count { it.severity == DtcSeverity.INFO }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryPill(stringResource(R.string.total), dtcs.size, selected = false) {}
            SummaryPill(stringResource(R.string.critical), critical, selected = false) {}
            SummaryPill(stringResource(R.string.warning), warning, selected = false) {}
            SummaryPill(stringResource(R.string.info), info, selected = false) {}
        }

        if (dtcs.isEmpty()) {
            AllClearBox(text = emptyText)
        }
    }
}