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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssetStatus
import com.example.data.model.CriticalityLevel
import com.example.data.model.EquipmentCategory
import com.example.data.model.EquipmentEntity
import com.example.ui.components.AssetStatusBadge
import com.example.ui.components.CriticalityBadge
import com.example.ui.components.EngineeringBlueprintCanvas
import com.example.ui.theme.ElectricalBlue

@Composable
fun EquipmentListScreen(
    equipmentList: List<EquipmentEntity>,
    selectedCategoryFilter: EquipmentCategory?,
    onCategoryFilterChange: (EquipmentCategory?) -> Unit,
    searchQuery: String,
    onSelectEquipment: (EquipmentEntity) -> Unit,
    onAddEquipment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredList = equipmentList.filter { item ->
        val matchesCategory = selectedCategoryFilter == null || item.category == selectedCategoryFilter
        val matchesSearch = searchQuery.isBlank() ||
                item.tagNumber.contains(searchQuery, ignoreCase = true) ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true) ||
                item.manufacturer.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Category filter tab bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                val categories = listOf(null) + EquipmentCategory.values().toList()
                val selectedIndex = categories.indexOf(selectedCategoryFilter).coerceAtLeast(0)

                ScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = ElectricalBlue
                ) {
                    categories.forEachIndexed { index, cat ->
                        Tab(
                            selected = index == selectedIndex,
                            onClick = { onCategoryFilterChange(cat) },
                            text = {
                                Text(
                                    text = cat?.displayName ?: "All Assets (${equipmentList.size})",
                                    fontSize = 12.sp,
                                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            if (filteredList.isEmpty()) {
                // Empty state matching prompt requirements
                EquipmentEmptyState(onAddEquipment = onAddEquipment)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { equipment ->
                        EquipmentListItemCard(
                            equipment = equipment,
                            onClick = { onSelectEquipment(equipment) }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddEquipment,
            containerColor = ElectricalBlue,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Equipment")
        }
    }
}

@Composable
fun EquipmentListItemCard(
    equipment: EquipmentEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Blueprint Preview Box
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color(0xFF0C1B2E), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                EngineeringBlueprintCanvas(category = equipment.category)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = equipment.tagNumber,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    CriticalityBadge(level = equipment.criticality)
                }

                Text(
                    text = equipment.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Text(
                    text = "${equipment.location} • ${equipment.voltageLevel} • ${equipment.rating}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssetStatusBadge(status = equipment.status)
                    Text(
                        text = "Next PM: ${equipment.nextMaintenanceDate.ifBlank { "N/A" }}",
                        fontSize = 10.sp,
                        color = ElectricalBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun EquipmentEmptyState(onAddEquipment: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF0F2238), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.PrecisionManufacturing,
                contentDescription = null,
                tint = ElectricalBlue,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Equipment Added Yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Add your first power transformer, circuit breaker, protection relay, cable, or motor to start tracking engineering maintenance and inspections.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onAddEquipment,
            colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
            shape = RoundedCornerShape(6.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Add Equipment", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEquipmentDialog(
    onDismiss: () -> Unit,
    onSave: (EquipmentEntity) -> Unit
) {
    var tagNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(EquipmentCategory.TRANSFORMER) }
    var location by remember { mutableStateOf("") }
    var voltageLevel by remember { mutableStateOf("33 kV") }
    var rating by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var criticality by remember { mutableStateOf(CriticalityLevel.HIGH) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Electrical Asset", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = tagNumber,
                    onValueChange = { tagNumber = it },
                    label = { Text("Tag Number (e.g. 132-TR-02)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Asset Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Functional Location / Bay") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = voltageLevel,
                        onValueChange = { voltageLevel = it },
                        label = { Text("Voltage Level") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = rating,
                        onValueChange = { rating = it },
                        label = { Text("Rating (e.g. 40 MVA)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = manufacturer,
                        onValueChange = { manufacturer = it },
                        label = { Text("Manufacturer") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model Number") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tagNumber.isNotBlank() && name.isNotBlank()) {
                        onSave(
                            EquipmentEntity(
                                tagNumber = tagNumber,
                                name = name,
                                category = category,
                                location = location.ifBlank { "Substation Yard" },
                                voltageLevel = voltageLevel,
                                rating = rating.ifBlank { "Standard Industrial Rating" },
                                manufacturer = manufacturer.ifBlank { "ABB / Siemens" },
                                model = model.ifBlank { "Standard Series" },
                                serialNumber = "SN-${System.currentTimeMillis() % 100000}",
                                commissioningDate = "2026-09-25",
                                status = AssetStatus.NORMAL,
                                criticality = criticality,
                                specificationsJson = "{\"Rated Voltage\":\"$voltageLevel\",\"Capacity\":\"$rating\"}",
                                photoSearchQuery = "${category.searchPhrase} industrial"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue)
            ) {
                Text("Register Asset")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
