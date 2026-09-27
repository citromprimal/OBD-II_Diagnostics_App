package com.primal.cardiagnosticsapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.primal.cardiagnosticsapp.R
import com.primal.cardiagnosticsapp.StatusText
import com.primal.cardiagnosticsapp.ui.model.ConnectionStatus
import com.primal.cardiagnosticsapp.ui.model.GraphTypeKey
import kotlin.math.max
import kotlin.math.roundToInt

private enum class GraphType(
    val labelRes: Int,
    val unit: String,
    val minY: Float,
    val maxY: Float
) {
    SPEED(R.string.speed, "km/h", 0f, 200f),
    RPM(R.string.rpm, "rpm", 0f, 8000f),
    COOLANT(R.string.coolant, "°C", 0f, 130f),
    THROTTLE(R.string.throttle, "%", 0f, 100f),
    ACCELERATOR_PEDAL(R.string.accelerator_pedal, "%", 0f, 100f),
    BOOST(R.string.boost, "PSI", -10f, 30f),
    MAP(R.string.map, "kPa", 0f, 255f),
    BARO(R.string.baro, "kPa", 70f, 110f),
    MAF(R.string.maf, "g/s", 0f, 80f),
    IAT(R.string.iat, "°C", -20f, 100f),
    TIMING_ADVANCE(R.string.timing_advance, "°", -64f, 64f),
    FUEL_LEVEL(R.string.fuel_level, "%", 0f, 100f),
    FUEL_CONSUMPTION(R.string.fuel_consumption, "L/100km", 0f, 25f),
    FUEL_RATE(R.string.fuel_rate, "L/h", 0f, 15f),
    ENGINE_LOAD(R.string.engine_load, "%", 0f, 100f),
    FUEL_PRESSURE(R.string.fuel_pressure, "kPa", 0f, 1000f),
    VOLTAGE(R.string.voltage, "V", 0f, 20f)
}

@Composable
fun GraphsTab(
    status: ConnectionStatus,
    speed: List<Float>,
    rpm: List<Float>,
    coolant: List<Float>,
    throttle: List<Float>,
    acceleratorPedal: List<Float>,
    boost: List<Float>,
    map: List<Float>,
    baro: List<Float>,
    maf: List<Float>,
    iat: List<Float>,
    timingAdvance: List<Float>,
    fuelLevel: List<Float>,
    fuelConsumption: List<Float>,
    fuelRate: List<Float>,
    engineLoad: List<Float>,
    fuelPressure: List<Float>,
    voltage: List<Float>,
    isConnected: Boolean,
    isFrozen: Boolean,
    onFrozenChange: (Boolean) -> Unit,
    graphColors: Map<String, Int>
) {
    var selected by rememberSaveable {
        mutableStateOf(
            setOf(
                GraphType.SPEED.name,
                GraphType.RPM.name,
                GraphType.COOLANT.name,
                GraphType.THROTTLE.name,
                GraphType.ACCELERATOR_PEDAL.name,
                GraphType.BOOST.name,
                GraphType.MAP.name,
                GraphType.BARO.name,
                GraphType.MAF.name,
                GraphType.IAT.name,
                GraphType.TIMING_ADVANCE.name,
                GraphType.FUEL_LEVEL.name,
                GraphType.FUEL_CONSUMPTION.name,
                GraphType.FUEL_RATE.name,
                GraphType.ENGINE_LOAD.name,
                GraphType.FUEL_PRESSURE.name,
                GraphType.VOLTAGE.name
            )
        )
    }

    fun dataFor(type: GraphType): List<Float> = when (type) {
        GraphType.SPEED -> speed
        GraphType.RPM -> rpm
        GraphType.COOLANT -> coolant
        GraphType.THROTTLE -> throttle
        GraphType.ACCELERATOR_PEDAL -> acceleratorPedal
        GraphType.BOOST -> boost
        GraphType.MAP -> map
        GraphType.BARO -> baro
        GraphType.MAF -> maf
        GraphType.IAT -> iat
        GraphType.TIMING_ADVANCE -> timingAdvance
        GraphType.FUEL_LEVEL -> fuelLevel
        GraphType.FUEL_CONSUMPTION -> fuelConsumption
        GraphType.FUEL_RATE -> fuelRate
        GraphType.ENGINE_LOAD -> engineLoad
        GraphType.FUEL_PRESSURE -> fuelPressure
        GraphType.VOLTAGE -> voltage
    }

    fun colorFor(type: GraphType): Color = when (type) {
        GraphType.SPEED -> Color(graphColors[GraphTypeKey.SPEED.name] ?: 0xFF1E88E5.toInt())
        GraphType.RPM -> Color(graphColors[GraphTypeKey.RPM.name] ?: 0xFFD32F2F.toInt())
        GraphType.COOLANT -> Color(graphColors[GraphTypeKey.COOLANT.name] ?: 0xFFF57C00.toInt())
        GraphType.THROTTLE -> Color(graphColors[GraphTypeKey.THROTTLE.name] ?: 0xFF8E24AA.toInt())
        GraphType.ACCELERATOR_PEDAL -> Color(graphColors[GraphTypeKey.ACCELERATOR_PEDAL.name] ?: 0xFF00ACC1.toInt())
        GraphType.BOOST -> Color(graphColors[GraphTypeKey.BOOST.name] ?: 0xFF2E7D32.toInt())
        GraphType.MAP -> Color(graphColors[GraphTypeKey.MAP.name] ?: 0xFF00838F.toInt())
        GraphType.BARO -> Color(graphColors[GraphTypeKey.BARO.name] ?: 0xFF607D8B.toInt())
        GraphType.MAF -> Color(graphColors[GraphTypeKey.MAF.name] ?: 0xFF7B1FA2.toInt())
        GraphType.IAT -> Color(graphColors[GraphTypeKey.IAT.name] ?: 0xFF3949AB.toInt())
        GraphType.TIMING_ADVANCE -> Color(graphColors[GraphTypeKey.TIMING_ADVANCE.name] ?: 0xFFC2185B.toInt())
        GraphType.FUEL_LEVEL -> Color(graphColors[GraphTypeKey.FUEL_LEVEL.name] ?: 0xFF00897B.toInt())
        GraphType.FUEL_CONSUMPTION -> Color(graphColors[GraphTypeKey.FUEL_CONSUMPTION.name] ?: 0xFF6D4C41.toInt())
        GraphType.FUEL_RATE -> Color(graphColors[GraphTypeKey.FUEL_RATE.name] ?: 0xFF546E7A.toInt())
        GraphType.ENGINE_LOAD -> Color(graphColors[GraphTypeKey.ENGINE_LOAD.name] ?: 0xFF00897B.toInt())
        GraphType.FUEL_PRESSURE -> Color(graphColors[GraphTypeKey.FUEL_PRESSURE.name] ?: 0xFFFFA000.toInt())
        GraphType.VOLTAGE -> Color(graphColors[GraphTypeKey.VOLTAGE.name] ?: 0xFFFDD835.toInt())
    }

    val selectedTypes = GraphType.values()
        .filter { selected.contains(it.name) }

    Column(
        Modifier
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
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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

        AppSectionCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppInnerBlock(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.freeze_button),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = isFrozen,
                            onCheckedChange = onFrozenChange,
                            modifier = Modifier.scale(0.80f)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GraphType.values().forEach { type ->
                        val isSel = selected.contains(type.name)
                        FilterChip(
                            selected = isSel,
                            onClick = {
                                selected = if (isSel) selected - type.name else selected + type.name
                            },
                            label = {
                                Text(
                                    text = stringResource(type.labelRes),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    selectedTypes.forEach { type ->
                        AppLegendItem(
                            label = stringResource(type.labelRes),
                            color = colorFor(type)
                        )
                    }
                }
            }
        }

        if (selectedTypes.isEmpty()) {
            AppSectionCard {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.select_graph),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            return@Column
        }

        selectedTypes.forEach { type ->
            AppSectionCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(type.labelRes),
                        style = MaterialTheme.typography.titleSmall
                    )
                    LineChart(
                        values = dataFor(type),
                        minY = type.minY,
                        maxY = type.maxY,
                        unit = type.unit,
                        lineColor = colorFor(type),
                        isConnected = isConnected
                    )
                }
            }
        }
    }
}

