package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanySettingsEntity
import com.example.data.model.EquipmentEntity
import com.example.data.model.TestRecordEntity
import com.example.data.model.TestResultStatus
import com.example.data.model.TestType
import com.example.ui.components.TestResultBadge
import com.example.ui.theme.ElectricalBlue

@Composable
fun InspectionsScreen(
    tests: List<TestRecordEntity>,
    equipmentList: List<EquipmentEntity>,
    settings: CompanySettingsEntity,
    onAddTest: (TestRecordEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddTestDialog by remember { mutableStateOf(false) }
    var previewedTest by remember { mutableStateOf<TestRecordEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                        text = "High Voltage Inspections & Field Tests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Megger, Ductor, Relay Injection & Oil Quality Logs",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddTestDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "New Test", fontSize = 12.sp)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tests, key = { it.id }) { test ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = test.testType.label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${test.equipmentTag} • ${test.equipmentName}",
                                    fontSize = 12.sp,
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
                                text = "Tested by: ${test.engineerName} (${test.testDate})",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedButton(
                                onClick = { previewedTest = test },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "View Certificate", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddTestDialog) {
        AddTestDialog(
            equipmentList = equipmentList,
            engineerName = settings.engineerName,
            onDismiss = { showAddTestDialog = false },
            onSave = {
                onAddTest(it)
                showAddTestDialog = false
            }
        )
    }

    previewedTest?.let { test ->
        ReportPreviewModal(
            test = test,
            settings = settings,
            onDismiss = { previewedTest = null },
            onPrint = { previewedTest = null }
        )
    }
}

@Composable
fun AddTestDialog(
    equipmentList: List<EquipmentEntity>,
    engineerName: String,
    onDismiss: () -> Unit,
    onSave: (TestRecordEntity) -> Unit
) {
    var selectedEq by remember { mutableStateOf(equipmentList.firstOrNull()) }
    var testType by remember { mutableStateOf(TestType.INSULATION_RESISTANCE) }
    var instrument by remember { mutableStateOf("Megger MIT525 (5kV)") }
    var findings by remember { mutableStateOf("") }
    var readings by remember { mutableStateOf("{\"Insulation Resistance HV-Earth\":\"14.8 GΩ\",\"PI Index (10m/1m)\":\"2.4\"}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record New Electrical Test Certificate") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = instrument,
                    onValueChange = { instrument = it },
                    label = { Text("Test Instrument Model & Serial") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = findings,
                    onValueChange = { findings = it },
                    label = { Text("Engineering Assessment & Summary") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = readings,
                    onValueChange = { readings = it },
                    label = { Text("Readings Summary JSON / Key-Values") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val eq = selectedEq ?: equipmentList.first()
                    onSave(
                        TestRecordEntity(
                            equipmentId = eq.id,
                            equipmentTag = eq.tagNumber,
                            equipmentName = eq.name,
                            testType = testType,
                            testDate = "2026-09-25",
                            ambientTempC = 29.0,
                            humidityPercent = 45.0,
                            instrumentModel = instrument,
                            instrumentCalibDate = "2026-03-10",
                            testResult = TestResultStatus.PASS,
                            summaryFindings = findings.ifBlank { "All measured test values comply with IEC standards." },
                            readingsJson = readings,
                            engineerName = engineerName,
                            clientWitness = "National Grid Authorized Inspector"
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue)
            ) {
                Text("Save Certificate")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
