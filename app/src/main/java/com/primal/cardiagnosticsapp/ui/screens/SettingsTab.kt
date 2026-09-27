package com.primal.cardiagnosticsapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.primal.cardiagnosticsapp.DeviceItem
import com.primal.cardiagnosticsapp.R
import com.primal.cardiagnosticsapp.StatusText
import com.primal.cardiagnosticsapp.ui.model.ConnectionStatus
import com.primal.cardiagnosticsapp.ui.model.GraphTypeKey
import com.primal.cardiagnosticsapp.ui.theme.AppLanguage
import com.primal.cardiagnosticsapp.ui.theme.ThemeMode
import kotlin.collections.forEach

@Composable
fun SettingsTab(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    status: ConnectionStatus,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    scanning: Boolean,
    devices: List<DeviceItem>,
    onScan: () -> Unit,
    onStop: () -> Unit,
    onConnect: (DeviceItem) -> Unit,
    onDisconnect: () -> Unit,
    graphColors: Map<String, Int>,
    onGraphColorChange: (GraphTypeKey, Int) -> Unit,
    onLogout: () -> Unit
) {
    var selectedGraphColorKey by rememberSaveable { mutableStateOf(GraphTypeKey.SPEED) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ===== CONNECTION / ADAPTER =====
        AppSectionCard {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = stringResource(R.string.connection), style = MaterialTheme.typography.titleMedium)

                Column {
                    Text(
                        text = stringResource(R.string.status),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    StatusText(status = status)
                }

                Divider()

                val disconnectWeight = if (language == AppLanguage.ENGLISH) 1.3f else 1f

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onScan,
                        enabled = !scanning,
                        modifier = Modifier.weight(1f)
                    ) { Text(stringResource(R.string.scan)) }

                    Button(
                        onClick = onStop,
                        modifier = Modifier.weight(1f)
                    ) { Text(stringResource(R.string.stop)) }

                    OutlinedButton(
                        onClick = onDisconnect,
                        modifier = Modifier.weight(disconnectWeight)
                    ) {
                        Text(
                            text = stringResource(R.string.disconnect),
                            maxLines = 1
                        )
                    }
                }

                Divider()

                if (devices.isEmpty()) {
                    Text(
                        stringResource(R.string.no_devices),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        devices.forEach { dev ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(dev.name.ifBlank { "Unnamed device" })
                                    Text(
                                        dev.address,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(onClick = { onConnect(dev) }) {
                                    Text(stringResource(R.string.connect))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ===== APPEARANCE =====
        AppSectionCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.appearance), style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = stringResource(R.string.appearance_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                ThemeOption(
                    label = stringResource(R.string.system_default),
                    selected = themeMode == ThemeMode.SYSTEM,
                    onClick = { onThemeChange(ThemeMode.SYSTEM) }
                )

                ThemeOption(
                    label = stringResource(R.string.light),
                    selected = themeMode == ThemeMode.LIGHT,
                    onClick = { onThemeChange(ThemeMode.LIGHT) }
                )

                ThemeOption(
                    label = stringResource(R.string.dark),
                    selected = themeMode == ThemeMode.DARK,
                    onClick = { onThemeChange(ThemeMode.DARK) }
                )
            }
        }

        // ===== LANGUAGE =====
        AppSectionCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.language),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = stringResource(R.string.language_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                ThemeOption(
                    label = stringResource(R.string.system_default),
                    selected = language == AppLanguage.SYSTEM,
                    onClick = { onLanguageChange(AppLanguage.SYSTEM) }
                )

                ThemeOption(
                    label = stringResource(R.string.english),
                    selected = language == AppLanguage.ENGLISH,
                    onClick = { onLanguageChange(AppLanguage.ENGLISH) }
                )

                ThemeOption(
                    label = stringResource(R.string.slovak),
                    selected = language == AppLanguage.SLOVAK,
                    onClick = { onLanguageChange(AppLanguage.SLOVAK) }
                )
            }
        }

        // ===== GRAPH COLORS =====
        val speedArgb = graphColors[GraphTypeKey.SPEED.name] ?: 0xFF1E88E5.toInt()
        val rpmArgb = graphColors[GraphTypeKey.RPM.name] ?: 0xFFD32F2F.toInt()
        val coolantArgb = graphColors[GraphTypeKey.COOLANT.name] ?: 0xFFF57C00.toInt()
        val throttleArgb = graphColors[GraphTypeKey.THROTTLE.name] ?: 0xFF8E24AA.toInt()
        val acceleratorPedalArgb = graphColors[GraphTypeKey.ACCELERATOR_PEDAL.name] ?: 0xFF00ACC1.toInt()
        val boostArgb = graphColors[GraphTypeKey.BOOST.name] ?: 0xFF2E7D32.toInt()
        val mapArgb = graphColors[GraphTypeKey.MAP.name] ?: 0xFF00838F.toInt()
        val baroArgb = graphColors[GraphTypeKey.BARO.name] ?: 0xFF607D8B.toInt()
        val mafArgb = graphColors[GraphTypeKey.MAF.name] ?: 0xFF7B1FA2.toInt()
        val iatArgb = graphColors[GraphTypeKey.IAT.name] ?: 0xFF3949AB.toInt()
        val timingAdvanceArgb = graphColors[GraphTypeKey.TIMING_ADVANCE.name] ?: 0xFFC2185B.toInt()
        val fuelLevelArgb = graphColors[GraphTypeKey.FUEL_LEVEL.name] ?: 0xFF00897B.toInt()
        val fuelConsumptionArgb = graphColors[GraphTypeKey.FUEL_CONSUMPTION.name] ?: 0xFF6D4C41.toInt()
        val fuelRateArgb = graphColors[GraphTypeKey.FUEL_RATE.name] ?: 0xFF546E7A.toInt()
        val engineLoadArgb = graphColors[GraphTypeKey.ENGINE_LOAD.name] ?: 0xFF00897B.toInt()
        val fuelPressureArgb = graphColors[GraphTypeKey.FUEL_PRESSURE.name] ?: 0xFFFFA000.toInt()
        val voltageArgb = graphColors[GraphTypeKey.VOLTAGE.name] ?: 0xFFFDD835.toInt()

        val palette = listOf(
            Color(0xFF1E88E5),
            Color(0xFFD32F2F),
            Color(0xFFF57C00),
            Color(0xFF8E24AA),
            Color(0xFF2E7D32),
            Color(0xFF00897B),
            Color(0xFF5E35B1),
            Color(0xFF546E7A),
            Color(0xFF6D4C41),
            Color(0xFF3949AB),
            Color(0xFF00838F),
            Color(0xFF7B1FA2),
            Color(0xFFC2185B),
            Color(0xFF43A047),
            Color(0xFF6A1B9A),
            Color(0xFFEF6C00),
            Color(0xFF89AFD5)
        )

        fun setGraphColor(type: GraphTypeKey, color: Color) {
            onGraphColorChange(type, color.toArgb())
        }

        AppSectionCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.graph_colors),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = stringResource(R.string.graph_colors_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GraphTypeKey.values().forEach { type ->
                        FilterChip(
                            selected = selectedGraphColorKey == type,
                            onClick = { selectedGraphColorKey = type },
                            label = { Text(graphTypeLabel(type)) },
                            border = BorderStroke(
                                1.dp,
                                if (selectedGraphColorKey == type)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outlineVariant
                            ),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.15f
                                ),
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = graphTypeLabel(selectedGraphColorKey),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                AppInnerBlock {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        palette.forEach { c ->
                            val selectedArgb = when (selectedGraphColorKey) {
                                GraphTypeKey.SPEED -> speedArgb
                                GraphTypeKey.RPM -> rpmArgb
                                GraphTypeKey.COOLANT -> coolantArgb
                                GraphTypeKey.THROTTLE -> throttleArgb
                                GraphTypeKey.ACCELERATOR_PEDAL -> acceleratorPedalArgb
                                GraphTypeKey.BOOST -> boostArgb
                                GraphTypeKey.MAP -> mapArgb
                                GraphTypeKey.BARO -> baroArgb
                                GraphTypeKey.MAF -> mafArgb
                                GraphTypeKey.IAT -> iatArgb
                                GraphTypeKey.TIMING_ADVANCE -> timingAdvanceArgb
                                GraphTypeKey.FUEL_LEVEL -> fuelLevelArgb
                                GraphTypeKey.FUEL_CONSUMPTION -> fuelConsumptionArgb
                                GraphTypeKey.FUEL_RATE -> fuelRateArgb
                                GraphTypeKey.ENGINE_LOAD -> engineLoadArgb
                                GraphTypeKey.FUEL_PRESSURE -> fuelPressureArgb
                                GraphTypeKey.VOLTAGE -> voltageArgb

                            }

                            ColorOption(
                                color = c,
                                selected = c.toArgb() == selectedArgb,
                                onClick = { setGraphColor(selectedGraphColorKey, c) }
                            )
                        }
                    }
                }
            }
        }

        // ===== LOGOUT =====
        AppSectionCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.account),
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.sign_out))
                }
            }
        }
    }
}

