package com.example.data.repository

import com.example.data.dao.ElectricalDao
import com.example.data.model.AssetPhotoEntity
import com.example.data.model.CompanySettingsEntity
import com.example.data.model.EquipmentEntity
import com.example.data.model.FaultRecordEntity
import com.example.data.model.MaintenanceTaskEntity
import com.example.data.model.TestRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ElectricalRepository(private val dao: ElectricalDao) {

    val allEquipment: Flow<List<EquipmentEntity>> = dao.getAllEquipment()
    val allTasks: Flow<List<MaintenanceTaskEntity>> = dao.getAllTasks()
    val allFaults: Flow<List<FaultRecordEntity>> = dao.getAllFaults()
    val allTests: Flow<List<TestRecordEntity>> = dao.getAllTests()
    val companySettings: Flow<CompanySettingsEntity?> = dao.getSettings()

    fun getEquipmentById(id: Long): Flow<EquipmentEntity?> = dao.getEquipmentById(id)
    fun getPhotosForEquipment(equipmentId: Long): Flow<List<AssetPhotoEntity>> = dao.getPhotosForEquipment(equipmentId)
    fun getTasksForEquipment(equipmentId: Long): Flow<List<MaintenanceTaskEntity>> = dao.getTasksForEquipment(equipmentId)
    fun getFaultsForEquipment(equipmentId: Long): Flow<List<FaultRecordEntity>> = dao.getFaultsForEquipment(equipmentId)
    fun getTestsForEquipment(equipmentId: Long): Flow<List<TestRecordEntity>> = dao.getTestsForEquipment(equipmentId)

    suspend fun insertEquipment(equipment: EquipmentEntity): Long = dao.insertEquipment(equipment)
    suspend fun updateEquipment(equipment: EquipmentEntity) = dao.updateEquipment(equipment)
    suspend fun deleteEquipment(equipment: EquipmentEntity) = dao.deleteEquipment(equipment)

    suspend fun insertPhoto(photo: AssetPhotoEntity): Long = dao.insertPhoto(photo)
    suspend fun deletePhoto(photo: AssetPhotoEntity) = dao.deletePhoto(photo)

    suspend fun insertTask(task: MaintenanceTaskEntity): Long = dao.insertTask(task)
    suspend fun updateTask(task: MaintenanceTaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: MaintenanceTaskEntity) = dao.deleteTask(task)

    suspend fun insertFault(fault: FaultRecordEntity): Long = dao.insertFault(fault)
    suspend fun updateFault(fault: FaultRecordEntity) = dao.updateFault(fault)
    suspend fun deleteFault(fault: FaultRecordEntity) = dao.deleteFault(fault)

    suspend fun insertTest(test: TestRecordEntity): Long = dao.insertTest(test)
    suspend fun deleteTest(test: TestRecordEntity) = dao.deleteTest(test)

    suspend fun updateSettings(settings: CompanySettingsEntity) = dao.insertSettings(settings)

    suspend fun seedDatabaseIfEmpty() {
        val existingEquipment = dao.getAllEquipment().first()
        if (existingEquipment.isEmpty()) {
            dao.insertAllEquipment(SeedData.initialEquipment)
            dao.insertAllPhotos(SeedData.initialPhotos)
            dao.insertAllTasks(SeedData.initialTasks)
            dao.insertAllFaults(SeedData.initialFaults)
            dao.insertAllTests(SeedData.initialTests)
            dao.insertSettings(SeedData.initialSettings)
        }
    }
}
