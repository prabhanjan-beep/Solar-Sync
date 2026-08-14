package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.SolarSyncViewModel

@Composable
fun SettingsScreen(
    viewModel: SolarSyncViewModel,
    modifier: Modifier = Modifier
) {
    var unitName by remember { mutableStateOf("SolarSync Alpha-01") }
    var locationName by remember { mutableStateOf("Green Valley Farm, Zone B") }
    var samplingRate by remember { mutableStateOf("5 Seconds (Live)") }
    var highTempAlert by remember { mutableStateOf("15.0°C") }
    var lowBatteryAlert by remember { mutableStateOf("20%") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Storage Unit Profile
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Storage Unit Configuration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = unitName,
                        onValueChange = { unitName = it },
                        label = { Text("Storage Unit Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        label = { Text("Farm Location Zone") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // Hardware Connectivity & Integration Card (Next Step readiness)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hardware & Cloud Connection Readiness",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Prepared for ESP32 microcontroller & Firebase cloud sync",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ListItem(
                        headlineContent = { Text("ESP32 Sensor Array (Bluetooth / Wi-Fi)") },
                        supportingContent = { Text("Mock telemetry active • Standby for physical ESP32 pairing") },
                        leadingContent = {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = SolarYellowPrimary)
                        },
                        trailingContent = {
                            Surface(shape = RoundedCornerShape(8.dp), color = SolarYellowContainer) {
                                Text(
                                    text = "READY",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = SolarYellowPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    )

                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Firebase Cloud Database") },
                        supportingContent = { Text("Local state active • Ready for step 2 Firebase setup") },
                        leadingContent = {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = CoolingBluePrimary)
                        },
                        trailingContent = {
                            Surface(shape = RoundedCornerShape(8.dp), color = CoolingBlueContainer) {
                                Text(
                                    text = "STANDBY",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = CoolingBluePrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    )
                }
            }
        }

        // Alert Thresholds Config
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Safety Thresholds & Sampling Rate",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = highTempAlert,
                            onValueChange = { highTempAlert = it },
                            label = { Text("High Temp Limit") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lowBatteryAlert,
                            onValueChange = { lowBatteryAlert = it },
                            label = { Text("Low Battery Limit") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Hardware Specification Sheet
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SolarSync Unit Hardware Specifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("• Solar Panel: 200W Monocrystalline High-Efficiency PV Module", style = MaterialTheme.typography.bodyMedium)
                        Text("• Battery Bank: 12.8V 100Ah Deep-Cycle LiFePO4 Battery Pack", style = MaterialTheme.typography.bodyMedium)
                        Text("• Cooling Module: Dual 12V Solid-State Peltier + Variable Compressor", style = MaterialTheme.typography.bodyMedium)
                        Text("• Sensor Suite: SHT31 Temp/Humidity, NDIR CO₂, Ethylene Gas", style = MaterialTheme.typography.bodyMedium)
                        Text("• Max Storage Capacity: 30.0 kg Produce", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // Reset Demo Simulation
        item {
            OutlinedButton(
                onClick = { viewModel.simulateAlertTrigger("RESET") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AgriGreenPrimary)
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset All Sensor Readings to Default")
            }
        }
    }
}
