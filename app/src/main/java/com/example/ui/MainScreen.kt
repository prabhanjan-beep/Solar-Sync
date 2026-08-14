package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.SolarSyncViewModel

data class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val pageIndex: Int
)

val navItems = listOf(
    NavItem("Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard, 0),
    NavItem("Storage", Icons.Default.Thermostat, Icons.Outlined.Thermostat, 1),
    NavItem("Energy", Icons.Default.WbSunny, Icons.Outlined.WbSunny, 2),
    NavItem("Inventory", Icons.Default.Agriculture, Icons.Outlined.Agriculture, 3),
    NavItem("AI Insights", Icons.Default.AutoAwesome, Icons.Outlined.AutoAwesome, 4),
    NavItem("Alerts", Icons.Default.Notifications, Icons.Outlined.Notifications, 5),
    NavItem("Settings", Icons.Default.Settings, Icons.Outlined.Settings, 6)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: SolarSyncViewModel
) {
    val activePageIndex by viewModel.selectedPageIndex.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()
    val alerts by viewModel.alerts.collectAsState()

    val unreadAlertsCount = alerts.count { !it.isAcknowledged }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 600.dp

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AgriGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.solarsync_icon_fg_1786640373119),
                                    contentDescription = "SolarSync Logo",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "SolarSync",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        // Battery Quick Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SolarYellowContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = SolarYellowPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${sensorData.batteryLevel.toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SolarYellowPrimary
                                )
                            }
                        }

                        // Alerts bell icon
                        IconButton(onClick = { viewModel.selectPage(5) }) {
                            BadgedBox(
                                badge = {
                                    if (unreadAlertsCount > 0) {
                                        Badge(containerColor = StatusAlertRed) {
                                            Text(unreadAlertsCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (activePageIndex == 5) Icons.Default.Notifications else Icons.Outlined.Notifications,
                                    contentDescription = "Alerts"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 3.dp
                    ) {
                        ScrollableTabRow(
                            selectedTabIndex = activePageIndex,
                            containerColor = MaterialTheme.colorScheme.surface,
                            edgePadding = 8.dp,
                            divider = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            navItems.forEach { item ->
                                val isSelected = activePageIndex == item.pageIndex
                                Tab(
                                    selected = isSelected,
                                    onClick = { viewModel.selectPage(item.pageIndex) },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = item.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                    },
                                    selectedContentColor = MaterialTheme.colorScheme.primary,
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Navigation Rail for Wide Screens
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        navItems.forEach { item ->
                            val isSelected = activePageIndex == item.pageIndex
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { viewModel.selectPage(item.pageIndex) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(item.title, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Page Content Switcher
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (activePageIndex) {
                        0 -> DashboardScreen(viewModel = viewModel, onNavigateToPage = { viewModel.selectPage(it) })
                        1 -> StorageMonitoringScreen(viewModel = viewModel)
                        2 -> EnergyAnalyticsScreen(viewModel = viewModel)
                        3 -> InventoryScreen(viewModel = viewModel)
                        4 -> AiInsightsScreen(viewModel = viewModel)
                        5 -> AlertsScreen(viewModel = viewModel)
                        6 -> SettingsScreen(viewModel = viewModel)
                        else -> DashboardScreen(viewModel = viewModel, onNavigateToPage = { viewModel.selectPage(it) })
                    }
                }
            }
        }
    }
}
