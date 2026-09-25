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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanySettingsEntity
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    settings: CompanySettingsEntity,
    currentThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onSaveSettings: (CompanySettingsEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var companyName by remember(settings) { mutableStateOf(settings.companyName) }
    var currentProject by remember(settings) { mutableStateOf(settings.currentProject) }
    var clientName by remember(settings) { mutableStateOf(settings.clientName) }
    var engineerName by remember(settings) { mutableStateOf(settings.engineerName) }
    var engineerLicense by remember(settings) { mutableStateOf(settings.engineerLicense) }
    var companyAddress by remember(settings) { mutableStateOf(settings.companyAddress) }
    var contactEmail by remember(settings) { mutableStateOf(settings.contactEmail) }
    var reportHeader by remember(settings) { mutableStateOf(settings.reportHeader) }
    var reportFooter by remember(settings) { mutableStateOf(settings.reportFooter) }
    var savedNotice by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
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
                        text = "Company Branding & Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Customize Corporate Identity, Theme, and Report Certification",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        onSaveSettings(
                            settings.copy(
                                companyName = companyName,
                                currentProject = currentProject,
                                clientName = clientName,
                                engineerName = engineerName,
                                engineerLicense = engineerLicense,
                                companyAddress = companyAddress,
                                contactEmail = contactEmail,
                                reportHeader = reportHeader,
                                reportFooter = reportFooter
                            )
                        )
                        savedNotice = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Save", fontSize = 12.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (savedNotice) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF14532D),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4ADE80))
                        Text(
                            text = "Settings & corporate branding updated across all modules and reports.",
                            color = Color(0xFFDCFCE7),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Theme Switcher Card (Light, Dark, System Default)
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Brightness4, contentDescription = null, tint = ElectricalBlue)
                        Text(text = "Interface Color Theme", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Text(
                        text = "Choose between industrial high-contrast dark mode, clean light mode, or match system settings.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            ThemeMode.SYSTEM to "System Default",
                            ThemeMode.LIGHT to "Light Mode",
                            ThemeMode.DARK to "Dark Navy Mode"
                        ).forEach { (mode, title) ->
                            val isSel = currentThemeMode == mode
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) ElectricalBlue else MaterialTheme.colorScheme.surface,
                                border = if (isSel) null else CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onThemeModeChange(mode) }
                            ) {
                                Text(
                                    text = title,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Company Profile & Entity Details
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = ElectricalBlue)
                        Text(text = "Engineering Organization Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Company / Organization Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = currentProject,
                        onValueChange = { currentProject = it },
                        label = { Text("Active Substation / Facility Project") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Client / Grid Authority Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = companyAddress,
                        onValueChange = { companyAddress = it },
                        label = { Text("Official Engineering Office Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = { contactEmail = it },
                        label = { Text("Official Contact Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Lead Engineer & Certification Credentials
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = ElectricalBlue)
                        Text(text = "Lead Protection & Commissioning Engineer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    OutlinedTextField(
                        value = engineerName,
                        onValueChange = { engineerName = it },
                        label = { Text("Engineer Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = engineerLicense,
                        onValueChange = { engineerLicense = it },
                        label = { Text("Professional License / Credentials") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Report Header & Certification Footers
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Test Certificate Branding & Standards Declaration", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    OutlinedTextField(
                        value = reportHeader,
                        onValueChange = { reportHeader = it },
                        label = { Text("Official Report Title Header") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = reportFooter,
                        onValueChange = { reportFooter = it },
                        label = { Text("Standards Compliance Disclaimer / Footer") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
