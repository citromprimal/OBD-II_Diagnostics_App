package com.primal.cardiagnosticsapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.primal.cardiagnosticsapp.R
import com.primal.cardiagnosticsapp.StatusText
import com.primal.cardiagnosticsapp.data.DtcItem
import com.primal.cardiagnosticsapp.data.DtcSeverity
import com.primal.cardiagnosticsapp.data.VinInfo
import com.primal.cardiagnosticsapp.ui.model.ConnectionStatus
import com.primal.cardiagnosticsapp.ui.theme.AppLanguage
import com.primal.cardiagnosticsapp.ui.theme.localizedVinPlace

enum class DtcFilter {
    ALL,
    CRITICAL,
    WARNING,
    INFO
}

enum class DtcCategory {
    STORED,
    PENDING,
    PERMANENT
}

@Composable
fun DiagnosticsTab(
    status: ConnectionStatus,
    vin: String?,
    vinInfo: VinInfo?,
    dtcs: List<DtcItem>,
    pendingDtcs: List<DtcItem>,
    permanentDtcs: List<DtcItem>,
    canReadDtcs: Boolean,
    language: AppLanguage,
    vinStatus: String?,
    dtcStatus: String?,
    onReadVin: () -> Unit,
    onReadDtcs: () -> Unit,
    onClearDtcs: () -> Unit
) {
    var filter by rememberSaveable { mutableStateOf(DtcFilter.ALL) }
    var dtcCategory by rememberSaveable { mutableStateOf(DtcCategory.STORED) }

    val activeDtcs = when (dtcCategory) {
        DtcCategory.STORED -> dtcs
        DtcCategory.PENDING -> pendingDtcs
        DtcCategory.PERMANENT -> permanentDtcs
    }

    val criticalCount = activeDtcs.count { it.severity == DtcSeverity.CRITICAL }
    val warningCount = activeDtcs.count { it.severity == DtcSeverity.WARNING }
    val infoCount = activeDtcs.count { it.severity == DtcSeverity.INFO }

    val filteredDtcs = when (filter) {
        DtcFilter.ALL -> activeDtcs
        DtcFilter.CRITICAL -> activeDtcs.filter { it.severity == DtcSeverity.CRITICAL }
        DtcFilter.WARNING -> activeDtcs.filter { it.severity == DtcSeverity.WARNING }
        DtcFilter.INFO -> activeDtcs.filter { it.severity == DtcSeverity.INFO }
    }

    val sortedDtcs = filteredDtcs.sortedWith(
        compareBy(
            {
                when (it.severity) {
                    DtcSeverity.CRITICAL -> 0
                    DtcSeverity.WARNING -> 1
                    DtcSeverity.INFO -> 2
                }
            },
            { it.code }
        )
    )

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

        Text(stringResource(R.string.tab_diagnostics), style = MaterialTheme.typography.titleMedium)

        AppSectionCard {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {


                Button(
                    onClick = onReadVin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.read_vin))
                }

                Button(
                    onClick = onReadDtcs,
                    enabled = canReadDtcs,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.read_dtcs))
                }

                OutlinedButton(
                    onClick = onClearDtcs,
                    enabled = dtcs.isNotEmpty() || pendingDtcs.isNotEmpty() || permanentDtcs.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.clear_dtcs))
                }
            }
        }

        Text(text = stringResource(R.string.vehicle_id), style = MaterialTheme.typography.titleMedium)

        if (!vinStatus.isNullOrBlank()) {
            AppInfoChip(vinStatus)
        }

        AppSectionCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.vin),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = vin ?: "--",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(stringResource(R.string.manufacturer), vinInfo?.manufacturer)
                    InfoChip(stringResource(R.string.country), localizedVinPlace(vinInfo?.country))
                    InfoChip(stringResource(R.string.year), vinInfo?.year?.toString())
                    InfoChip("WMI", vinInfo?.wmi)
                    InfoChip(stringResource(R.string.region), localizedVinPlace(vinInfo?.region))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(
                        stringResource(R.string.vin_format),
                        when {
                            vinInfo == null -> null
                            vinInfo.isValidFormat -> stringResource(R.string.valid)
                            else -> stringResource(R.string.invalid)
                        }
                    )
                    InfoChip(
                        stringResource(R.string.check_digit),
                        when (vinInfo?.checkDigitValid) {
                            true -> stringResource(R.string.valid)
                            false -> stringResource(R.string.invalid)
                            null -> stringResource(R.string.not_used)
                        }
                    )
                }

                if (vinInfo != null && !vinInfo.isYearReliable) {
                    Text(
                        text = stringResource(R.string.vin_year_not_reliable),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else if (!vinInfo?.yearCandidates.isNullOrEmpty()) {
                    val candidatesText = vinInfo!!.yearCandidates!!.joinToString(
                        separator = " ${stringResource(R.string.or)} "
                    )

                    Text(
                        text = stringResource(R.string.vin_year_is_cyclical, candidatesText),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text(stringResource(R.string.trouble_codes), style = MaterialTheme.typography.titleMedium)

        if (!dtcStatus.isNullOrBlank()) {
            AppInfoChip(dtcStatus)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = dtcCategory == DtcCategory.STORED,
                onClick = { dtcCategory = DtcCategory.STORED },
                label = { Text(stringResource(R.string.stored_dtcs)) }
            )
            FilterChip(
                selected = dtcCategory == DtcCategory.PENDING,
                onClick = { dtcCategory = DtcCategory.PENDING },
                label = { Text(stringResource(R.string.pending_dtcs)) }
            )
            FilterChip(
                selected = dtcCategory == DtcCategory.PERMANENT,
                onClick = { dtcCategory = DtcCategory.PERMANENT },
                label = { Text(stringResource(R.string.permanent_dtcs)) }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryPill(
                label = stringResource(R.string.total),
                value = activeDtcs.size,
                selected = filter == DtcFilter.ALL,
                onClick = { filter = DtcFilter.ALL }
            )
            SummaryPill(
                label = stringResource(R.string.critical),
                value = criticalCount,
                selected = filter == DtcFilter.CRITICAL,
                onClick = { filter = DtcFilter.CRITICAL }
            )
            SummaryPill(
                label = stringResource(R.string.warning),
                value = warningCount,
                selected = filter == DtcFilter.WARNING,
                onClick = { filter = DtcFilter.WARNING }
            )
            SummaryPill(
                label = stringResource(R.string.info),
                value = infoCount,
                selected = filter == DtcFilter.INFO,
                onClick = { filter = DtcFilter.INFO }
            )
        }

        val statusSaysNoDtcs =
            dtcStatus == stringResource(R.string.no_stored_dtcs) ||
                    dtcStatus == stringResource(R.string.no_pending_dtcs) ||
                    dtcStatus == stringResource(R.string.no_permanent_dtcs)

        if (activeDtcs.isEmpty()) {
            if (!statusSaysNoDtcs) {
                when (dtcCategory) {
                    DtcCategory.STORED -> AllClearBox(text = stringResource(R.string.no_stored_dtcs))
                    DtcCategory.PENDING -> AllClearBox(text = stringResource(R.string.no_pending_dtcs))
                    DtcCategory.PERMANENT -> AllClearBox(text = stringResource(R.string.no_permanent_dtcs))
                }
            }
        } else if (sortedDtcs.isEmpty()) {
            EmptyCategoryBox()
        } else {
            Column { sortedDtcs.forEach { DtcRow(it, language) } }
        }
    }
}

@Composable
private fun EmptyCategoryBox() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Filled.Info, contentDescription = null)
            Text(
                text = stringResource(R.string.no_dtcs_in_category),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DtcRow(item: DtcItem, language: AppLanguage) {
    AppInnerBlock(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.code,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace
                )
                SeverityChip(item.severity)
            }

            if (!item.description.isNullOrBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            AppInfoChip(
                text = if (item.isGeneric == true)
                    stringResource(R.string.sae_code)
                else
                    stringResource(R.string.manufacturer_code)
            )

            if (item.likelyCauses.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.likely_causes) + ": ${item.likelyCauses.joinToString(", ")}",
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
private fun SeverityChip(severity: DtcSeverity) {
    val bg: Color
    val fg: Color
    val icon: ImageVector

    when (severity) {
        DtcSeverity.CRITICAL -> {
            bg = MaterialTheme.colorScheme.errorContainer
            fg = MaterialTheme.colorScheme.onErrorContainer
            icon = Icons.Filled.Error
        }

        DtcSeverity.WARNING -> {
            bg = Color(0xFFFFECB3)
            fg = Color(0xFF6D4C41)
            icon = Icons.Filled.Warning
        }

        DtcSeverity.INFO -> {
            bg = MaterialTheme.colorScheme.primaryContainer
            fg = MaterialTheme.colorScheme.onPrimaryContainer
            icon = Icons.Filled.Info
        }
    }

    Row(
        modifier = Modifier
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = fg)
        Text(
            text = severity.name,
            color = fg,
            style = MaterialTheme.typography.labelMedium
        )
    }
}