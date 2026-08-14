package com.example.data

import java.util.UUID

enum class SpoilageRiskLevel(val label: String) {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH")
}

enum class CoolingMode(val label: String) {
    ACTIVE("ACTIVE"),
    ECO("ECO"),
    STANDBY("STANDBY"),
    DEFROST("DEFROST")
}

data class TimeSeriesPoint(
    val timeLabel: String,
    val value: Float,
    val secondaryValue: Float? = null
)

data class SensorData(
    val temperature: Float = 12.4f,        // °C
    val targetTemperature: Float = 12.0f,  // °C
    val humidity: Float = 78.0f,           // %
    val targetHumidity: Float = 75.0f,     // %
    val batteryLevel: Float = 84.0f,       // %
    val solarPower: Float = 42.0f,         // W
    val peakSolarPowerToday: Float = 180.0f, // W
    val totalSolarEnergyToday: Float = 1.25f, // kWh
    val produceWeight: Float = 12.5f,      // kg
    val storageHealth: Float = 96.0f,      // %
    val spoilageRisk: SpoilageRiskLevel = SpoilageRiskLevel.LOW,
    val estimatedShelfLifeDays: Int = 9,
    val coolingStatus: CoolingMode = CoolingMode.ACTIVE,
    val isDoorOpen: Boolean = false,
    val isVentOpen: Boolean = false,
    val ethylenePpm: Float = 0.12f,
    val co2Ppm: Float = 410.0f,
    val fanRpm: Int = 2450
)

data class InventoryItem(
    val id: String = UUID.randomUUID().toString(),
    val cropName: String,
    val category: String,
    val weightKg: Float,
    val chamber: String,
    val storedDate: String,
    val daysInStorage: Int,
    val shelfLifeRemainingDays: Int,
    val freshnessScore: Int, // 0 - 100
    val targetTemp: String,
    val targetHumidity: String
)

enum class AlertSeverity {
    CRITICAL, WARNING, INFO
}

data class AlertItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,
    val severity: AlertSeverity,
    val timestamp: String,
    val chamber: String? = null,
    val isAcknowledged: Boolean = false
)

data class AiInsight(
    val freshnessScore: Int = 94,
    val spoilageRisk: SpoilageRiskLevel = SpoilageRiskLevel.LOW,
    val estimatedShelfLifeDays: Int = 9,
    val recommendation: String = "Current storage conditions are suitable. Continue monitoring temperature and humidity.",
    val detailedTips: List<String> = listOf(
        "Peak solar radiation expected between 11:30 AM - 2:30 PM. System will automatically pre-cool chamber to 10.5°C.",
        "Chamber 01 produce weight is stable at 12.5 kg. No abnormal respiration weight loss detected.",
        "Ethylene accumulation remains low at 0.12 ppm. Ventilation doors are performing optimally."
    )
)

object MockDataGenerator {
    val initialSensorData = SensorData()

    val initialInventory = listOf(
        InventoryItem(
            cropName = "Tomatoes (Roma)",
            category = "Nightshade",
            weightKg = 6.5f,
            chamber = "Chamber 01",
            storedDate = "2 days ago",
            daysInStorage = 2,
            shelfLifeRemainingDays = 9,
            freshnessScore = 96,
            targetTemp = "11-13°C",
            targetHumidity = "75-80%"
        ),
        InventoryItem(
            cropName = "Golden Apples",
            category = "Pome Fruit",
            weightKg = 3.2f,
            chamber = "Chamber 01",
            storedDate = "4 days ago",
            daysInStorage = 4,
            shelfLifeRemainingDays = 14,
            freshnessScore = 92,
            targetTemp = "8-10°C",
            targetHumidity = "80-85%"
        ),
        InventoryItem(
            cropName = "Fresh Spinach",
            category = "Leafy Greens",
            weightKg = 1.8f,
            chamber = "Chamber 02",
            storedDate = "1 day ago",
            daysInStorage = 1,
            shelfLifeRemainingDays = 5,
            freshnessScore = 98,
            targetTemp = "4-6°C",
            targetHumidity = "85-90%"
        ),
        InventoryItem(
            cropName = "Strawberries",
            category = "Berries",
            weightKg = 1.0f,
            chamber = "Chamber 02",
            storedDate = "Today",
            daysInStorage = 0,
            shelfLifeRemainingDays = 6,
            freshnessScore = 100,
            targetTemp = "2-4°C",
            targetHumidity = "90-95%"
        )
    )

