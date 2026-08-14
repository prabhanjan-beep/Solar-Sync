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
import com.example.ui.components.CircularGauge
import com.example.ui.components.LineChartCard
import com.example.ui.components.MetricCard
import com.example.ui.theme.*
import com.example.viewmodel.SolarSyncViewModel

@Composable
fun EnergyAnalyticsScreen(
    viewModel: SolarSyncViewModel,
    modifier: Modifier = Modifier
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val solarSeries by viewModel.solarPowerSeries.collectAsState()
    val batterySeries by viewModel.batteryLevelSeries.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Solar Array Hero Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SolarYellowContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = "Solar Energy",
                                    tint = SolarYellowPrimary
                                )
                            }
                            Column {
                                Text(
                                    text = "Solar Power & Energy Analytics",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "200W Monocrystalline PV Array • 12V LiFePO4",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = StatusOkGreen
                        ) {
                            Text(
                                text = "100% Off-Grid",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CircularGauge(
                            value = (sensorData.solarPower / 200f) * 100f,
                            label = "Solar Power",
                            valueText = "${sensorData.solarPower.toInt()} W",
                            statusText = "Peak: 180 W",
                            gaugeColor = SolarYellowPrimary
                        )

                        CircularGauge(
                            value = sensorData.batteryLevel,
                            label = "Battery SOC",
                            valueText = "${sensorData.batteryLevel.toInt()}%",
                            statusText = "13.4V (Healthy)",
                            gaugeColor = SolarYellowSecondary
                        )
                    }
                }
            }
        }

        // Energy Key Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Solar Output",
                    value = "${sensorData.solarPower.toInt()} W",
                    subtitle = "1.25 kWh Total Today",
                    icon = Icons.Default.SolarPower,
                    accentColor = SolarYellowPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Battery Charge",
                    value = "${sensorData.batteryLevel.toInt()}%",
                    subtitle = "Est. 18.5 hrs Backup",
                    icon = Icons.Default.BatteryChargingFull,
                    accentColor = SolarYellowSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Energy Management Strategy Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SolarYellowContainer.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Solar Strategy",
                        tint = SolarYellowPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Smart Solar Pre-Cooling Active",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "System leverages peak daytime solar power to lower storage temp to 10.5°C, reducing battery drain during overnight hours.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Charts: Solar Power Generation & Battery SOC Profile
        item {
            LineChartCard(
                title = "Solar Power Generation",
                subtitle = "Hourly PV output power (Watts)",
                dataPoints = solarSeries,
                lineColor = SolarYellowPrimary,
                fillGradientColors = listOf(SolarYellowPrimary.copy(alpha = 0.45f), SolarYellowPrimary.copy(alpha = 0.05f)),
                unit = "W"
            )
        }

        item {
            LineChartCard(
                title = "Battery State-of-Charge Profile",
                subtitle = "24h battery discharge/charge curve",
                dataPoints = batterySeries,
                lineColor = SolarYellowSecondary,
                unit = "%"
            )
        }
    }
}
