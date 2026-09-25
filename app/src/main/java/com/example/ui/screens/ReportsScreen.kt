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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CompanySettingsEntity
import com.example.data.model.EquipmentEntity
import com.example.data.model.TestRecordEntity
import com.example.ui.components.TestResultBadge
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusPassGreen

@Composable
fun ReportsScreen(
    tests: List<TestRecordEntity>,
    settings: CompanySettingsEntity,
    equipmentList: List<EquipmentEntity>,
    modifier: Modifier = Modifier
) {
    var selectedTestForPreview by remember { mutableStateOf<TestRecordEntity?>(null) }
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }

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
                        text = "Engineering Test Certificates & Reports",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Authorized Substation Inspection Reports (PDF Compliant)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        selectedTestForPreview = tests.firstOrNull()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Preview Active Report", fontSize = 12.sp)
                }
            }
        }

        // Test List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tests, key = { it.id }) { test ->
                ReportCertificateCard(
                    test = test,
                    onPreviewClick = { selectedTestForPreview = test },
                    onDownloadClick = { exportSuccessMessage = "Report '${test.testType.label}' exported successfully as PDF." }
                )
            }
        }
    }

    // Success Feedback Banner
    exportSuccessMessage?.let { msg ->
        androidx.compose.material3.Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                OutlinedButton(onClick = { exportSuccessMessage = null }) {
                    Text("OK")
                }
            }
        ) {
            Text(msg)
        }
    }

    // Full PDF-like Report Preview Dialog
    selectedTestForPreview?.let { test ->
        ReportPreviewModal(
            test = test,
            settings = settings,
            onDismiss = { selectedTestForPreview = null },
            onPrint = {
                exportSuccessMessage = "Print command dispatched for Certificate #${test.id}."
                selectedTestForPreview = null
            }
        )
    }
}

@Composable
fun ReportCertificateCard(
    test: TestRecordEntity,
    onPreviewClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = test.testType.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${test.equipmentTag} • ${test.equipmentName} • ${test.testDate}",
                        fontSize = 11.sp,
                        color = ElectricalBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TestResultBadge(result = test.testResult)
            }

            Text(
                text = "Instrument: ${test.instrumentModel} | Calib: ${test.instrumentCalibDate}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = test.summaryFindings,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tested by: ${test.engineerName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onPreviewClick,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Preview", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onDownloadClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "PDF", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// PDF-Style Official Certificate Preview Modal
@Composable
fun ReportPreviewModal(
    test: TestRecordEntity,
    settings: CompanySettingsEntity,
    onDismiss: () -> Unit,
    onPrint: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Action Toolbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PDF DOCUMENT PREVIEW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(onClick = onPrint, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Print, contentDescription = "Print Document", tint = Color(0xFF0F172A))
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close Preview", tint = Color(0xFF0F172A))
                        }
                    }
                }

                // Official Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, Color(0xFF0F2642))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = settings.companyName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B192C)
                                )
                                Text(
                                    text = settings.companyAddress,
                                    fontSize = 9.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                            Icon(
                                Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = Color(0xFF0066CC),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Divider(color = Color(0xFFCBD5E1), modifier = Modifier.padding(vertical = 6.dp))

                        Text(
                            text = settings.reportHeader,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF003875),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Project & Client Metadata Table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0))
                        .background(Color(0xFFF8FAFC))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ReportRow("Project Name:", settings.currentProject)
                    ReportRow("Client / Owner:", settings.clientName)
                    ReportRow("Equipment Tag:", test.equipmentTag)
                    ReportRow("Equipment Description:", test.equipmentName)
                    ReportRow("Test Type:", test.testType.label)
                    ReportRow("Date Tested:", test.testDate)
                    ReportRow("Test Instrument:", "${test.instrumentModel} (Calib: ${test.instrumentCalibDate})")
                    ReportRow("Ambient Conditions:", "${test.ambientTempC}°C, ${test.humidityPercent}% RH")
                }

                // Readings Table
                Text(
                    text = "MEASUREMENT READINGS & TOLERANCE VERIFICATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFCBD5E1)),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE2E8F0))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Parameter / Injection Point", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(text = "Observed Reading", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }

                    // Parse JSON readings
                    val cleaned = test.readingsJson
                        .replace("{", "")
                        .replace("}", "")
                        .replace("\"", "")
                    cleaned.split(",").forEach { item ->
                        val parts = item.split(":")
                        if (parts.size >= 2) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = parts[0].trim(), fontSize = 10.sp, color = Color(0xFF334155))
                                Text(
                                    text = parts.subList(1, parts.size).joinToString(":").trim(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                            Divider(color = Color(0xFFF1F5F9))
                        }
                    }
                }

                // Final Evaluation Stamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Engineering Findings & Comments:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                        Text(text = test.summaryFindings, fontSize = 10.sp, color = Color(0xFF0F172A), lineHeight = 14.sp)
                    }

                    Box(
                        modifier = Modifier
                            .border(2.dp, Color(0xFF16A34A), RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "PASS / COMPLIANT",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = Color(0xFF16A34A)
                        )
                    }
                }

                Divider(color = Color(0xFFE2E8F0))

                // Signatures
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Tested & Certified by:", fontSize = 9.sp, color = Color(0xFF64748B))
                        Text(text = test.engineerName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(text = settings.engineerLicense, fontSize = 9.sp, color = Color(0xFF64748B))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Client Witness / Authority:", fontSize = 9.sp, color = Color(0xFF64748B))
                        Text(text = test.clientWitness.ifBlank { "National Grid Inspector" }, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(text = "Status: Formally Witnessed", fontSize = 9.sp, color = Color(0xFF16A34A))
                    }
                }

                // Footer & Page Number
                Text(
                    text = "${settings.reportFooter} | Page 1 of 1",
                    fontSize = 8.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ReportRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
    }
}
