package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.database.AppDatabase
import com.example.data.model.AssetPhotoEntity
import com.example.data.model.CompanySettingsEntity
import com.example.data.model.EquipmentCategory
import com.example.data.model.EquipmentEntity
import com.example.data.model.FaultRecordEntity
import com.example.data.model.FaultStatus
import com.example.data.model.MaintenanceStatus
import com.example.data.model.MaintenanceTaskEntity
import com.example.data.model.TestRecordEntity
import com.example.data.model.UserAccountEntity
import com.example.data.repository.ElectricalRepository
import com.example.data.repository.SeedData
import com.example.ui.components.NavDestination
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ElectricalRepository
    val authManager: AuthManager

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ElectricalRepository(db.electricalDao())
        authManager = AuthManager(db.authAndAdminDao())
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
            authManager.initializeDefaultAdminIfEmpty()
        }
    }

    val currentUser: StateFlow<UserAccountEntity?> = authManager.currentUser

    val allEquipment: StateFlow<List<EquipmentEntity>> = repository.allEquipment
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks: StateFlow<List<MaintenanceTaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFaults: StateFlow<List<FaultRecordEntity>> = repository.allFaults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTests: StateFlow<List<TestRecordEntity>> = repository.allTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val companySettings: StateFlow<CompanySettingsEntity?> = repository.companySettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.initialSettings)

    // UI Navigation & Filters State
    private val _currentDestination = MutableStateFlow(NavDestination.DASHBOARD)
    val currentDestination: StateFlow<NavDestination> = _currentDestination.asStateFlow()

    private val _selectedEquipmentId = MutableStateFlow<Long?>(null)
    val selectedEquipmentId: StateFlow<Long?> = _selectedEquipmentId.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<EquipmentCategory?>(null)
    val selectedCategoryFilter: StateFlow<EquipmentCategory?> = _selectedCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun navigateTo(destination: NavDestination) {
        _currentDestination.value = destination
        _selectedEquipmentId.value = null
    }

    fun openEquipmentDetail(equipment: EquipmentEntity) {
        _selectedEquipmentId.value = equipment.id
    }

    fun closeEquipmentDetail() {
        _selectedEquipmentId.value = null
    }

    fun setCategoryFilter(category: EquipmentCategory?) {
        _selectedCategoryFilter.value = category
        _currentDestination.value = NavDestination.EQUIPMENT
        _selectedEquipmentId.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearch() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) {
            _searchQuery.value = ""
        }
    }

    fun toggleTheme() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.SYSTEM -> ThemeMode.DARK
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    // CRUD Actions
    fun addEquipment(equipment: EquipmentEntity) {
        viewModelScope.launch {
            repository.insertEquipment(equipment)
        }
    }

    fun updateEquipmentPhoto(equipmentId: Long, photoUri: String) {
        viewModelScope.launch {
            val current = allEquipment.value.find { it.id == equipmentId }
            if (current != null) {
                repository.updateEquipment(current.copy(photoUri = photoUri))
            }
        }
    }

    fun addPhoto(photo: AssetPhotoEntity) {
        viewModelScope.launch {
            repository.insertPhoto(photo)
        }
    }

    fun deletePhoto(photo: AssetPhotoEntity) {
        viewModelScope.launch {
            repository.deletePhoto(photo)
        }
    }

    fun getPhotosForEquipment(equipmentId: Long) = repository.getPhotosForEquipment(equipmentId)

    fun toggleMaintenanceStatus(task: MaintenanceTaskEntity) {
        viewModelScope.launch {
            val updatedStatus = if (task.status == MaintenanceStatus.COMPLETED) {
                MaintenanceStatus.DUE_THIS_WEEK
            } else {
                MaintenanceStatus.COMPLETED
            }
            repository.updateTask(task.copy(status = updatedStatus))
        }
    }

    fun scheduleTask(task: MaintenanceTaskEntity) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun addFault(fault: FaultRecordEntity) {
        viewModelScope.launch {
            repository.insertFault(fault)
        }
    }

    fun addTest(test: TestRecordEntity) {
        viewModelScope.launch {
            repository.insertTest(test)
        }
    }

    fun updateSettings(settings: CompanySettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(settings)
        }
    }
}
