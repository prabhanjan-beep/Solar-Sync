package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SolarSyncViewModel : ViewModel() {

    private val _sensorData = MutableStateFlow(MockDataGenerator.initialSensorData)
    val sensorData: StateFlow<SensorData> = _sensorData.asStateFlow()

    private val _temperatureSeries = MutableStateFlow(MockDataGenerator.temperatureSeries)
    val temperatureSeries: StateFlow<List<TimeSeriesPoint>> = _temperatureSeries.asStateFlow()

    private val _humiditySeries = MutableStateFlow(MockDataGenerator.humiditySeries)
    val humiditySeries: StateFlow<List<TimeSeriesPoint>> = _humiditySeries.asStateFlow()

    private val _solarPowerSeries = MutableStateFlow(MockDataGenerator.solarPowerSeries)
    val solarPowerSeries: StateFlow<List<TimeSeriesPoint>> = _solarPowerSeries.asStateFlow()

    private val _batteryLevelSeries = MutableStateFlow(MockDataGenerator.batteryLevelSeries)
    val batteryLevelSeries: StateFlow<List<TimeSeriesPoint>> = _batteryLevelSeries.asStateFlow()

    private val _produceWeightSeries = MutableStateFlow(MockDataGenerator.produceWeightSeries)
    val produceWeightSeries: StateFlow<List<TimeSeriesPoint>> = _produceWeightSeries.asStateFlow()

    private val _inventory = MutableStateFlow(MockDataGenerator.initialInventory)
    val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()

    private val _alerts = MutableStateFlow(MockDataGenerator.initialAlerts)
    val alerts: StateFlow<List<AlertItem>> = _alerts.asStateFlow()

    private val _aiInsight = MutableStateFlow(AiInsight())
    val aiInsight: StateFlow<AiInsight> = _aiInsight.asStateFlow()

    private val _selectedPageIndex = MutableStateFlow(0)
    val selectedPageIndex: StateFlow<Int> = _selectedPageIndex.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            "Hello Farmer! I am your SolarSync AI Storage Assistant. How can I assist with your produce storage today?"
        )
    )
    val chatMessages: StateFlow<List<String>> = _chatMessages.asStateFlow()

    init {
        // Subtle simulated live sensor drift for realistic farmer dashboard experience
        viewModelScope.launch {
            while (true) {
                delay(5000)
                updateSensorDrift()
            }
        }
    }

    fun selectPage(index: Int) {
        _selectedPageIndex.value = index
    }

    fun toggleCooling() {
        _sensorData.value = _sensorData.value.let { current ->
            val nextStatus = if (current.coolingStatus == CoolingMode.ACTIVE) CoolingMode.ECO else CoolingMode.ACTIVE
            current.copy(coolingStatus = nextStatus)
        }
    }

    fun setTargetTemperature(target: Float) {
        _sensorData.value = _sensorData.value.copy(targetTemperature = target)
    }

    fun setTargetHumidity(target: Float) {
        _sensorData.value = _sensorData.value.copy(targetHumidity = target)
    }

    fun toggleVent() {
        _sensorData.value = _sensorData.value.let { current ->
            current.copy(isVentOpen = !current.isVentOpen)
        }
    }

    fun addInventoryItem(cropName: String, category: String, weightKg: Float, chamber: String) {
        val newItem = InventoryItem(
            cropName = cropName,
            category = category,
            weightKg = weightKg,
            chamber = chamber,
            storedDate = "Today",
            daysInStorage = 0,
            shelfLifeRemainingDays = 10,
            freshnessScore = 100,
            targetTemp = "10-12°C",
            targetHumidity = "75-80%"
        )
        _inventory.value = listOf(newItem) + _inventory.value
        recalculateProduceWeight()
    }

    fun removeInventoryItem(id: String) {
        _inventory.value = _inventory.value.filterNot { it.id == id }
        recalculateProduceWeight()
    }

    private fun recalculateProduceWeight() {
        val totalKg = _inventory.value.sumOf { it.weightKg.toDouble() }.toFloat()
        _sensorData.value = _sensorData.value.copy(produceWeight = totalKg)
    }

    fun acknowledgeAlert(id: String) {
        _alerts.value = _alerts.value.map {
            if (it.id == id) it.copy(isAcknowledged = true) else it
        }
    }

    fun acknowledgeAllAlerts() {
        _alerts.value = _alerts.value.map { it.copy(isAcknowledged = true) }
    }

    fun simulateAlertTrigger(type: String) {
        when (type) {
            "TEMP_HIGH" -> {
                _sensorData.value = _sensorData.value.copy(
                    temperature = 18.2f,
                    spoilageRisk = SpoilageRiskLevel.MEDIUM,
                    coolingStatus = CoolingMode.ACTIVE
                )
                val newAlert = AlertItem(
                    title = "Temperature High",
                    subtitle = "Cooling Activated (18.2°C Detected)",
                    severity = AlertSeverity.WARNING,
                    timestamp = "Just now",
                    chamber = "Chamber 01"
                )
                _alerts.value = listOf(newAlert) + _alerts.value
            }
            "BATTERY_LOW" -> {
                _sensorData.value = _sensorData.value.copy(batteryLevel = 18.0f)
                val newAlert = AlertItem(
                    title = "Battery Low",
                    subtitle = "Check Solar Charging (18% Remaining)",
                    severity = AlertSeverity.WARNING,
                    timestamp = "Just now",
                    chamber = "Power Unit"
                )
                _alerts.value = listOf(newAlert) + _alerts.value
            }
            "DOOR_OPEN" -> {
                _sensorData.value = _sensorData.value.copy(isDoorOpen = true)
                val newAlert = AlertItem(
                    title = "Door Open",
                    subtitle = "Storage Unit 01 Left Unlocked",
                    severity = AlertSeverity.CRITICAL,
                    timestamp = "Just now",
                    chamber = "Storage Unit 01"
                )
                _alerts.value = listOf(newAlert) + _alerts.value
            }
            "RESET" -> {
                _sensorData.value = MockDataGenerator.initialSensorData
            }
        }
    }

    fun askAiAssistant(question: String) {
        if (question.isBlank()) return
        val userText = "Farmer: $question"
        val response = when {
            question.contains("temp", ignoreCase = true) ->
                "SolarSync AI: The current chamber temp is 12.4°C. For tomatoes and tropical crops, maintaining 11-13°C prevents chilling injury while extending shelf life by up to 9 days."
            question.contains("humidity", ignoreCase = true) ->
                "SolarSync AI: Relative humidity is at 78%. Optimal range for non-leafy crops is 75-80%. If condensation rises, open ventilation for 10 minutes."
            question.contains("battery", ignoreCase = true) or question.contains("solar", ignoreCase = true) ->
                "SolarSync AI: Current battery level is 84% with 42W solar input. Solar generation will peak at ~180W midday, automatically charging battery to 100%."
            else ->
                "SolarSync AI: Based on your current sensor telemetry (12.4°C, 78% RH, 84% Battery), your produce freshness score is 94%. Continue standard storage mode."
        }
        _chatMessages.value = _chatMessages.value + userText + response
    }

    private fun updateSensorDrift() {
        val current = _sensorData.value
        // Slight natural fluctuation around 12.4°C and 78%
        val tempDrift = ((-1..1).random() * 0.1f)
        val humDrift = ((-1..1).random() * 0.2f)

        val newTemp = (current.temperature + tempDrift).coerceIn(10.5f, 15.0f)
        val newHum = (current.humidity + humDrift).coerceIn(70.0f, 85.0f)

        _sensorData.value = current.copy(
            temperature = (Math.round(newTemp * 10) / 10.0f),
            humidity = (Math.round(newHum * 10) / 10.0f)
        )
    }
}
