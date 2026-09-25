package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.dao.AuthAndAdminDao
import com.example.data.dao.ElectricalDao
import com.example.data.model.AssetPhotoEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.CompanySettingsEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.DeveloperProfileEntity
import com.example.data.model.EquipmentEntity
import com.example.data.model.FaultRecordEntity
import com.example.data.model.GoogleSheetSyncEntity
import com.example.data.model.MaintenanceTaskEntity
import com.example.data.model.ScadaTelemetryEntity
import com.example.data.model.TestRecordEntity
import com.example.data.model.UserAccountEntity

@Database(
    entities = [
        EquipmentEntity::class,
        AssetPhotoEntity::class,
        MaintenanceTaskEntity::class,
        FaultRecordEntity::class,
        TestRecordEntity::class,
        CompanySettingsEntity::class,
        UserAccountEntity::class,
        GoogleSheetSyncEntity::class,
        AuditLogEntity::class,
        ContactMessageEntity::class,
        DeveloperProfileEntity::class,
        ScadaTelemetryEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun electricalDao(): ElectricalDao
    abstract fun authAndAdminDao(): AuthAndAdminDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "electrical_engineer_pro.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
