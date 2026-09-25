package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ScadaProtocolType(val protocolName: String, val defaultPort: Int, val description: String) {
    MODBUS_TCP("Modbus TCP/IP", 502, "Industrial register-based polling (Holding Registers 40001-40100)"),
    IEC_60870_5_104("IEC 60870-5-104", 2404, "Telecontrol and substation automation over TCP/IP APDU"),
    DNP3("DNP3 over IP", 20000, "Distributed Network Protocol for utility SCADA networks"),
    REST_JSON_GATEWAY("SCADA IoT / REST Ingestion", 8443, "Secure JSON streaming gateway for plant historians")
}

@Entity(tableName = "scada_telemetry")
data class ScadaTelemetryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipmentId: Long,
    val equipmentTag: String,
    val equipmentName: String,
    val timestamp: String,
    val voltageKv: Double,
    val currentPhaseA: Double,
    val currentPhaseB: Double,
    val currentPhaseC: Double,
    val activePowerMw: Double,
    val reactivePowerMvar: Double,
    val powerFactor: Double,
    val frequencyHz: Double = 50.00,
    val topOilTempC: Double = 0.0,
    val windingTempC: Double = 0.0,
    val sf6PressureBar: Double = 0.0,
    val breakerStatus: String = "CLOSED", // CLOSED, OPEN, TRIPPED
    val springCharged: Boolean = true,
    val tapPosition: Int = 9,
    val activeAlarms: String = "" // Semicolon-delimited alarm codes
)

data class ScadaIngestionPayload(
    val protocol: String,
    val gatewayId: String,
    val timestampUtc: Long,
    val tag: String,
    val voltageKv: Double,
    val currents: List<Double>,
    val powerMw: Double,
    val powerMvar: Double,
    val powerFactor: Double,
    val oilTempC: Double?,
    val windingTempC: Double?,
    val gasPressureBar: Double?,
    val status: String,
    val alarms: List<String>
)
