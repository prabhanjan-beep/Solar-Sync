package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.ui.components.LineChartCard
import com.example.ui.components.MetricCard
import com.example.ui.theme.*
import com.example.viewmodel.SolarSyncViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: SolarSyncViewModel,
    onNavigateToPage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val tempSeries by viewModel.temperatureSeries.collectAsState()
    val humSeries by viewModel.humiditySeries.collectAsState()
    val solarSeries by viewModel.solarPowerSeries.collectAsState()
    val batterySeries by viewModel.batteryLevelSeries.collectAsState()
    val weightSeries by viewModel.produceWeightSeries.collectAsState()
    val aiInsight by viewModel.aiInsight.collectAsState()
    val alerts by viewModel.alerts.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.solarsync_hero_banner_1786640388121),
                        contentDescription = "SolarSync Storage Unit",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = StatusOkGreen
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Text(
                                        text = "Unit 01 • Solar Online",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.toggleCooling() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AcUnit,
                                    contentDescription = "Cooling Toggle",
                                    tint = if (sensorData.coolingStatus == CoolingMode.ACTIVE) CoolingBlueSecondary else Color.White
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "SolarSync Smart Storage",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Green Valley Farm • Zone B Chamber",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Core Metrics
        item {
            Text(
                text = "Live Sensor Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 9 Requested Cards Grid
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1: Temperature, Humidity, Battery Level
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Temperature",
                        value = "${sensorData.temperature}°C",
                        subtitle = "Target: ${sensorData.targetTemperature}°C",
                        icon = Icons.Default.Thermostat,
                        accentColor = CoolingBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Humidity",
                        value = "${sensorData.humidity.toInt()}%",
                        subtitle = "Target: ${sensorData.targetHumidity.toInt()}%",
                        icon = Icons.Default.WaterDrop,
                        accentColor = CoolingBlueSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Battery Level",
                        value = "${sensorData.batteryLevel.toInt()}%",
                        subtitle = if (sensorData.batteryLevel > 20) "Charging" else "Low Battery",
                        icon = Icons.Default.BatteryChargingFull,
                        accentColor = SolarYellowPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Solar Power, Produce Weight, Storage Health
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Solar Power",
                        value = "${sensorData.solarPower.toInt()} W",
                        subtitle = "Peak Today: 180W",
                        icon = Icons.Default.WbSunny,
                        accentColor = SolarYellowSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Produce Weight",
                        value = "${sensorData.produceWeight} kg",
                        subtitle = "4 Batches Stored",
                        icon = Icons.Default.Scale,
                        accentColor = AgriGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Storage Health",
                        value = "${sensorData.storageHealth.toInt()}%",
                        subtitle = "System Optimal",
                        icon = Icons.Default.HealthAndSafety,
                        accentColor = StatusOkGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Spoilage Risk, Estimated Shelf Life, Cooling Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Spoilage Risk",
                        value = sensorData.spoilageRisk.label,
                        subtitle = "Optimal Climate",
                        icon = Icons.Default.Shield,
                        accentColor = when (sensorData.spoilageRisk) {
                            SpoilageRiskLevel.LOW -> StatusOkGreen
                            SpoilageRiskLevel.MEDIUM -> StatusWarningYellow
                            SpoilageRiskLevel.HIGH -> StatusAlertRed
                        },
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Shelf Life",
                        value = "${sensorData.estimatedShelfLifeDays} days",
                        subtitle = "Remaining",
                        icon = Icons.Default.HourglassTop,
                        accentColor = AgriGreenSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Cooling Status",
                        value = sensorData.coolingStatus.label,
                        subtitle = "Fan: ${sensorData.fanRpm} RPM",
                        icon = Icons.Default.AcUnit,
                        accentColor = CoolingBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section Title: AI Insights Card (Requested Section)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Insights",
                                tint = AgriGreenPrimary
                            )
                            Text(
                                text = "SolarSync AI Storage Insights",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        TextButton(onClick = { onNavigateToPage(4) }) {
                            Text("View All Insights →", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Freshness Score",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${aiInsight.freshnessScore}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = StatusOkGreen
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(36.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Spoilage Risk",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = aiInsight.spoilageRisk.label,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = StatusOkGreen
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(36.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Estimated Shelf Life",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${aiInsight.estimatedShelfLifeDays} days",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TipsAndUpdates,
                                contentDescription = "Recommendation",
                                tint = SolarYellowPrimary
                            )
                            Column {
                                Text(
                                    text = "Recommendation",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = aiInsight.recommendation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Requested Charts
        item {
            Text(
                text = "Environmental & Solar Trends",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 1. Temperature over time chart
        item {
            LineChartCard(
                title = "Temperature over time",
                subtitle = "Chamber 01 target range 11°C - 13°C",
                dataPoints = tempSeries,
                lineColor = CoolingBluePrimary,
                unit = "°C"
            )
        }

        // 2. Humidity over time chart
        item {
            LineChartCard(
                title = "Humidity over time",
                subtitle = "Target relative humidity 75% - 80%",
                dataPoints = humSeries,
                lineColor = CoolingBlueSecondary,
                unit = "%"
            )
        }

        // 3. Solar power generation chart
        item {
            LineChartCard(
                title = "Solar power generation",
                subtitle = "Real-time array generation curve (Watts)",
                dataPoints = solarSeries,
                lineColor = SolarYellowPrimary,
                fillGradientColors = listOf(SolarYellowPrimary.copy(alpha = 0.4f), SolarYellowPrimary.copy(alpha = 0.05f)),
                unit = "W"
            )
        }

        // 4. Battery level chart
        item {
            LineChartCard(
                title = "Battery level",
                subtitle = "LiFePO4 storage state-of-charge (%)",
                dataPoints = batterySeries,
                lineColor = SolarYellowSecondary,
                unit = "%"
            )
        }

        // 5. Produce weight chart
        item {
            LineChartCard(
                title = "Produce weight",
                subtitle = "Storage inventory weight trajectory (kg)",
                dataPoints = weightSeries,
                lineColor = AgriGreenPrimary,
                unit = "kg"
            )
        }

        // Section Title: Recent Alerts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent System Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { onNavigateToPage(5) }) {
                    Text("Alert Center (${alerts.size})")
                }
            }
        }

        items(alerts.take(3)) { alert ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                when (alert.severity) {
                                    AlertSeverity.CRITICAL -> StatusAlertRed.copy(alpha = 0.15f)
                                    AlertSeverity.WARNING -> StatusWarningYellow.copy(alpha = 0.15f)
                                    AlertSeverity.INFO -> CoolingBluePrimary.copy(alpha = 0.15f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (alert.severity) {
                                AlertSeverity.CRITICAL -> Icons.Default.Warning
                                AlertSeverity.WARNING -> Icons.Default.NotificationsActive
                                AlertSeverity.INFO -> Icons.Default.Info
                            },
                            contentDescription = alert.title,
                            tint = when (alert.severity) {
                                AlertSeverity.CRITICAL -> StatusAlertRed
                                AlertSeverity.WARNING -> StatusWarningYellow
                                AlertSeverity.INFO -> CoolingBluePrimary
                            }
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = alert.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            alert.chamber?.let {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = alert.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = alert.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
