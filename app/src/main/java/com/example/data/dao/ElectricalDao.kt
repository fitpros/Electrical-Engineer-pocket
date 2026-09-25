package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AssetPhotoEntity
import com.example.data.model.CompanySettingsEntity
import com.example.data.model.EquipmentEntity
import com.example.data.model.FaultRecordEntity
import com.example.data.model.MaintenanceTaskEntity
import com.example.data.model.TestRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectricalDao {
    // --- Equipment ---
    @Query("SELECT * FROM equipment ORDER BY tagNumber ASC")
    fun getAllEquipment(): Flow<List<EquipmentEntity>>

    @Query("SELECT * FROM equipment WHERE id = :id")
    fun getEquipmentById(id: Long): Flow<EquipmentEntity?>

    @Query("SELECT * FROM equipment WHERE category = :category")
    fun getEquipmentByCategory(category: String): Flow<List<EquipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: EquipmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEquipment(list: List<EquipmentEntity>)

    @Update
    suspend fun updateEquipment(equipment: EquipmentEntity)

    @Delete
    suspend fun deleteEquipment(equipment: EquipmentEntity)

    // --- Asset Photos ---
    @Query("SELECT * FROM asset_photos WHERE equipmentId = :equipmentId ORDER BY id DESC")
    fun getPhotosForEquipment(equipmentId: Long): Flow<List<AssetPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: AssetPhotoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPhotos(photos: List<AssetPhotoEntity>)

    @Delete
    suspend fun deletePhoto(photo: AssetPhotoEntity)

    // --- Maintenance Tasks ---
    @Query("SELECT * FROM maintenance_tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<MaintenanceTaskEntity>>

    @Query("SELECT * FROM maintenance_tasks WHERE equipmentId = :equipmentId ORDER BY dueDate ASC")
    fun getTasksForEquipment(equipmentId: Long): Flow<List<MaintenanceTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: MaintenanceTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<MaintenanceTaskEntity>)

    @Update
    suspend fun updateTask(task: MaintenanceTaskEntity)

    @Delete
    suspend fun deleteTask(task: MaintenanceTaskEntity)

    // --- Fault Records ---
    @Query("SELECT * FROM fault_records ORDER BY id DESC")
    fun getAllFaults(): Flow<List<FaultRecordEntity>>

    @Query("SELECT * FROM fault_records WHERE equipmentId = :equipmentId ORDER BY id DESC")
    fun getFaultsForEquipment(equipmentId: Long): Flow<List<FaultRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFault(fault: FaultRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFaults(faults: List<FaultRecordEntity>)

    @Update
    suspend fun updateFault(fault: FaultRecordEntity)

    @Delete
    suspend fun deleteFault(fault: FaultRecordEntity)

    // --- Test Records ---
    @Query("SELECT * FROM test_records ORDER BY testDate DESC")
    fun getAllTests(): Flow<List<TestRecordEntity>>

    @Query("SELECT * FROM test_records WHERE equipmentId = :equipmentId ORDER BY testDate DESC")
    fun getTestsForEquipment(equipmentId: Long): Flow<List<TestRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTests(tests: List<TestRecordEntity>)

    @Delete
    suspend fun deleteTest(test: TestRecordEntity)

    // --- Company Settings ---
    @Query("SELECT * FROM company_settings WHERE id = 1")
    fun getSettings(): Flow<CompanySettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: CompanySettingsEntity)
}
