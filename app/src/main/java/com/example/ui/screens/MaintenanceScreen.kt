package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EquipmentEntity
import com.example.data.model.MaintenanceStatus
import com.example.data.model.MaintenanceTaskEntity
import com.example.data.model.MaintenanceType
import com.example.data.model.PriorityLevel
import com.example.ui.components.MaintenanceStatusBadge
import com.example.ui.components.MaintenanceSummaryMetrics
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusPassGreen
import com.example.ui.theme.StatusWarningAmber

@Composable
fun MaintenanceScreen(
    tasks: List<MaintenanceTaskEntity>,
    equipmentList: List<EquipmentEntity>,
    onToggleTaskStatus: (MaintenanceTaskEntity) -> Unit,
    onSchedulePM: (MaintenanceTaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<MaintenanceStatus?>(null) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var selectedTaskForInspection by remember { mutableStateOf<MaintenanceTaskEntity?>(null) }

    val dueToday = tasks.count { it.status == MaintenanceStatus.DUE_TODAY }
    val dueWeek = tasks.count { it.status == MaintenanceStatus.DUE_THIS_WEEK }
    val overdue = tasks.count { it.status == MaintenanceStatus.OVERDUE }
    val completed = tasks.count { it.status == MaintenanceStatus.COMPLETED }
    val totalTasks = tasks.size.coerceAtLeast(1)
    val progress = completed.toFloat() / totalTasks.toFloat()

    val filteredTasks = tasks.filter { task ->
        selectedFilter == null || task.status == selectedFilter
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PM & PdM Maintenance Center",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Reliability-Centered Maintenance (RCM) Tracking",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showScheduleDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Schedule PM", fontSize = 12.sp)
                        }
                    }

                    // Maintenance Progress bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Monthly PM Completion Progress",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "%.0f%% (%d of %d Tasks)".format(progress * 100, completed, tasks.size),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricalBlue
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = StatusPassGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // Summary KPI Cards (Due Today, Due This Week, Overdue, Completed)
                    MaintenanceSummaryMetrics(
                        dueTodayCount = dueToday,
                        dueWeekCount = dueWeek,
                        overdueCount = overdue,
                        completedCount = completed,
                        onFilterClick = { filterStr ->
                            selectedFilter = when (filterStr) {
                                "DUE_TODAY" -> MaintenanceStatus.DUE_TODAY
                                "DUE_THIS_WEEK" -> MaintenanceStatus.DUE_THIS_WEEK
                                "OVERDUE" -> MaintenanceStatus.OVERDUE
                                "COMPLETED" -> MaintenanceStatus.COMPLETED
                                else -> null
                            }
                        }
                    )
                }
            }

            // Filter Tabs
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                val filters = listOf(null) + MaintenanceStatus.values().toList()
                val selectedIndex = filters.indexOf(selectedFilter).coerceAtLeast(0)

                ScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = ElectricalBlue
                ) {
                    filters.forEachIndexed { i, f ->
                        Tab(
                            selected = i == selectedIndex,
                            onClick = { selectedFilter = f },
                            text = {
                                Text(
                                    text = f?.label ?: "All Tasks (${tasks.size})",
                                    fontSize = 12.sp,
                                    fontWeight = if (i == selectedIndex) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            // Task List
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(40.dp))
                        Text(text = "No Maintenance Tasks In This Category", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "All equipment in this inspection cycle are up to date.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        MaintenanceTaskCard(
                            task = task,
                            onToggleStatus = { onToggleTaskStatus(task) },
                            onOpenChecklist = { selectedTaskForInspection = task }
                        )
                    }
                }
            }
        }
    }

    // Schedule PM Dialog
    if (showScheduleDialog) {
        SchedulePMDialog(
            equipmentList = equipmentList,
            onDismiss = { showScheduleDialog = false },
            onSave = { newTask ->
                onSchedulePM(newTask)
                showScheduleDialog = false
            }
        )
    }

    // Inspection Checklist Dialog
    selectedTaskForInspection?.let { task ->
        InspectionChecklistDialog(
            task = task,
            onDismiss = { selectedTaskForInspection = null },
            onCompleteTask = {
                onToggleTaskStatus(task)
                selectedTaskForInspection = null
            }
        )
    }
}

@Composable
fun MaintenanceTaskCard(
    task: MaintenanceTaskEntity,
    onToggleStatus: () -> Unit,
    onOpenChecklist: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${task.equipmentTag} • ${task.equipmentName}",
                        fontSize = 12.sp,
                        color = ElectricalBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                MaintenanceStatusBadge(status = task.status)
            }

            Text(
                text = task.notes,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Due Date: ${task.dueDate}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Assigned: ${task.assignedEngineer}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onOpenChecklist,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Checklist", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onToggleStatus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (task.status == MaintenanceStatus.COMPLETED) StatusPassGreen else ElectricalBlue
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (task.status == MaintenanceStatus.COMPLETED) "Completed" else "Mark Complete",
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// Checklist modal dialog
@Composable
fun InspectionChecklistDialog(
    task: MaintenanceTaskEntity,
    onDismiss: () -> Unit,
    onCompleteTask: () -> Unit
) {
    val items = task.checklistItems.split(";").filter { it.isNotBlank() }
    val checkedStates = remember { mutableStateOf(items.map { true }.toMutableList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(text = "Field Inspection Checklist", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "${task.equipmentTag} - ${task.title}", fontSize = 12.sp, color = ElectricalBlue)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.forEachIndexed { i, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = checkedStates.value.getOrElse(i) { true },
                            onCheckedChange = { checked ->
                                val updated = checkedStates.value.toMutableList()
                                if (i < updated.size) updated[i] = checked
                                checkedStates.value = updated
                            },
                            colors = CheckboxDefaults.colors(checkedColor = ElectricalBlue)
                        )
                        Text(text = item, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCompleteTask,
                colors = ButtonDefaults.buttonColors(containerColor = StatusPassGreen)
            ) {
                Text("Sign Off & Complete Task")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// Schedule PM Dialog
@Composable
fun SchedulePMDialog(
    equipmentList: List<EquipmentEntity>,
    onDismiss: () -> Unit,
    onSave: (MaintenanceTaskEntity) -> Unit
) {
    var selectedEquipment by remember { mutableStateOf(equipmentList.firstOrNull()) }
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("2026-10-15") }
    var notes by remember { mutableStateOf("") }
    var engineer by remember { mutableStateOf("Marcus Vance, PE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Preventive Maintenance (PM)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title (e.g. Contact Wear Inspection)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = engineer,
                    onValueChange = { engineer = it },
                    label = { Text("Assigned Engineer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Maintenance Scope & Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val eq = selectedEquipment ?: equipmentList.first()
                        onSave(
                            MaintenanceTaskEntity(
                                equipmentId = eq.id,
                                equipmentTag = eq.tagNumber,
                                equipmentName = eq.name,
                                title = title,
                                taskType = MaintenanceType.PREVENTIVE,
                                priority = PriorityLevel.HIGH,
                                dueDate = dueDate,
                                status = MaintenanceStatus.DUE_THIS_WEEK,
                                assignedEngineer = engineer,
                                notes = notes.ifBlank { "Standard scheduled maintenance inspection." },
                                checklistItems = "Safety clearance & lock-out verified;Visual inspection of terminals;Measurement of insulation resistance;Re-torque electrical connections"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue)
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
