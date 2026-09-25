package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricalBlue

enum class NavDestination(val label: String, val icon: ImageVector, val section: String = "MAIN") {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "OVERVIEW"),
    USER_DASHBOARD("Engineer Profile & Stats", Icons.Default.Person, "ACCOUNT"),
    SCADA_TELEMETRY("SCADA Live Telemetry", Icons.Default.Sensors, "OPERATIONS"),
    PROTECTION_COORDINATION("Relay Coordination (TCC)", Icons.Default.Shield, "ENGINEERING"),
    CALCULATORS("Calculators Suite", Icons.Default.Calculate, "ENGINEERING"),
    EQUIPMENT("All Equipment", Icons.Default.PrecisionManufacturing, "ASSETS"),
    TRANSFORMER("Transformers", Icons.Default.Power, "ASSETS"),
    PROTECTION("Protection Relays", Icons.Default.Shield, "ASSETS"),
    CABLE("Cables & Feeders", Icons.Default.ElectricBolt, "ASSETS"),
    MOTOR("Motors & VFDs", Icons.Default.Speed, "ASSETS"),
    SOLAR("Solar PV & Inverters", Icons.Default.SolarPower, "ASSETS"),
    MAINTENANCE("PM & PdM Tasks", Icons.Default.Build, "OPERATIONS"),
    FAULT_ANALYSIS("Fault Analysis", Icons.Default.Warning, "OPERATIONS"),
    INSPECTIONS("Inspections & Tests", Icons.Default.FactCheck, "OPERATIONS"),
    REPORTS("Reports & Certificates", Icons.Default.Assessment, "DOCUMENTATION"),
    DOCUMENTS("Tech Specs & Manuals", Icons.Default.Description, "DOCUMENTATION"),
    HISTORY("Audit & Event Log", Icons.Default.History, "DOCUMENTATION"),
    ADMIN_PANEL("Admin & Cloud Sync", Icons.Default.AdminPanelSettings, "ADMINISTRATION"),
    ABOUT_DEVELOPER("About Developer (Imran)", Icons.Default.Info, "ABOUT & SUPPORT"),
    PUBLIC_PORTAL("Public Portal & Features", Icons.Default.Public, "ABOUT & SUPPORT"),
    SETTINGS("Company & Settings", Icons.Default.Settings, "SYSTEM")
}

@Composable
fun NavigationDrawerContent(
    currentDestination: NavDestination,
    onSelectDestination: (NavDestination) -> Unit,
    activeFaultsCount: Int,
    dueMaintenanceCount: Int,
    companyName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp),
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Brand Drawer Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF0A192F), RoundedCornerShape(8.dp))
                        .border(1.5.dp, ElectricalBlue, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Electrical Engineer Pro",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = companyName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(4.dp))

            // Navigation Items grouped
            var lastSection = ""
            NavDestination.values().forEach { dest ->
                if (dest.section != lastSection) {
                    lastSection = dest.section
                    Text(
                        text = dest.section,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
                    )
                }

                val isSelected = currentDestination == dest
                val badgeCount = when (dest) {
                    NavDestination.FAULT_ANALYSIS -> if (activeFaultsCount > 0) activeFaultsCount else null
                    NavDestination.MAINTENANCE -> if (dueMaintenanceCount > 0) dueMaintenanceCount else null
                    else -> null
                }

                NavMenuItem(
                    item = dest,
                    isSelected = isSelected,
                    badgeCount = badgeCount,
                    onClick = { onSelectDestination(dest) }
                )
            }
        }
    }
}

@Composable
fun NavMenuItem(
    item: NavDestination,
    isSelected: Boolean,
    badgeCount: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) ElectricalBlue.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) ElectricalBlue else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) ElectricalBlue.copy(alpha = 0.4f) else Color.Transparent

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, borderColor) else null,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    item.icon,
                    contentDescription = item.label,
                    tint = contentColor,
                    modifier = Modifier.size(19.dp)
                )
                Text(
                    text = item.label,
                    color = contentColor,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }

            if (badgeCount != null) {
                Box(
                    modifier = Modifier
                        .background(
                            if (item == NavDestination.FAULT_ANALYSIS) Color(0xFFDC2626) else Color(0xFFD97706),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
