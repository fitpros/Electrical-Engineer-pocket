package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EquipmentCategory
import com.example.data.model.EquipmentEntity
import com.example.data.model.FaultRecordEntity
import com.example.data.model.FaultSeverity
import com.example.data.model.FaultStatus
import com.example.ui.components.FaultSeverityBadge
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusPassGreen
import com.example.ui.theme.StatusWarningAmber

@Composable
fun FaultAnalysisScreen(
    faults: List<FaultRecordEntity>,
    equipmentList: List<EquipmentEntity>,
    onAddFault: (FaultRecordEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddFaultDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Diagnostic Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Fault Diagnostics & Event Root-Cause Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Structured 3-Tier Diagnostic Investigation (IEC / IEEE C37)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddFaultDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCriticalRed),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Log Fault Event", fontSize = 12.sp)
                }
            }
        }

        // Subsystem Telemetry Status Strip with requested Industrial Icons
        DiagnosticTelemetryStrip(
            openTripsCount = faults.count { it.status != FaultStatus.RESOLVED }
        )

        // Diagnostic Records
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(faults, key = { it.id }) { fault ->
                DiagnosticFaultCard(fault = fault)
            }
        }
    }

    if (showAddFaultDialog) {
        AddFaultDialog(
            equipmentList = equipmentList,
            onDismiss = { showAddFaultDialog = false },
            onSave = { newFault ->
                onAddFault(newFault)
                showAddFaultDialog = false
            }
        )
    }
}

@Composable
fun DiagnosticTelemetryStrip(openTripsCount: Int) {
    Surface(
        color = Color(0xFF0C192C),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TelemetryIndicator(icon = Icons.Default.Shield, label = "Protection Trip", value = if (openTripsCount > 0) "$openTripsCount Active" else "Nominal", isWarning = openTripsCount > 0)
            TelemetryIndicator(icon = Icons.Default.Power, label = "Breaker / VCB", value = "Interlocked", isWarning = false)
            TelemetryIndicator(icon = Icons.Default.ElectricBolt, label = "Transformer", value = "Online", isWarning = false)
            TelemetryIndicator(icon = Icons.Default.BatteryChargingFull, label = "110V DC Supply", value = "124 V DC", isWarning = false)
            TelemetryIndicator(icon = Icons.Default.Sensors, label = "SCADA / RTU", value = "Connected", isWarning = false)
        }
    }
}

@Composable
fun TelemetryIndicator(icon: ImageVector, label: String, value: String, isWarning: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isWarning) StatusCriticalRed else Color(0xFF38BDF8),
            modifier = Modifier.size(15.dp)
        )
        Column {
            Text(text = label, fontSize = 9.sp, color = Color(0xFF94A3B8))
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isWarning) StatusCriticalRed else Color.White
            )
        }
    }
}

// 3-Section Diagnostic Layout Card
@Composable
fun DiagnosticFaultCard(fault: FaultRecordEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fault.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Asset: ${fault.equipmentTag} (${fault.equipmentName}) • ${fault.timestamp}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FaultSeverityBadge(severity = fault.severity)
            }

            // Relay & Breaker Status bar
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF0F1E33),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Relay Trip Function: ${fault.tripRelayFunction}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "Breaker: ${fault.breakerStatus}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (fault.breakerStatus.contains("Tripped") || fault.breakerStatus.contains("Lock")) StatusCriticalRed else StatusPassGreen
                    )
                }
            }

            // 3 Diagnostic Columns (Stacked on mobile for responsiveness)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tier 1: Observed Information
                DiagnosticPanel(
                    title = "1. OBSERVED INFORMATION & ALARMS",
                    content = fault.observedInfo,
                    titleColor = ElectricalBlue,
                    bgColor = Color(0xFF0B192C)
                )

                // Tier 2: Possible Causes and Inspection Checks
                DiagnosticPanel(
                    title = "2. POSSIBLE CAUSES & CHECKS",
                    content = fault.possibleCauses,
                    titleColor = StatusWarningAmber,
                    bgColor = Color(0xFF141F2E)
                )

                // Tier 3: Measurements and Findings
                DiagnosticPanel(
                    title = "3. MEASUREMENTS & FINDINGS",
                    content = fault.measurementsFindings,
                    titleColor = Color(0xFF38BDF8),
                    bgColor = Color(0xFF0A1C30)
                )
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

            // Root cause & Corrective Action
            if (fault.rootCause.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Root Cause:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusCriticalRed
                    )
                    Text(
                        text = fault.rootCause,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (fault.correctiveAction.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Corrective Action & Remediation:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusPassGreen
                    )
                    Text(
                        text = fault.correctiveAction,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun DiagnosticPanel(
    title: String,
    content: String,
    titleColor: Color,
    bgColor: Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, titleColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = titleColor, letterSpacing = 0.5.sp)
            Text(text = content, fontSize = 11.sp, color = Color(0xFFE2E8F0), lineHeight = 16.sp)
        }
    }
}

@Composable
fun AddFaultDialog(
    equipmentList: List<EquipmentEntity>,
    onDismiss: () -> Unit,
    onSave: (FaultRecordEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var tripFunction by remember { mutableStateOf("ANSI 50/51 Overcurrent") }
    var observedInfo by remember { mutableStateOf("") }
    var possibleCauses by remember { mutableStateOf("") }
    var measurements by remember { mutableStateOf("") }
    var rootCause by remember { mutableStateOf("") }
    var correctiveAction by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Substation Fault / Trip Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Fault Event Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tripFunction,
                    onValueChange = { tripFunction = it },
                    label = { Text("Relay Trip Function (e.g. 87T, 50/51)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = observedInfo,
                    onValueChange = { observedInfo = it },
                    label = { Text("1. Observed Info / SCADA Alarms") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = possibleCauses,
                    onValueChange = { possibleCauses = it },
                    label = { Text("2. Possible Causes & Checks") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = measurements,
                    onValueChange = { measurements = it },
                    label = { Text("3. Measurements & Findings") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val eq = equipmentList.firstOrNull()
                        onSave(
                            FaultRecordEntity(
                                equipmentId = eq?.id ?: 1L,
                                equipmentTag = eq?.tagNumber ?: "132-TR-01",
                                equipmentName = eq?.name ?: "Main Transformer",
                                equipmentCategory = eq?.category ?: EquipmentCategory.TRANSFORMER,
                                title = title,
                                timestamp = "2026-09-25 09:30:00 UTC",
                                severity = FaultSeverity.PROTECTION_TRIP,
                                status = FaultStatus.INVESTIGATING,
                                tripRelayFunction = tripFunction,
                                breakerStatus = "Tripped / Open",
                                observedInfo = observedInfo.ifBlank { "Relay tripped breaker on high phase current." },
                                possibleCauses = possibleCauses.ifBlank { "Downstream feeder breakdown or internal fault." },
                                measurementsFindings = measurements.ifBlank { "Megger test in progress." },
                                rootCause = rootCause,
                                correctiveAction = correctiveAction
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusCriticalRed)
            ) {
                Text("Log Event")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
