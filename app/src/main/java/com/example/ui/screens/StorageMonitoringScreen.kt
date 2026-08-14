package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoolingMode
import com.example.ui.components.CircularGauge
import com.example.ui.components.LineChartCard
import com.example.ui.theme.*
import com.example.viewmodel.SolarSyncViewModel

@Composable
fun StorageMonitoringScreen(
    viewModel: SolarSyncViewModel,
    modifier: Modifier = Modifier
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val tempSeries by viewModel.temperatureSeries.collectAsState()
    val humSeries by viewModel.humiditySeries.collectAsState()

    var selectedChamber by remember { mutableStateOf("Chamber 01") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chamber Selection Tabs
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Chamber 01 (Tomatoes & Apples)", "Chamber 02 (Greens & Berries)").forEach { chamber ->
                        val isSelected = selectedChamber.startsWith(chamber.take(10))
                        Button(
                            onClick = { selectedChamber = chamber },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(
                                text = chamber.takeWhile { it != '(' }.trim(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Environmental Dial Gauges
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "$selectedChamber Climate Control",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time telemetry and target setpoints",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularGauge(
                            value = (sensorData.temperature / 30f) * 100f,
                            label = "Temperature",
                            valueText = "${sensorData.temperature}°C",
                            statusText = "Target: ${sensorData.targetTemperature}°C",
                            gaugeColor = CoolingBluePrimary
                        )

                        CircularGauge(
                            value = sensorData.humidity,
                            label = "Humidity",
                            valueText = "${sensorData.humidity.toInt()}%",
                            statusText = "Target: ${sensorData.targetHumidity.toInt()}%",
                            gaugeColor = CoolingBlueSecondary
                        )
                    }
                }
            }
        }

        // Target Adjustment Sliders
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Set Environmental Targets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Target Temp Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Target Temperature",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${sensorData.targetTemperature}°C",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CoolingBluePrimary
                            )
                        }
                        Slider(
                            value = sensorData.targetTemperature,
                            onValueChange = { viewModel.setTargetTemperature(Math.round(it * 2) / 2.0f) },
                            valueRange = 4.0f..20.0f,
                            steps = 31,
                            colors = SliderDefaults.colors(
                                thumbColor = CoolingBluePrimary,
                                activeTrackColor = CoolingBluePrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Target Humidity Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Target Humidity",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${sensorData.targetHumidity.toInt()}% RH",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CoolingBlueSecondary
                            )
                        }
                        Slider(
                            value = sensorData.targetHumidity,
                            onValueChange = { viewModel.setTargetHumidity(Math.round(it).toFloat()) },
                            valueRange = 50.0f..95.0f,
                            steps = 45,
                            colors = SliderDefaults.colors(
                                thumbColor = CoolingBlueSecondary,
                                activeTrackColor = CoolingBlueSecondary
                            )
                        )
                    }
                }
            }
        }

        // Hardware Controls & Gas Diagnostics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ventilation Control Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Air, contentDescription = "Ventilation", tint = AgriGreenPrimary)
                            Text(
                                text = "Ventilation Flap",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (sensorData.isVentOpen) "STATUS: OPEN" else "STATUS: CLOSED",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (sensorData.isVentOpen) StatusOkGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.toggleVent() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (sensorData.isVentOpen) AgriGreenPrimary else MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = if (sensorData.isVentOpen) "Close Vent" else "Open Vent",
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Ethylene & CO2 Gas Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Co2, contentDescription = "Gas Sensors", tint = SolarYellowPrimary)
                            Text(
                                text = "Gas Diagnostics",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ethylene: ${sensorData.ethylenePpm} ppm",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "CO₂ Level: ${sensorData.co2Ppm.toInt()} ppm",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AgriGreenContainer
                        ) {
                            Text(
                                text = "Respiration Normal",
                                style = MaterialTheme.typography.labelSmall,
                                color = AgriGreenPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Charts: Detailed 24h Trends
        item {
            LineChartCard(
                title = "24h Chamber Temperature Curve",
                subtitle = "Chamber 01 internal ambient sensor readings",
                dataPoints = tempSeries,
                lineColor = CoolingBluePrimary,
                unit = "°C"
            )
        }

        item {
            LineChartCard(
                title = "24h Chamber Relative Humidity Curve",
                subtitle = "Monitored via high-accuracy SHT31 sensor",
                dataPoints = humSeries,
                lineColor = CoolingBlueSecondary,
                unit = "%"
            )
        }
    }
}