    val initialAlerts = listOf(
        AlertItem(
            title = "Temperature High",
            subtitle = "Cooling Activated",
            severity = AlertSeverity.WARNING,
            timestamp = "10 mins ago",
            chamber = "Chamber 01",
            isAcknowledged = false
        ),
        AlertItem(
            title = "Battery Low",
            subtitle = "Check Solar Charging",
            severity = AlertSeverity.WARNING,
            timestamp = "45 mins ago",
            chamber = "Power Unit",
            isAcknowledged = false
        ),
        AlertItem(
            title = "Door Open",
            subtitle = "Storage Unit 01",
            severity = AlertSeverity.CRITICAL,
            timestamp = "2 hours ago",
            chamber = "Storage Unit 01",
            isAcknowledged = true
        ),
        AlertItem(
            title = "Solar Input Peak",
            subtitle = "Generating 180W at max efficiency",
            severity = AlertSeverity.INFO,
            timestamp = "3 hours ago",
            chamber = "Solar Array",
            isAcknowledged = true
        )
    )

    // Time Series for Charts
    val temperatureSeries = listOf(
        TimeSeriesPoint("00:00", 12.8f),
        TimeSeriesPoint("03:00", 12.6f),
        TimeSeriesPoint("06:00", 12.5f),
        TimeSeriesPoint("09:00", 13.1f),
        TimeSeriesPoint("12:00", 12.4f),
        TimeSeriesPoint("15:00", 12.2f),
        TimeSeriesPoint("18:00", 12.5f),
        TimeSeriesPoint("21:00", 12.4f)
    )

    val humiditySeries = listOf(
        TimeSeriesPoint("00:00", 76f),
        TimeSeriesPoint("03:00", 77f),
        TimeSeriesPoint("06:00", 79f),
        TimeSeriesPoint("09:00", 81f),
        TimeSeriesPoint("12:00", 78f),
        TimeSeriesPoint("15:00", 76f),
        TimeSeriesPoint("18:00", 77f),
        TimeSeriesPoint("21:00", 78f)
    )

    val solarPowerSeries = listOf(
        TimeSeriesPoint("06:00", 10f),
        TimeSeriesPoint("08:00", 45f),
        TimeSeriesPoint("10:00", 120f),
        TimeSeriesPoint("12:00", 180f),
        TimeSeriesPoint("14:00", 165f),
        TimeSeriesPoint("16:00", 85f),
        TimeSeriesPoint("18:00", 42f),
        TimeSeriesPoint("20:00", 0f)
    )

    val batteryLevelSeries = listOf(
        TimeSeriesPoint("00:00", 65f),
        TimeSeriesPoint("03:00", 58f),
        TimeSeriesPoint("06:00", 52f),
        TimeSeriesPoint("09:00", 68f),
        TimeSeriesPoint("12:00", 92f),
        TimeSeriesPoint("15:00", 98f),
        TimeSeriesPoint("18:00", 88f),
        TimeSeriesPoint("21:00", 84f)
    )

    val produceWeightSeries = listOf(
        TimeSeriesPoint("Mon", 10.0f),
        TimeSeriesPoint("Tue", 10.0f),
        TimeSeriesPoint("Wed", 11.5f),
        TimeSeriesPoint("Thu", 11.5f),
        TimeSeriesPoint("Fri", 12.5f),
        TimeSeriesPoint("Sat", 12.5f),
        TimeSeriesPoint("Sun", 12.5f)
    )
}
