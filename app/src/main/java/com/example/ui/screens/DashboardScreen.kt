package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanySettingsEntity
import com.example.data.model.EquipmentCategory
import com.example.data.model.EquipmentEntity
import com.example.data.model.FaultRecordEntity
import com.example.data.model.FaultStatus
import com.example.data.model.MaintenanceStatus
import com.example.data.model.MaintenanceTaskEntity
import com.example.ui.components.EngineeringHeroSection
import com.example.ui.components.EquipmentCategoryCard
import com.example.ui.components.MaintenanceSummaryMetrics
import com.example.ui.components.NavDestination
import com.example.ui.components.SolarProductionCurve
import com.example.ui.components.TransformerLoadingChart
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusWarningAmber

@Composable
fun DashboardScreen(
    equipmentList: List<EquipmentEntity>,
    tasks: List<MaintenanceTaskEntity>,
    faults: List<FaultRecordEntity>,
    settings: CompanySettingsEntity,
    onNavigate: (NavDestination) -> Unit,
    onOpenCategory: (EquipmentCategory) -> Unit,
    onOpenEquipmentDetail: (EquipmentEntity) -> Unit,
    onAddEquipmentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dueToday = tasks.count { it.status == MaintenanceStatus.DUE_TODAY }
    val dueWeek = tasks.count { it.status == MaintenanceStatus.DUE_THIS_WEEK }
    val overdue = tasks.count { it.status == MaintenanceStatus.OVERDUE }
    val completed = tasks.count { it.status == MaintenanceStatus.COMPLETED }
    val openFaults = faults.filter { it.status != FaultStatus.RESOLVED }

    var showSystemOnlineDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section
        EngineeringHeroSection(
            onOpenCalculators = { onNavigate(NavDestination.CALCULATORS) },
            onStartInspection = { onNavigate(NavDestination.INSPECTIONS) },
            onAddEquipment = onAddEquipmentClick,
            onCreateReport = { onNavigate(NavDestination.REPORTS) },
            onOpenScada = { showSystemOnlineDialog = true }
        )

        // Active Trip / Alarm Alert Banner (if any)
        if (openFaults.isNotEmpty()) {
            val primaryAlert = openFaults.first()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigate(NavDestination.FAULT_ANALYSIS) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(Color(0xFFDC2626), Color(0xFF991B1B))
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFDC2626), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CRITICAL PROTECTION EVENT ACTIVE",
                            color = Color(0xFFFCA5A5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${primaryAlert.title} (${primaryAlert.equipmentTag})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Trip Relay: ${primaryAlert.tripRelayFunction} • Breaker: ${primaryAlert.breakerStatus}",
                            color = Color(0xFFFECACA),
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Fault",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Section: Maintenance Tasks Overview Metrics
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MAINTENANCE & RELIABILITY METRICS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "View All Tasks",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricalBlue,
                    modifier = Modifier.clickable { onNavigate(NavDestination.MAINTENANCE) }
                )
            }

            MaintenanceSummaryMetrics(
                dueTodayCount = dueToday,
                dueWeekCount = dueWeek,
                overdueCount = overdue,
                completedCount = completed,
                onFilterClick = { onNavigate(NavDestination.MAINTENANCE) }
            )
        }

        // Live SCADA Telemetry & Relay Coordination Quick Action Panels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(NavDestination.SCADA_TELEMETRY) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Sensors, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(18.dp))
                        Text(text = "SCADA Live Feed", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Text(text = "Real-time parameters for TR-01 & CB-102 (Modbus/IEC)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Live Stream Active →", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricalBlue)
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(NavDestination.PROTECTION_COORDINATION) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(18.dp))
                        Text(text = "Relay TCC Curves", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Text(text = "Time-Current coordination & grading margin verification", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Verify Selectivity →", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0EA5E9))
                }
            }
        }

        // Substation Power Transformer Real-Time 24h Loading Chart
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TransformerLoadingChart()
        }

        // Equipment Categories Cards Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ELECTRICAL ASSET MODULES",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "View All (${equipmentList.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricalBlue,
                    modifier = Modifier.clickable { onNavigate(NavDestination.EQUIPMENT) }
                )
            }

            // Category Cards
            val categoriesWithDescriptions = listOf(
                EquipmentCategory.TRANSFORMER to "132 kV & 33 kV step-down/step-up oil-immersed power transformers, bushings, radiators, OLTC & Buchholz protection.",
                EquipmentCategory.CIRCUIT_BREAKER to "33 kV & 11 kV indoor/outdoor vacuum circuit breakers (VCB), SF6 switchgear panels, and spring mechanisms.",
                EquipmentCategory.PROTECTION_RELAY to "ANSI numerical protection relays (REF615, 7SJ, SEL), CT/VT analog secondary injection, and trip logic matrix.",
                EquipmentCategory.CABLE to "33 kV & 11 kV XLPE, EPR, armoured power cables, jointing kits, stress terminations, and Tan-Delta diagnostic tests.",
                EquipmentCategory.MOTOR to "Medium & Low voltage three-phase induction motors, Motor Control Centers (MCC), VFDs, and soft starter units.",
                EquipmentCategory.SOLAR_PV to "Utility-scale central inverter skids, 1500V DC combiner boxes, MV cast-resin transformers, and solar PV strings."
            )

            categoriesWithDescriptions.forEach { (cat, desc) ->
                val count = equipmentList.count { it.category == cat }
                EquipmentCategoryCard(
                    category = cat,
                    description = desc,
                    assetCount = count,
                    onOpenModule = { onOpenCategory(cat) }
                )
            }
        }

        // Solar Generation Profile Chart
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            SolarProductionCurve()
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Interactive SCADA Telemetry & Substation System Status Modal
    if (showSystemOnlineDialog) {
        AlertDialog(
            onDismissRequest = { showSystemOnlineDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF22C55E), CircleShape)
                    )
                    Text("Substation SCADA System Status", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF16A34A).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(18.dp))
                            Column {
                                Text("SYSTEM ONLINE & TELEMETRY STREAMING", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF22C55E))
                                Text("IEC 61850 MMS & Modbus TCP live field telemetry links healthy", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Substation Bus Voltage:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("132.4 kV / 33.1 kV (Nominal)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grid System Frequency:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("50.02 Hz (Synchronized)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Active Bay Controllers:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("6 / 6 Communicating", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF22C55E))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Telemetry Polling Rate:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("1,000 ms (Real-time)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Active Protection Lockout:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("1 Trip Latched (33-CB-102)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = StatusCriticalRed)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSystemOnlineDialog = false
                        onNavigate(NavDestination.SCADA_TELEMETRY)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue)
                ) {
                    Text("Open SCADA Live Telemetry")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSystemOnlineDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
