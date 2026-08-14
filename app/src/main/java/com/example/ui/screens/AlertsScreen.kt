package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.AlertItem
import com.example.data.AlertSeverity
import com.example.ui.theme.*
import com.example.viewmodel.SolarSyncViewModel

@Composable
fun AlertsScreen(
    viewModel: SolarSyncViewModel,
    modifier: Modifier = Modifier
) {
    val alerts by viewModel.alerts.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredAlerts = remember(alerts, selectedFilter) {
        when (selectedFilter) {
            "Critical" -> alerts.filter { it.severity == AlertSeverity.CRITICAL }
            "Warnings" -> alerts.filter { it.severity == AlertSeverity.WARNING }
            "Info" -> alerts.filter { it.severity == AlertSeverity.INFO }
            else -> alerts
        }
    }

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
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "System Alerts & Logs",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${alerts.count { !it.isAcknowledged }} Active Unacknowledged Alerts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { viewModel.acknowledgeAllAlerts() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = "Acknowledge All",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Critical", "Warnings", "Info").forEach { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter) }
                            )
                        }
                    }
                }
            }
        }

        // Test Trigger Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Simulate Hardware Test Alerts",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.simulateAlertTrigger("TEMP_HIGH") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Temp High", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.simulateAlertTrigger("BATTERY_LOW") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Battery Low", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.simulateAlertTrigger("DOOR_OPEN") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Door Open", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Alerts List
        items(filteredAlerts, key = { it.id }) { alert ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (alert.isAcknowledged) MaterialTheme.colorScheme.surface
                    else when (alert.severity) {
                        AlertSeverity.CRITICAL -> StatusAlertRed.copy(alpha = 0.08f)
                        AlertSeverity.WARNING -> StatusWarningYellow.copy(alpha = 0.08f)
                        AlertSeverity.INFO -> CoolingBlueContainer.copy(alpha = 0.3f)
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                when (alert.severity) {
                                    AlertSeverity.CRITICAL -> StatusAlertRed.copy(alpha = 0.2f)
                                    AlertSeverity.WARNING -> StatusWarningYellow.copy(alpha = 0.2f)
                                    AlertSeverity.INFO -> CoolingBluePrimary.copy(alpha = 0.2f)
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
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            alert.chamber?.let {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
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
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = alert.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!alert.isAcknowledged) {
                        IconButton(onClick = { viewModel.acknowledgeAlert(alert.id) }) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Acknowledge",
                                tint = StatusOkGreen
                            )
                        }
                    } else {
                        Text(
                            text = "ACK",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