@Composable
fun LineChart(
    values: List<Float>,
    minY: Float,
    maxY: Float,
    unit: String,
    lineColor: Color,
    modifier: Modifier = Modifier,
    isConnected: Boolean
) {
    val alpha = if (isConnected) 1f else 0.35f

    val chartHeight = 160.dp
    val gridSteps = 4

    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600),
        label = "lineAnim"
    )

    Row(modifier) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(chartHeight)
        ) {
            for (i in 0..gridSteps) {
                val fraction = i / gridSteps.toFloat()
                val value = maxY - (maxY - minY) * fraction

                Text(
                    text = "${value.toInt()} $unit",
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(y = chartHeight * fraction - 8.dp)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        var zoom by remember { mutableFloatStateOf(1f) }
        var offsetPoints by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(values.size) {
                        detectTransformGestures { _, pan, gestureZoom, _ ->
                            val newZoom = (zoom * gestureZoom).coerceIn(1f, 5f)
                            zoom = newZoom

                            val total = values.size
                            if (total >= 2) {
                                val visibleCount = max(2, (total / zoom).roundToInt())
                                val pxPerPoint = size.width / (visibleCount - 1).toFloat()

                                if (pxPerPoint > 0f) {
                                    offsetPoints += (-pan.x / pxPerPoint)
                                }

                                val maxOffset = (total - visibleCount).toFloat().coerceAtLeast(0f)
                                offsetPoints = offsetPoints.coerceIn(0f, maxOffset)
                            }
                        }
                    }
            ) {
                if (values.size < 2) return@Canvas

                val w = size.width
                val h = size.height
                val scaleY = h / (maxY - minY)

                val total = values.size
                val visibleCount = max(2, (total / zoom).roundToInt()).coerceAtMost(total)
                val maxOffset = (total - visibleCount).toFloat().coerceAtLeast(0f)
                val safeOffset = offsetPoints.coerceIn(0f, maxOffset)

                val startIndex = ((total - visibleCount) - safeOffset)
                    .roundToInt()
                    .coerceIn(0, total - visibleCount)

                val slice = values.subList(startIndex, startIndex + visibleCount)
                val stepX = w / (visibleCount - 1).toFloat()

                repeat(gridSteps + 1) { i ->
                    val y = size.height * i / gridSteps
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y)
                    )
                }

                val path = Path()
                slice.forEachIndexed { i, v ->
                    val x = stepX * i * animatedProgress
                    val y = h - (v - minY) * scaleY
                    if (i == 0) path.moveTo(x, y)
                    else path.lineTo(x, y)
                }

                drawPath(
                    path = path,
                    color = lineColor.copy(alpha = alpha),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            if (zoom > 1f || offsetPoints > 0f) {
                TextButton(
                    onClick = {
                        zoom = 1f
                        offsetPoints = 0f
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text("Reset")
                }
            }
        }
    }
}


@Composable
fun ColorOption(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
    )
}