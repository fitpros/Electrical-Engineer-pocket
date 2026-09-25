package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.model.AccountStatus
import com.example.data.model.AssetStatus
import com.example.data.model.CriticalityLevel
import com.example.data.model.EquipmentCategory
import com.example.data.model.FaultSeverity
import com.example.data.model.FaultStatus
import com.example.data.model.MaintenanceStatus
import com.example.data.model.MaintenanceType
import com.example.data.model.PriorityLevel
import com.example.data.model.SyncStatus
import com.example.data.model.TestResultStatus
import com.example.data.model.TestType
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromEquipmentCategory(value: EquipmentCategory): String = value.name

    @TypeConverter
    fun toEquipmentCategory(value: String): EquipmentCategory =
        try { EquipmentCategory.valueOf(value) } catch (e: Exception) { EquipmentCategory.TRANSFORMER }

    @TypeConverter
    fun fromAssetStatus(value: AssetStatus): String = value.name

    @TypeConverter
    fun toAssetStatus(value: String): AssetStatus =
        try { AssetStatus.valueOf(value) } catch (e: Exception) { AssetStatus.NORMAL }

    @TypeConverter
    fun fromCriticality(value: CriticalityLevel): String = value.name

    @TypeConverter
    fun toCriticality(value: String): CriticalityLevel =
        try { CriticalityLevel.valueOf(value) } catch (e: Exception) { CriticalityLevel.MEDIUM }

    @TypeConverter
    fun fromMaintenanceStatus(value: MaintenanceStatus): String = value.name

    @TypeConverter
    fun toMaintenanceStatus(value: String): MaintenanceStatus =
        try { MaintenanceStatus.valueOf(value) } catch (e: Exception) { MaintenanceStatus.DUE_THIS_WEEK }

    @TypeConverter
    fun fromMaintenanceType(value: MaintenanceType): String = value.name

    @TypeConverter
    fun toMaintenanceType(value: String): MaintenanceType =
        try { MaintenanceType.valueOf(value) } catch (e: Exception) { MaintenanceType.PREVENTIVE }

    @TypeConverter
    fun fromPriorityLevel(value: PriorityLevel): String = value.name

    @TypeConverter
    fun toPriorityLevel(value: String): PriorityLevel =
        try { PriorityLevel.valueOf(value) } catch (e: Exception) { PriorityLevel.MEDIUM }

    @TypeConverter
    fun fromFaultSeverity(value: FaultSeverity): String = value.name

    @TypeConverter
    fun toFaultSeverity(value: String): FaultSeverity =
        try { FaultSeverity.valueOf(value) } catch (e: Exception) { FaultSeverity.CRITICAL_ALARM }

    @TypeConverter
    fun fromFaultStatus(value: FaultStatus): String = value.name

    @TypeConverter
    fun toFaultStatus(value: String): FaultStatus =
        try { FaultStatus.valueOf(value) } catch (e: Exception) { FaultStatus.INVESTIGATING }

    @TypeConverter
    fun fromTestType(value: TestType): String = value.name

    @TypeConverter
    fun toTestType(value: String): TestType =
        try { TestType.valueOf(value) } catch (e: Exception) { TestType.INSULATION_RESISTANCE }

    @TypeConverter
    fun fromTestResult(value: TestResultStatus): String = value.name

    @TypeConverter
    fun toTestResult(value: String): TestResultStatus =
        try { TestResultStatus.valueOf(value) } catch (e: Exception) { TestResultStatus.PASS }

    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole =
        try { UserRole.valueOf(value) } catch (e: Exception) { UserRole.ENGINEER }

    @TypeConverter
    fun fromAccountStatus(value: AccountStatus): String = value.name

    @TypeConverter
    fun toAccountStatus(value: String): AccountStatus =
        try { AccountStatus.valueOf(value) } catch (e: Exception) { AccountStatus.ACTIVE }

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus =
        try { SyncStatus.valueOf(value) } catch (e: Exception) { SyncStatus.SYNCED }
}
