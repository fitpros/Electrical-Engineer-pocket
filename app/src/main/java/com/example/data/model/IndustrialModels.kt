package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EquipmentCategory(val displayName: String, val searchPhrase: String) {
    TRANSFORMER("Power Transformer", "power transformer substation industrial 40 MVA"),
    CIRCUIT_BREAKER("Circuit Breaker / Switchgear", "33 kV switchgear vacuum circuit breaker VCB GIS"),
    PROTECTION_RELAY("Protection Relay", "ABB REF615 electrical protection relay panel"),
    CABLE("HV / MV Power Cable", "high voltage cable termination joint tray"),
    MOTOR("Industrial Motor / MCC", "heavy industrial electrical motor control center VFD"),
    SOLAR_PV("Solar PV System", "utility scale solar hybrid inverter PV array")
}

enum class AssetStatus(val label: String) {
    NORMAL("Normal"),
    WARNING("Warning / Due Soon"),
    FAULT("Fault / Critical"),
    INACTIVE("Inactive / De-energized")
}

enum class CriticalityLevel(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Mission Critical")
}

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tagNumber: String,
    val name: String,
    val category: EquipmentCategory,
    val location: String,
    val voltageLevel: String,
    val rating: String,
    val manufacturer: String,
    val model: String,
    val serialNumber: String,
    val commissioningDate: String,
    val status: AssetStatus,
    val criticality: CriticalityLevel,
    val specificationsJson: String,
    val photoUri: String = "",
    val photoSearchQuery: String = "",
    val lastInspectionDate: String = "",
    val nextMaintenanceDate: String = ""
)

@Entity(tableName = "asset_photos")
data class AssetPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipmentId: Long,
    val photoUri: String,
    val caption: String,
    val photoType: String, // Nameplate, Front View, Rear View, Relay Display, Termination, Damage, Before Maintenance, After Maintenance
    val dateAdded: String,
    val inspectionRef: String = ""
)

enum class MaintenanceStatus(val label: String) {
    DUE_TODAY("Due Today"),
    DUE_THIS_WEEK("Due This Week"),
    OVERDUE("Overdue"),
    COMPLETED("Completed")
}

enum class MaintenanceType(val label: String) {
    PREVENTIVE("Preventive (PM)"),
    PREDICTIVE("Predictive (PdM)"),
    CORRECTIVE("Corrective / Repair"),
    COMMISSIONING("Commissioning")
}

enum class PriorityLevel(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent")
}

@Entity(tableName = "maintenance_tasks")
data class MaintenanceTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipmentId: Long,
    val equipmentTag: String,
    val equipmentName: String,
    val title: String,
    val taskType: MaintenanceType,
    val priority: PriorityLevel,
    val dueDate: String,
    val completionDate: String? = null,
    val status: MaintenanceStatus,
    val assignedEngineer: String,
    val notes: String = "",
    val checklistItems: String = "" // Delimited checklist
)

enum class FaultSeverity(val label: String) {
    CRITICAL_ALARM("Critical Alarm"),
    PROTECTION_TRIP("Protection Trip"),
    THERMAL_ANOMALY("Thermal Anomaly"),
    INSULATION_DEGRADATION("Insulation Degradation"),
    MECHANICAL_STRESS("Mechanical / Contact Wear")
}

enum class FaultStatus(val label: String) {
    INVESTIGATING("Investigating"),
    ROOT_CAUSE_IDENTIFIED("Root Cause Identified"),
    RESOLVED("Resolved / Closed")
}

@Entity(tableName = "fault_records")
data class FaultRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipmentId: Long,
    val equipmentTag: String,
    val equipmentName: String,
    val equipmentCategory: EquipmentCategory,
    val title: String,
    val timestamp: String,
    val severity: FaultSeverity,
    val status: FaultStatus,
    val tripRelayFunction: String, // e.g. ANSI 50/51 Overcurrent, ANSI 87T Differential, 64R Earth Fault
    val breakerStatus: String, // Tripped / Open, Locked Out, Closed
    val observedInfo: String,
    val possibleCauses: String,
    val measurementsFindings: String,
    val rootCause: String = "",
    val correctiveAction: String = ""
)

enum class TestType(val label: String) {
    INSULATION_RESISTANCE("Insulation Resistance (Megger)"),
    WINDING_RESISTANCE("Winding Resistance Test"),
    CONTACT_RESISTANCE("Contact Resistance (Ductor)"),
    EARTH_LOOP_IMPEDANCE("Earth Electrode / Ground Resistance"),
    RELAY_TIMING("Protection Relay Injection & Timing"),
    TRANSFORMER_TURNS_RATIO("Transformer Turns Ratio (TTR)"),
    DISSOLVED_GAS_ANALYSIS("Dissolved Gas Analysis (DGA)")
}

enum class TestResultStatus(val label: String) {
    PASS("Pass"),
    ATTENTION("Attention / Due Re-test"),
    FAIL("Fail / Out of Tolerance")
}

@Entity(tableName = "test_records")
data class TestRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipmentId: Long,
    val equipmentTag: String,
    val equipmentName: String,
    val testType: TestType,
    val testDate: String,
    val ambientTempC: Double,
    val humidityPercent: Double,
    val instrumentModel: String,
    val instrumentCalibDate: String,
    val testResult: TestResultStatus,
    val summaryFindings: String,
    val readingsJson: String,
    val engineerName: String,
    val clientWitness: String = ""
)

@Entity(tableName = "company_settings")
data class CompanySettingsEntity(
    @PrimaryKey val id: Int = 1,
    val companyName: String = "Apex Power & Engineering Solutions Ltd.",
    val currentProject: String = "Al-Khaleej 132/33 kV Substation Modernization",
    val clientName: String = "National Electric Grid Authority",
    val engineerName: String = "Marcus Vance, PE",
    val engineerLicense: String = "PE-EL-98402 / Senior Protection Engineer",
    val companyAddress: String = "Engineering Tower, Technopark District, St. 404",
    val contactEmail: String = "protection.team@apex-power-eng.com",
    val reportHeader: String = "HIGH VOLTAGE ASSET INSPECTION & TEST CERTIFICATE",
    val reportFooter: String = "Certified in compliance with IEEE C37, IEC 60076, and IEC 60255 Standards",
    val themeMode: String = "SYSTEM" // LIGHT, DARK, SYSTEM
)
