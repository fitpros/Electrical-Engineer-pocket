package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.AssetPhotoEntity
import com.example.data.model.EquipmentEntity
import com.example.data.model.FaultRecordEntity
import com.example.data.model.MaintenanceTaskEntity
import com.example.data.model.TestRecordEntity
import com.example.ui.components.AssetStatusBadge
import com.example.ui.components.CriticalityBadge
import com.example.ui.components.EngineeringAssetImage
import com.example.ui.components.FaultSeverityBadge
import com.example.ui.components.MaintenanceStatusBadge
import com.example.ui.components.TestResultBadge
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusPassGreen

enum class EquipmentDetailTab(val title: String) {
    OVERVIEW("Overview"),
    SPECIFICATIONS("Specifications"),
    MAINTENANCE("Maintenance (PM)"),
    INSPECTIONS("Inspections"),
    FAULTS("Faults & Trips"),
    TESTS("Test Records"),
    DOCUMENTS("Documents"),
    PHOTOS("Photo Gallery"),
    HISTORY("History / Log")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentDetailScreen(
    equipment: EquipmentEntity,
    photos: List<AssetPhotoEntity>,
    tasks: List<MaintenanceTaskEntity>,
    faults: List<FaultRecordEntity>,
    tests: List<TestRecordEntity>,
    onBack: () -> Unit,
    onAddPhoto: (AssetPhotoEntity) -> Unit,
    onDeletePhoto: (AssetPhotoEntity) -> Unit,
    onUpdateEquipmentPhoto: (String) -> Unit,
    onSchedulePM: (Long) -> Unit,
    onReportFault: (Long) -> Unit,
    onAddTest: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(EquipmentDetailTab.OVERVIEW) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var fullScreenPhoto by remember { mutableStateOf<AssetPhotoEntity?>(null) }
    var photoToDelete by remember { mutableStateOf<AssetPhotoEntity?>(null) }

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdateEquipmentPhoto(uri.toString())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Navigation Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Assets",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = equipment.tagNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = equipment.name,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                AssetStatusBadge(status = equipment.status)
            }
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Equipment Photo / Blueprint Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                EngineeringAssetImage(
                    category = equipment.category,
                    photoUri = equipment.photoUri.ifBlank { null },
                    searchPhrase = equipment.photoSearchQuery,
                    heightDp = 180,
                    onUploadClick = {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )
            }

