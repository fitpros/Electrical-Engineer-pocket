package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EquipmentCategory
import com.example.data.model.FaultStatus
import com.example.data.model.MaintenanceStatus
import com.example.ui.MainViewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.NavDestination
import com.example.ui.components.NavigationDrawerContent
import com.example.ui.screens.AboutDeveloperScreen
import com.example.ui.screens.AddEquipmentDialog
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AuthFlowScreen
import com.example.ui.screens.CalculatorsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EquipmentDetailScreen
import com.example.ui.screens.EquipmentListScreen
import com.example.ui.screens.FaultAnalysisScreen
import com.example.ui.screens.InspectionsScreen
import com.example.ui.screens.MaintenanceScreen
import com.example.ui.screens.ProtectionCoordinationScreen
import com.example.ui.screens.PublicLandingScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.ScadaScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UserDashboardScreen
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.ElectricalEngineerProTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            ElectricalEngineerProTheme(themeMode = themeMode) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val equipmentList by viewModel.allEquipment.collectAsStateWithLifecycle()
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val faults by viewModel.allFaults.collectAsStateWithLifecycle()
    val tests by viewModel.allTests.collectAsStateWithLifecycle()
    val settings by viewModel.companySettings.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val selectedEquipmentId by viewModel.selectedEquipmentId.collectAsStateWithLifecycle()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var showAddEquipmentDialog by remember { mutableStateOf(false) }
    var showAlertsDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val activeFaults = faults.filter { it.status != FaultStatus.RESOLVED }
    val dueMaintenanceTasks = tasks.filter { it.status == MaintenanceStatus.DUE_TODAY || it.status == MaintenanceStatus.OVERDUE }

    val safeSettings = settings ?: com.example.data.repository.SeedData.initialSettings

    // Handle Back Press gracefully
    BackHandler(enabled = drawerState.isOpen || selectedEquipmentId != null || currentDestination != NavDestination.DASHBOARD) {
        when {
            drawerState.isOpen -> coroutineScope.launch { drawerState.close() }
            selectedEquipmentId != null -> viewModel.closeEquipmentDetail()
            currentDestination != NavDestination.DASHBOARD -> viewModel.navigateTo(NavDestination.DASHBOARD)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            ) {
                NavigationDrawerContent(
                    currentDestination = currentDestination,
                    onSelectDestination = { dest ->
                        when (dest) {
                            NavDestination.TRANSFORMER -> viewModel.setCategoryFilter(EquipmentCategory.TRANSFORMER)
                            NavDestination.PROTECTION -> viewModel.setCategoryFilter(EquipmentCategory.PROTECTION_RELAY)
                            NavDestination.CABLE -> viewModel.setCategoryFilter(EquipmentCategory.CABLE)
                            NavDestination.MOTOR -> viewModel.setCategoryFilter(EquipmentCategory.MOTOR)
                            NavDestination.SOLAR -> viewModel.setCategoryFilter(EquipmentCategory.SOLAR_PV)
                            NavDestination.EQUIPMENT -> viewModel.setCategoryFilter(null)
                            else -> viewModel.navigateTo(dest)
                        }
                        coroutineScope.launch { drawerState.close() }
                    },
                    activeFaultsCount = activeFaults.size,
                    dueMaintenanceCount = dueMaintenanceTasks.size,
                    companyName = safeSettings.companyName
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("app_scaffold"),
            topBar = {
                AppHeader(
                    projectName = safeSettings.currentProject,
                    engineerName = safeSettings.engineerName,
                    themeMode = themeMode,
                    onThemeToggle = { viewModel.toggleTheme() },
                    onMenuClick = { coroutineScope.launch { drawerState.open() } },
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    searchQuery = searchQuery,
                    isSearchActive = isSearchActive,
                    onToggleSearch = { viewModel.toggleSearch() },
                    unreadAlertsCount = activeFaults.size,
                    onNotificationsClick = { showAlertsDialog = true },
                    onProfileClick = { viewModel.navigateTo(NavDestination.USER_DASHBOARD) },
                    onSettingsClick = { viewModel.navigateTo(NavDestination.SETTINGS) }
                )
            },
            bottomBar = {
                if (selectedEquipmentId == null) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        NavigationBarItem(
                            selected = currentDestination == NavDestination.DASHBOARD,
                            onClick = { viewModel.navigateTo(NavDestination.DASHBOARD) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text("Dashboard", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricalBlue, indicatorColor = ElectricalBlue.copy(alpha = 0.15f))
                        )
                        NavigationBarItem(
                            selected = currentDestination == NavDestination.CALCULATORS,
                            onClick = { viewModel.navigateTo(NavDestination.CALCULATORS) },
                            icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculators") },
                            label = { Text("Calculators", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricalBlue, indicatorColor = ElectricalBlue.copy(alpha = 0.15f))
                        )
                        NavigationBarItem(
                            selected = currentDestination == NavDestination.EQUIPMENT,
                            onClick = { viewModel.setCategoryFilter(null) },
                            icon = { Icon(Icons.Default.PrecisionManufacturing, contentDescription = "Equipment") },
                            label = { Text("Equipment", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricalBlue, indicatorColor = ElectricalBlue.copy(alpha = 0.15f))
                        )
                        NavigationBarItem(
                            selected = currentDestination == NavDestination.MAINTENANCE,
                            onClick = { viewModel.navigateTo(NavDestination.MAINTENANCE) },
                            icon = { Icon(Icons.Default.Build, contentDescription = "Maintenance") },
                            label = { Text("PM / PdM", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricalBlue, indicatorColor = ElectricalBlue.copy(alpha = 0.15f))
                        )
                        NavigationBarItem(
                            selected = currentDestination == NavDestination.FAULT_ANALYSIS,
                            onClick = { viewModel.navigateTo(NavDestination.FAULT_ANALYSIS) },
                            icon = { Icon(Icons.Default.Warning, contentDescription = "Faults") },
                            label = { Text("Faults", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricalBlue, indicatorColor = ElectricalBlue.copy(alpha = 0.15f))
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val currentEq = equipmentList.find { it.id == selectedEquipmentId }

                if (currentEq != null) {
                    val photos by viewModel.getPhotosForEquipment(currentEq.id).collectAsState(initial = emptyList())
                    val eqTasks = tasks.filter { it.equipmentId == currentEq.id }
                    val eqFaults = faults.filter { it.equipmentId == currentEq.id }
                    val eqTests = tests.filter { it.equipmentId == currentEq.id }

                    EquipmentDetailScreen(
                        equipment = currentEq,
                        photos = photos,
                        tasks = eqTasks,
                        faults = eqFaults,
                        tests = eqTests,
                        onBack = { viewModel.closeEquipmentDetail() },
                        onAddPhoto = { viewModel.addPhoto(it) },
                        onDeletePhoto = { viewModel.deletePhoto(it) },
                        onUpdateEquipmentPhoto = { uri -> viewModel.updateEquipmentPhoto(currentEq.id, uri) },
                        onSchedulePM = { viewModel.navigateTo(NavDestination.MAINTENANCE) },
                        onReportFault = { viewModel.navigateTo(NavDestination.FAULT_ANALYSIS) },
                        onAddTest = { viewModel.navigateTo(NavDestination.INSPECTIONS) }
                    )
                } else {
                    when (currentDestination) {
                        NavDestination.DASHBOARD -> DashboardScreen(
                            equipmentList = equipmentList,
                            tasks = tasks,
                            faults = faults,
                            settings = safeSettings,
                            onNavigate = { viewModel.navigateTo(it) },
                            onOpenCategory = { cat -> viewModel.setCategoryFilter(cat) },
                            onOpenEquipmentDetail = { eq -> viewModel.openEquipmentDetail(eq) },
                            onAddEquipmentClick = { showAddEquipmentDialog = true }
                        )

                        NavDestination.CALCULATORS -> CalculatorsScreen()

                        NavDestination.SCADA_TELEMETRY -> ScadaScreen()

                        NavDestination.PROTECTION_COORDINATION -> ProtectionCoordinationScreen()

                        NavDestination.EQUIPMENT,
                        NavDestination.TRANSFORMER,
                        NavDestination.PROTECTION,
                        NavDestination.CABLE,
                        NavDestination.MOTOR,
                        NavDestination.SOLAR -> EquipmentListScreen(
                            equipmentList = equipmentList,
                            selectedCategoryFilter = selectedCategoryFilter,
                            onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                            searchQuery = searchQuery,
                            onSelectEquipment = { eq -> viewModel.openEquipmentDetail(eq) },
                            onAddEquipment = { showAddEquipmentDialog = true }
                        )

                        NavDestination.MAINTENANCE -> MaintenanceScreen(
                            tasks = tasks,
                            equipmentList = equipmentList,
                            onToggleTaskStatus = { viewModel.toggleMaintenanceStatus(it) },
                            onSchedulePM = { viewModel.scheduleTask(it) }
                        )

                        NavDestination.FAULT_ANALYSIS -> FaultAnalysisScreen(
                            faults = faults,
                            equipmentList = equipmentList,
                            onAddFault = { viewModel.addFault(it) }
                        )

                        NavDestination.INSPECTIONS -> InspectionsScreen(
                            tests = tests,
                            equipmentList = equipmentList,
                            settings = safeSettings,
                            onAddTest = { viewModel.addTest(it) }
                        )

                        NavDestination.REPORTS,
                        NavDestination.DOCUMENTS,
                        NavDestination.HISTORY -> ReportsScreen(
                            tests = tests,
                            settings = safeSettings,
                            equipmentList = equipmentList
                        )

                        NavDestination.SETTINGS -> SettingsScreen(
                            settings = safeSettings,
                            currentThemeMode = themeMode,
                            onThemeModeChange = { viewModel.setThemeMode(it) },
                            onSaveSettings = { viewModel.updateSettings(it) }
                        )

                        NavDestination.USER_DASHBOARD -> {
                            val user = currentUser
                            if (user != null) {
                                UserDashboardScreen(
                                    user = user,
                                    authManager = viewModel.authManager,
                                    equipmentList = equipmentList,
                                    tasks = tasks,
                                    tests = tests,
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onLogout = { viewModel.navigateTo(NavDestination.DASHBOARD) }
                                )
                            } else {
                                AuthFlowScreen(
                                    authManager = viewModel.authManager,
                                    onAuthSuccess = { viewModel.navigateTo(NavDestination.USER_DASHBOARD) },
                                    onBack = { viewModel.navigateTo(NavDestination.DASHBOARD) }
                                )
                            }
                        }

                        NavDestination.ADMIN_PANEL -> AdminPanelScreen(
                            authManager = viewModel.authManager,
                            onBack = { viewModel.navigateTo(NavDestination.DASHBOARD) }
                        )

                        NavDestination.ABOUT_DEVELOPER -> AboutDeveloperScreen(
                            authManager = viewModel.authManager,
                            onBack = { viewModel.navigateTo(NavDestination.DASHBOARD) }
                        )

                        NavDestination.PUBLIC_PORTAL -> PublicLandingScreen(
                            onNavigateToLogin = { viewModel.navigateTo(NavDestination.USER_DASHBOARD) },
                            onNavigateToSignUp = { viewModel.navigateTo(NavDestination.USER_DASHBOARD) },
                            onNavigateToAbout = { viewModel.navigateTo(NavDestination.ABOUT_DEVELOPER) },
                            onNavigateToContact = { viewModel.navigateTo(NavDestination.ABOUT_DEVELOPER) },
                            onNavigateToFeatures = { viewModel.navigateTo(NavDestination.DASHBOARD) },
                            onNavigateToPrivacy = { /* privacy */ },
                            onNavigateToTerms = { /* terms */ }
                        )
                    }
                }
            }
        }
    }

    // Add Equipment Dialog
    if (showAddEquipmentDialog) {
        AddEquipmentDialog(
            onDismiss = { showAddEquipmentDialog = false },
            onSave = { newEq ->
                viewModel.addEquipment(newEq)
                showAddEquipmentDialog = false
            }
        )
    }

    // Active System Alerts Dialog
    if (showAlertsDialog) {
        AlertDialog(
            onDismissRequest = { showAlertsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFDC2626))
                    Text("Substation Telemetry & Protection Alerts", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (activeFaults.isEmpty()) {
                        Text("No active fault alarms or trips. Power system is operating normally.", fontSize = 12.sp)
                    } else {
                        activeFaults.forEach { fault ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF450A0A),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(text = fault.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                    Text(text = "${fault.equipmentTag} • ${fault.tripRelayFunction}", color = Color(0xFFFCA5A5), fontSize = 11.sp)
                                    Text(text = fault.observedInfo, color = Color(0xFFE2E8F0), fontSize = 10.sp, maxLines = 2)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAlertsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue)) {
                    Text("Acknowledge")
                }
            }
        )
    }

    // User Profile Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Certified Engineer Credentials") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = safeSettings.engineerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = safeSettings.engineerLicense, color = ElectricalBlue, fontSize = 12.sp)
                    Text(text = "Organization: ${safeSettings.companyName}", fontSize = 11.sp)
                    Text(text = "Current Project: ${safeSettings.currentProject}", fontSize = 11.sp)
                    Text(text = "Client: ${safeSettings.clientName}", fontSize = 11.sp)
                    Text(text = "Email: ${safeSettings.contactEmail}", fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showProfileDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue)) {
                    Text("Close")
                }
            }
        )
    }
}
