package com.example.data.scada

import com.example.data.model.ScadaProtocolType
import com.example.data.model.ScadaTelemetryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

object ScadaTelemetryManager {

    private val _liveTelemetry = MutableStateFlow<Map<String, ScadaTelemetryEntity>>(emptyMap())
    val liveTelemetry: StateFlow<Map<String, ScadaTelemetryEntity>> = _liveTelemetry.asStateFlow()

    private val _isLiveFeedActive = MutableStateFlow(true)
    val isLiveFeedActive: StateFlow<Boolean> = _isLiveFeedActive.asStateFlow()

    private val _activeProtocol = MutableStateFlow(ScadaProtocolType.MODBUS_TCP)
    val activeProtocol: StateFlow<ScadaProtocolType> = _activeProtocol.asStateFlow()

    private var streamingJob: Job? = null

    init {
        // Initialize baseline operational parameters for primary equipment
        val baseline = mapOf(
            "132-TR-01" to ScadaTelemetryEntity(
                equipmentId = 1L,
                equipmentTag = "132-TR-01",
                equipmentName = "132/33 kV 40 MVA Transformer",
                timestamp = "2026-09-25 09:34:00 UTC",
                voltageKv = 33.2,
                currentPhaseA = 582.4,
                currentPhaseB = 579.8,
                currentPhaseC = 585.1,
                activePowerMw = 31.4,
                reactivePowerMvar = 8.2,
                powerFactor = 0.88,
                frequencyHz = 50.02,
                topOilTempC = 64.8,
                windingTempC = 82.4,
                sf6PressureBar = 0.0,
                breakerStatus = "CLOSED",
                springCharged = true,
                tapPosition = 9,
                activeAlarms = ""
            ),
            "33-CB-102" to ScadaTelemetryEntity(
                equipmentId = 2L,
                equipmentTag = "33-CB-102",
                equipmentName = "33 kV 1250A VCB Breaker",
                timestamp = "2026-09-25 09:34:00 UTC",
                voltageKv = 33.1,
                currentPhaseA = 412.0,
                currentPhaseB = 408.5,
                currentPhaseC = 414.2,
                activePowerMw = 22.8,
                reactivePowerMvar = 5.9,
                powerFactor = 0.92,
                frequencyHz = 50.01,
                topOilTempC = 0.0,
                windingTempC = 0.0,
                sf6PressureBar = 5.8,
                breakerStatus = "CLOSED",
                springCharged = true,
                tapPosition = 0,
                activeAlarms = ""
            ),
            "615-RY-04" to ScadaTelemetryEntity(
                equipmentId = 3L,
                equipmentTag = "615-RY-04",
                equipmentName = "ABB REF615 Feeder Relay",
                timestamp = "2026-09-25 09:34:00 UTC",
                voltageKv = 33.15,
                currentPhaseA = 412.0,
                currentPhaseB = 408.5,
                currentPhaseC = 414.2,
                activePowerMw = 22.8,
                reactivePowerMvar = 5.9,
                powerFactor = 0.92,
                frequencyHz = 50.01,
                topOilTempC = 0.0,
                windingTempC = 0.0,
                sf6PressureBar = 0.0,
                breakerStatus = "CLOSED",
                springCharged = true,
                tapPosition = 0,
                activeAlarms = ""
            )
        )
        _liveTelemetry.value = baseline
    }

    fun startLiveStream(scope: CoroutineScope) {
        if (streamingJob?.isActive == true) return
        _isLiveFeedActive.value = true

        streamingJob = scope.launch(Dispatchers.Default) {
            while (isActive && _isLiveFeedActive.value) {
                delay(2500) // Update every 2.5s for real-time SCADA cadence

                val currentMap = _liveTelemetry.value.toMutableMap()

                // Drift Transformer telemetry
                currentMap["132-TR-01"]?.let { tr ->
                    val driftTemp = (Random.nextDouble(-0.4, 0.5))
                    val driftCurrent = (Random.nextDouble(-4.0, 5.0))
                    val newWinding = (tr.windingTempC + driftTemp).coerceIn(70.0, 98.0)
                    val newCurrentA = (tr.currentPhaseA + driftCurrent).coerceAtLeast(100.0)

                    val alarms = if (newWinding >= 95.0) "STAGE1_WTI_HIGH;COOLING_FAN_FORCED" else ""

                    currentMap["132-TR-01"] = tr.copy(
                        timestamp = "LIVE " + java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date()),
                        windingTempC = newWinding,
                        topOilTempC = (tr.topOilTempC + driftTemp * 0.5).coerceIn(55.0, 85.0),
                        currentPhaseA = newCurrentA,
                        currentPhaseB = newCurrentA * 0.99,
                        currentPhaseC = newCurrentA * 1.01,
                        activePowerMw = (newCurrentA * 1.732 * 33.0 * 0.88 / 1000.0),
                        activeAlarms = alarms
                    )
                }

                // Drift Breaker telemetry
                currentMap["33-CB-102"]?.let { cb ->
                    val driftCurrent = Random.nextDouble(-3.0, 3.0)
                    currentMap["33-CB-102"] = cb.copy(
                        timestamp = "LIVE " + java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date()),
                        currentPhaseA = (cb.currentPhaseA + driftCurrent).coerceAtLeast(50.0),
                        currentPhaseB = (cb.currentPhaseB + driftCurrent).coerceAtLeast(50.0),
                        currentPhaseC = (cb.currentPhaseC + driftCurrent).coerceAtLeast(50.0),
                        sf6PressureBar = (cb.sf6PressureBar + Random.nextDouble(-0.01, 0.01)).coerceIn(5.2, 6.2)
                    )
                }

                _liveTelemetry.value = currentMap
            }
        }
    }

    fun stopLiveStream() {
        _isLiveFeedActive.value = false
        streamingJob?.cancel()
    }

    fun toggleLiveStream(scope: CoroutineScope) {
        if (_isLiveFeedActive.value) {
            stopLiveStream()
        } else {
            startLiveStream(scope)
        }
    }

    fun setProtocol(protocol: ScadaProtocolType) {
        _activeProtocol.value = protocol
    }

    // Ingest foreign SCADA packet
    fun ingestScadaPacket(tag: String, voltage: Double, currentA: Double, currentB: Double, currentC: Double, tempC: Double, status: String) {
        val currentMap = _liveTelemetry.value.toMutableMap()
        val existing = currentMap[tag]
        if (existing != null) {
            currentMap[tag] = existing.copy(
                timestamp = "INGESTED " + java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date()),
                voltageKv = voltage,
                currentPhaseA = currentA,
                currentPhaseB = currentB,
                currentPhaseC = currentC,
                windingTempC = tempC,
                breakerStatus = status
            )
            _liveTelemetry.value = currentMap
        }
    }
}