            // Quick Asset Meta Strip
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = equipment.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        CriticalityBadge(level = equipment.criticality)
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetaItem(label = "Tag Number", value = equipment.tagNumber)
                        MetaItem(label = "Voltage Level", value = equipment.voltageLevel)
                        MetaItem(label = "Capacity / Rating", value = equipment.rating)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetaItem(label = "Location", value = equipment.location)
                        MetaItem(label = "Manufacturer", value = equipment.manufacturer)
                        MetaItem(label = "Model", value = equipment.model)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ElectricalBlue
            ) {
                EquipmentDetailTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Panels
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    EquipmentDetailTab.OVERVIEW -> EquipmentOverviewTab(equipment = equipment, tasks = tasks, faults = faults)
                    EquipmentDetailTab.SPECIFICATIONS -> EquipmentSpecsTab(equipment = equipment)
                    EquipmentDetailTab.MAINTENANCE -> EquipmentMaintenanceTab(tasks = tasks, onSchedulePM = { onSchedulePM(equipment.id) })
                    EquipmentDetailTab.INSPECTIONS -> EquipmentInspectionsTab(equipment = equipment, tests = tests, onAddTest = { onAddTest(equipment.id) })
                    EquipmentDetailTab.FAULTS -> EquipmentFaultsTab(faults = faults, onReportFault = { onReportFault(equipment.id) })
                    EquipmentDetailTab.TESTS -> EquipmentTestsTab(tests = tests, onAddTest = { onAddTest(equipment.id) })
                    EquipmentDetailTab.DOCUMENTS -> EquipmentDocumentsTab(equipment = equipment)
                    EquipmentDetailTab.PHOTOS -> EquipmentPhotosTab(
                        photos = photos,
                        onAddPhotoClick = { showAddPhotoDialog = true },
                        onPhotoClick = { fullScreenPhoto = it },
                        onDeletePhoto = { photoToDelete = it }
                    )
                    EquipmentDetailTab.HISTORY -> EquipmentHistoryTab(equipment = equipment)
                }
            }
        }
    }

    // Full Screen Photo Dialog
    fullScreenPhoto?.let { photo ->
        Dialog(onDismissRequest = { fullScreenPhoto = null }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF091424),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = photo.caption,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        IconButton(onClick = { fullScreenPhoto = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    if (photo.photoUri.isNotBlank()) {
                        AsyncImage(
                            model = photo.photoUri,
                            contentDescription = photo.caption,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color(0xFF132238), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Asset Image Preview [${photo.photoType}]", color = Color(0xFF94A3B8), fontSize = 13.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Type: ${photo.photoType}", color = Color(0xFF38BDF8), fontSize = 11.sp)
                        Text(text = "Date: ${photo.dateAdded}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            }
        }
    }

    // Delete Photo Confirmation
    photoToDelete?.let { photo ->
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            title = { Text("Delete Asset Photo?") },
            text = { Text("Are you sure you want to permanently delete '${photo.caption}' from the asset gallery?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePhoto(photo)
                        photoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCriticalRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { photoToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Photo Dialog
    if (showAddPhotoDialog) {
        AddPhotoDialog(
            equipmentId = equipment.id,
            onDismiss = { showAddPhotoDialog = false },
            onSave = { newPhoto ->
                onAddPhoto(newPhoto)
                showAddPhotoDialog = false
            }
        )
    }
}

@Composable
fun MetaItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

// --- Tabs implementations ---
@Composable
fun EquipmentOverviewTab(
    equipment: EquipmentEntity,
    tasks: List<MaintenanceTaskEntity>,
    faults: List<FaultRecordEntity>
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "OPERATIONAL STATUS & METRICS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricalBlue)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Commissioning Date:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = equipment.commissioningDate, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Last Periodic Inspection:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = equipment.lastInspectionDate.ifBlank { "N/A" }, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Next Scheduled Maintenance:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = equipment.nextMaintenanceDate.ifBlank { "N/A" }, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active PM Tasks
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "ASSOCIATED MAINTENANCE TASKS (${tasks.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricalBlue)
                if (tasks.isEmpty()) {
                    Text(text = "No pending maintenance tasks for this equipment.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    tasks.forEach { task ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = task.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            MaintenanceStatusBadge(status = task.status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentSpecsTab(equipment: EquipmentEntity) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "TECHNICAL SPECIFICATIONS & ELECTRICAL RATINGS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricalBlue)
            // Parse simple JSON or key-values
            val cleaned = equipment.specificationsJson
                .replace("{", "")
                .replace("}", "")
                .replace("\"", "")
            val pairs = cleaned.split(",")

            pairs.forEach { pair ->
                val parts = pair.split(":")
                if (parts.size >= 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = parts[0].trim(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = parts.subList(1, parts.size).joinToString(":").trim(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                }
            }
        }
    }
}

@Composable
fun EquipmentMaintenanceTab(
    tasks: List<MaintenanceTaskEntity>,
    onSchedulePM: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Maintenance Schedule", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Button(
                onClick = onSchedulePM,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(text = "Schedule PM", fontSize = 11.sp)
            }
        }

        if (tasks.isEmpty()) {
            Text(text = "No maintenance records currently registered.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            tasks.forEach { task ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = task.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            MaintenanceStatusBadge(status = task.status)
                        }
                        Text(text = task.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Due: ${task.dueDate}", fontSize = 10.sp, color = ElectricalBlue)
                            Text(text = "Assigned: ${task.assignedEngineer}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentInspectionsTab(
    equipment: EquipmentEntity,
    tests: List<TestRecordEntity>,
    onAddTest: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Periodic Inspections & Quality Checks", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Button(onClick = onAddTest, colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue), shape = RoundedCornerShape(6.dp)) {
                Text(text = "Log Inspection", fontSize = 11.sp)
            }
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "PRE-ENERGIZATION / PERIODIC CHECKLIST", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricalBlue)
                listOf(
                    "Visual inspection of porcelain / composite bushings for micro-cracks and pollution deposit" to true,
                    "Earthing / grounding bond integrity check (< 0.1 Ω to main substation earth grid)" to true,
                    "Auxiliary wiring, terminal strip tightness, and terminal labeling verification" to true,
                    "Check silica gel breather color (minimum 75% active desiccant)" to true,
                    "Nameplate verification against Single Line Diagram (SLD)" to true
                ).forEach { (item, isChecked) ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.FactCheck, contentDescription = null, tint = StatusPassGreen, modifier = Modifier.size(16.dp))
                        Text(text = item, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentFaultsTab(
    faults: List<FaultRecordEntity>,
    onReportFault: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Diagnostic Fault & Trip Logs", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Button(onClick = onReportFault, colors = ButtonDefaults.buttonColors(containerColor = StatusCriticalRed), shape = RoundedCornerShape(6.dp)) {
                Text(text = "Report Fault", fontSize = 11.sp)
            }
        }

        if (faults.isEmpty()) {
            Text(text = "No fault or trip events recorded for this equipment. All protection indices nominal.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            faults.forEach { fault ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = fault.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            FaultSeverityBadge(severity = fault.severity)
                        }
                        Text(text = "Relay Function: ${fault.tripRelayFunction}", fontSize = 11.sp, color = ElectricalBlue)
                        Text(text = fault.observedInfo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Root Cause: ${fault.rootCause}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentTestsTab(
    tests: List<TestRecordEntity>,
    onAddTest: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Electrical Test Certificates", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Button(onClick = onAddTest, colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue), shape = RoundedCornerShape(6.dp)) {
                Text(text = "New Test Record", fontSize = 11.sp)
            }
        }

        if (tests.isEmpty()) {
            Text(text = "No diagnostic test records logged yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            tests.forEach { test ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = test.testType.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            TestResultBadge(result = test.testResult)
                        }
                        Text(text = "Date: ${test.testDate} | Instrument: ${test.instrumentModel}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = test.summaryFindings, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Certified by: ${test.engineerName}", fontSize = 10.sp, color = ElectricalBlue)
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentDocumentsTab(equipment: EquipmentEntity) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "Technical Manuals & Schematic Drawings", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        listOf(
            "Manufacturer Factory Acceptance Test (FAT) Certificate" to "PDF (4.2 MB)",
            "Single Line Diagram (SLD) Bay Schedule & CT/VT Ratios" to "DWG / PDF (8.9 MB)",
            "Operation & Maintenance (O&M) Instruction Manual" to "PDF (18.5 MB)",
            "Protection Relay Configuration & Setting Calculation File" to "CID / RIO (640 KB)"
        ).forEach { (doc, size) ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = ElectricalBlue)
                        Column {
                            Text(text = doc, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = size, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    OutlinedButton(onClick = {}, shape = RoundedCornerShape(4.dp)) {
                        Text(text = "View", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentPhotosTab(
    photos: List<AssetPhotoEntity>,
    onAddPhotoClick: () -> Unit,
    onPhotoClick: (AssetPhotoEntity) -> Unit,
    onDeletePhoto: (AssetPhotoEntity) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Site Asset Photo Gallery", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = "${photos.size} Verified Inspection Photos", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = onAddPhotoClick,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                shape = RoundedCornerShape(6.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Add Photo", fontSize = 11.sp)
            }
        }

        if (photos.isEmpty()) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(36.dp))
                    Text(text = "No Site Photos Uploaded Yet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Upload photos of the nameplate, HV bushings, control panel, or maintenance work.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                photos.forEach { photo ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPhotoClick(photo) }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(Color(0xFF0F1E33), RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (photo.photoUri.isNotBlank()) {
                                    AsyncImage(
                                        model = photo.photoUri,
                                        contentDescription = photo.caption,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp))
                                    )
                                } else {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = ElectricalBlue)
                                }
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(text = photo.caption, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "Type: ${photo.photoType}", fontSize = 11.sp, color = ElectricalBlue)
                                Text(text = "Date: ${photo.dateAdded} • Ref: ${photo.inspectionRef}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            IconButton(onClick = { onDeletePhoto(photo) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusCriticalRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentHistoryTab(equipment: EquipmentEntity) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "Audit Log & Lifecycle History", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        listOf(
            "Commissioned and energized into service by Senior Commissioning Team" to equipment.commissioningDate,
            "1-Year Periodic Dissolved Gas Analysis (DGA) and Megger test passed" to "2022-08-15",
            "Annual thermographic IR inspection completed with zero hotspots" to "2023-09-02",
            "Routine tap-changer counter reading and oil sampling" to equipment.lastInspectionDate
        ).forEach { (event, date) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(16.dp))
                Column {
                    Text(text = event, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = date, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// Dialog for Adding a Site Photo
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddPhotoDialog(
    equipmentId: Long,
    onDismiss: () -> Unit,
    onSave: (AssetPhotoEntity) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var photoType by remember { mutableStateOf("Nameplate") }
    var inspectionRef by remember { mutableStateOf("INS-2026-09") }
    var selectedUri by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri.toString()
        }
    }

    val types = listOf(
        "Nameplate",
        "Front View",
        "Rear View",
        "Relay Display",
        "Termination",
        "Internal Panel",
        "Damage",
        "Before Maintenance",
        "After Maintenance"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Site Photo / Evidence") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Photo Caption") },
                    placeholder = { Text("e.g. HV Bushing Oil Level Gauge") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = inspectionRef,
                    onValueChange = { inspectionRef = it },
                    label = { Text("Inspection Reference ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Photo Classification:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    types.take(6).forEach { t ->
                        val isSel = photoType == t
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isSel) ElectricalBlue else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { photoType = t }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = t,
                                fontSize = 10.sp,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = ElectricalBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedUri.isBlank()) "Select Image from Device" else "Image Selected ✓",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        AssetPhotoEntity(
                            equipmentId = equipmentId,
                            photoUri = selectedUri,
                            caption = caption.ifBlank { "Asset Photo ($photoType)" },
                            photoType = photoType,
                            dateAdded = "2026-09-25",
                            inspectionRef = inspectionRef
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue)
            ) {
                Text("Save Photo")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