@Composable
fun ThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun graphTypeLabel(type: GraphTypeKey): String {
    return when (type) {
        GraphTypeKey.SPEED -> stringResource(R.string.speed)
        GraphTypeKey.RPM -> stringResource(R.string.rpm)
        GraphTypeKey.COOLANT -> stringResource(R.string.coolant)
        GraphTypeKey.THROTTLE -> stringResource(R.string.throttle)
        GraphTypeKey.ACCELERATOR_PEDAL -> stringResource(R.string.accelerator_pedal)
        GraphTypeKey.BOOST -> stringResource(R.string.boost)
        GraphTypeKey.MAP -> stringResource(R.string.map)
        GraphTypeKey.BARO -> stringResource(R.string.baro)
        GraphTypeKey.MAF -> stringResource(R.string.maf)
        GraphTypeKey.IAT -> stringResource(R.string.iat)
        GraphTypeKey.TIMING_ADVANCE -> stringResource(R.string.timing_advance)
        GraphTypeKey.FUEL_LEVEL -> stringResource(R.string.fuel_level)
        GraphTypeKey.FUEL_CONSUMPTION -> stringResource(R.string.fuel_consumption)
        GraphTypeKey.FUEL_RATE -> stringResource(R.string.fuel_rate)
        GraphTypeKey.ENGINE_LOAD -> stringResource(R.string.engine_load)
        GraphTypeKey.FUEL_PRESSURE -> stringResource(R.string.fuel_pressure)
        GraphTypeKey.VOLTAGE -> stringResource(R.string.voltage)
    }
}