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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.calculator.CalculationResult
import com.example.calculator.ElectricalCalculators
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.ElectricalBlueCyan
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusPassGreen
import com.example.ui.theme.StatusWarningAmber

enum class CalculatorType(val title: String) {
    FULL_LOAD_CURRENT("Full Load Current"),
    TRANSFORMER_FAULT("Transformer & Fault Level"),
    VOLTAGE_DROP("Voltage Drop & Run Length"),
    CABLE_DERATING("Cable Ampacity Derating"),
    RELAY_TRIP_TIME("IEC Relay Tripping Time"),
    MOTOR_STARTING("Motor Starting Inrush")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorsScreen(
    modifier: Modifier = Modifier
) {
    var selectedCalc by remember { mutableStateOf(CalculatorType.FULL_LOAD_CURRENT) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Module Top Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(ElectricalBlue.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            tint = ElectricalBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Engineering Calculations Suite",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Standard IEC 60076, IEC 60255, IEC 60364 & IEEE Standards",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                ScrollableTabRow(
                    selectedTabIndex = selectedCalc.ordinal,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = ElectricalBlue
                ) {
                    CalculatorType.values().forEach { calc ->
                        Tab(
                            selected = selectedCalc == calc,
                            onClick = { selectedCalc = calc },
                            text = {
                                Text(
                                    text = calc.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedCalc == calc) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        // Main Calculation Work Area: Input Panel + Results Panel
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedCalc) {
                CalculatorType.FULL_LOAD_CURRENT -> FullLoadCurrentCalculatorView()
                CalculatorType.TRANSFORMER_FAULT -> TransformerFaultCalculatorView()
                CalculatorType.VOLTAGE_DROP -> VoltageDropCalculatorView()
                CalculatorType.CABLE_DERATING -> CableDeratingCalculatorView()
                CalculatorType.RELAY_TRIP_TIME -> RelayTripTimeCalculatorView()
                CalculatorType.MOTOR_STARTING -> MotorStartingCalculatorView()
            }
        }
    }
}

// --- 1. Full Load Current Calculator ---
@Composable
fun FullLoadCurrentCalculatorView() {
    var powerKw by remember { mutableStateOf("150") }
    var voltage by remember { mutableStateOf("400") }
    var powerFactor by remember { mutableStateOf("0.85") }
    var efficiency by remember { mutableStateOf("95.0") }
    var result by remember {
        mutableStateOf(
            ElectricalCalculators.calculateFullLoadCurrent(150.0, 400.0, 0.85, 95.0)
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CalculatorInputCard(
            title = "Three-Phase Load Parameters (IEC 60034)",
            onCalculate = {
                val p = powerKw.toDoubleOrNull() ?: 150.0
                val v = voltage.toDoubleOrNull() ?: 400.0
                val pf = powerFactor.toDoubleOrNull() ?: 0.85
                val eff = efficiency.toDoubleOrNull() ?: 95.0
                result = ElectricalCalculators.calculateFullLoadCurrent(p, v, pf, eff)
            }
        ) {
            EngineeringInputField(label = "Active Power", value = powerKw, onValueChange = { powerKw = it }, unit = "kW", tooltip = "Total 3-phase real power")
            EngineeringInputField(label = "Line-to-Line Voltage", value = voltage, onValueChange = { voltage = it }, unit = "V", tooltip = "Rated nominal system voltage")
            EngineeringInputField(label = "Power Factor (cos φ)", value = powerFactor, onValueChange = { powerFactor = it }, unit = "lag", tooltip = "Operating power factor (0.1 - 1.0)")
            EngineeringInputField(label = "Motor / Drive Efficiency (η)", value = efficiency, onValueChange = { efficiency = it }, unit = "%", tooltip = "Electrical to mechanical conversion efficiency")
        }

        CalculatorResultsCard(result = result)
    }
}

// --- 2. Transformer Fault Level Calculator ---
@Composable
fun TransformerFaultCalculatorView() {
    var ratingKva by remember { mutableStateOf("40000") } // 40 MVA = 40,000 kVA
    var secondaryVoltage by remember { mutableStateOf("33000") } // 33 kV
    var impedanceZ by remember { mutableStateOf("12.45") }
    var result by remember {
        mutableStateOf(
            ElectricalCalculators.calculateTransformerFault(40000.0, 33000.0, 12.45)
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CalculatorInputCard(
            title = "Transformer Nameplate Ratings (IEC 60076)",
            onCalculate = {
                val s = ratingKva.toDoubleOrNull() ?: 40000.0
                val v = secondaryVoltage.toDoubleOrNull() ?: 33000.0
                val z = impedanceZ.toDoubleOrNull() ?: 12.45
                result = ElectricalCalculators.calculateTransformerFault(s, v, z)
            }
        ) {
            EngineeringInputField(label = "Transformer Rating", value = ratingKva, onValueChange = { ratingKva = it }, unit = "kVA", tooltip = "Nominal rated continuous capacity")
            EngineeringInputField(label = "Secondary Rated Voltage", value = secondaryVoltage, onValueChange = { secondaryVoltage = it }, unit = "V", tooltip = "Low/Medium voltage side line voltage")
            EngineeringInputField(label = "Percent Impedance (%Z)", value = impedanceZ, onValueChange = { impedanceZ = it }, unit = "%", tooltip = "Short-circuit impedance at reference temp (typically 5% - 15%)")
        }

        CalculatorResultsCard(result = result)
    }
}

// --- 3. Voltage Drop Calculator ---
@Composable
fun VoltageDropCalculatorView() {
    var currentAmps by remember { mutableStateOf("120") }
    var voltageVolts by remember { mutableStateOf("400") }
    var lengthMeters by remember { mutableStateOf("250") }
    var resistancePerKm by remember { mutableStateOf("0.193") } // 120 mm2 Cu
    var reactancePerKm by remember { mutableStateOf("0.082") }
    var powerFactor by remember { mutableStateOf("0.85") }
    var result by remember {
        mutableStateOf(
            ElectricalCalculators.calculateVoltageDrop(120.0, 400.0, 250.0, 0.193, 0.082, 0.85)
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CalculatorInputCard(
            title = "Feeder Cable Route & Impedance Parameters",
            onCalculate = {
                val i = currentAmps.toDoubleOrNull() ?: 120.0
                val v = voltageVolts.toDoubleOrNull() ?: 400.0
                val l = lengthMeters.toDoubleOrNull() ?: 250.0
                val r = resistancePerKm.toDoubleOrNull() ?: 0.193
                val x = reactancePerKm.toDoubleOrNull() ?: 0.082
                val pf = powerFactor.toDoubleOrNull() ?: 0.85
                result = ElectricalCalculators.calculateVoltageDrop(i, v, l, r, x, pf)
            }
        ) {
            EngineeringInputField(label = "Design Load Current (Ib)", value = currentAmps, onValueChange = { currentAmps = it }, unit = "A", tooltip = "Continuous balanced design current")
            EngineeringInputField(label = "Nominal System Voltage", value = voltageVolts, onValueChange = { voltageVolts = it }, unit = "V", tooltip = "Supply voltage at source bus")
            EngineeringInputField(label = "One-Way Route Length", value = lengthMeters, onValueChange = { lengthMeters = it }, unit = "m", tooltip = "Distance from switchgear to load center")
            EngineeringInputField(label = "AC Conductor Resistance (R)", value = resistancePerKm, onValueChange = { resistancePerKm = it }, unit = "Ω/km", tooltip = "Resistance per kilometer at 90°C")
            EngineeringInputField(label = "Conductor Reactance (X)", value = reactancePerKm, onValueChange = { reactancePerKm = it }, unit = "Ω/km", tooltip = "Inductive reactance per kilometer at 50/60Hz")
            EngineeringInputField(label = "Load Power Factor", value = powerFactor, onValueChange = { powerFactor = it }, unit = "lag", tooltip = "Operating power factor")
        }

        CalculatorResultsCard(result = result)
    }
}

// --- 4. Cable Derating Calculator ---
@Composable
fun CableDeratingCalculatorView() {
    var baseAmpacity by remember { mutableStateOf("380") }
    var ambientTemp by remember { mutableStateOf("45") }
    var groupCount by remember { mutableIntStateOf(3) }
    var isDirectBuried by remember { mutableStateOf(false) }
    var result by remember {
        mutableStateOf(
            ElectricalCalculators.calculateCableDerating(380.0, 45.0, 3, false)
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CalculatorInputCard(
            title = "Environmental & Installation Conditions (IEC 60364-5-52)",
            onCalculate = {
                val b = baseAmpacity.toDoubleOrNull() ?: 380.0
                val t = ambientTemp.toDoubleOrNull() ?: 45.0
                result = ElectricalCalculators.calculateCableDerating(b, t, groupCount, isDirectBuried)
            }
        ) {
            EngineeringInputField(label = "Base Cable Ampacity (Air/Std)", value = baseAmpacity, onValueChange = { baseAmpacity = it }, unit = "A", tooltip = "From manufacturer catalog at reference 30°C")
            EngineeringInputField(label = "Ambient / Soil Temp", value = ambientTemp, onValueChange = { ambientTemp = it }, unit = "°C", tooltip = "Maximum peak summer ambient or soil temperature")
            EngineeringInputField(label = "Adjacent Circuits in Group", value = groupCount.toString(), onValueChange = { groupCount = it.toIntOrNull() ?: 1 }, unit = "circuits", tooltip = "Number of multi-core cables touching or in same tray")
        }

        CalculatorResultsCard(result = result)
    }
}

// --- 5. Relay Tripping Time Calculator ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelayTripTimeCalculatorView() {
    var selectedCurve by remember { mutableStateOf(ElectricalCalculators.IecCurve.NORMAL_INVERSE) }
    var pickupCurrent by remember { mutableStateOf("100") }
    var faultCurrent by remember { mutableStateOf("600") }
    var tms by remember { mutableStateOf("0.15") }
    var result by remember {
        mutableStateOf(
            ElectricalCalculators.calculateRelayTripTime(ElectricalCalculators.IecCurve.NORMAL_INVERSE, 100.0, 600.0, 0.15)
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CalculatorInputCard(
            title = "Overcurrent Protection Relay (IEC 60255-151)",
            onCalculate = {
                val ip = pickupCurrent.toDoubleOrNull() ?: 100.0
                val ift = faultCurrent.toDoubleOrNull() ?: 600.0
                val tm = tms.toDoubleOrNull() ?: 0.15
                result = ElectricalCalculators.calculateRelayTripTime(selectedCurve, ip, ift, tm)
            }
        ) {
            EngineeringInputField(label = "Relay Pickup Setting (Is)", value = pickupCurrent, onValueChange = { pickupCurrent = it }, unit = "A", tooltip = "Threshold secondary or primary pickup current")
            EngineeringInputField(label = "Prospective Fault Current (If)", value = faultCurrent, onValueChange = { faultCurrent = it }, unit = "A", tooltip = "Expected or measured short circuit current")
            EngineeringInputField(label = "Time Multiplier Setting (TMS)", value = tms, onValueChange = { tms = it }, unit = "", tooltip = "Time dial multiplier (typically 0.05 - 1.0)")
        }

        CalculatorResultsCard(result = result)
    }
}

// --- 6. Motor Starting Calculator ---
@Composable
fun MotorStartingCalculatorView() {
    var motorKw by remember { mutableStateOf("450") }
    var voltage by remember { mutableStateOf("3300") }
    var ratedCurrent by remember { mutableStateOf("96.5") }
    var method by remember { mutableStateOf(ElectricalCalculators.StartingMethod.DOL) }
    var result by remember {
        mutableStateOf(
            ElectricalCalculators.calculateMotorStarting(450.0, 3300.0, 96.5, ElectricalCalculators.StartingMethod.DOL)
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CalculatorInputCard(
            title = "Medium/Low Voltage Motor Starting (IEC 60034)",
            onCalculate = {
                val kw = motorKw.toDoubleOrNull() ?: 450.0
                val v = voltage.toDoubleOrNull() ?: 3300.0
                val in_ = ratedCurrent.toDoubleOrNull() ?: 96.5
                result = ElectricalCalculators.calculateMotorStarting(kw, v, in_, method)
            }
        ) {
            EngineeringInputField(label = "Rated Motor Power", value = motorKw, onValueChange = { motorKw = it }, unit = "kW", tooltip = "Shaft output mechanical rating")
            EngineeringInputField(label = "Motor Rated Voltage", value = voltage, onValueChange = { voltage = it }, unit = "V", tooltip = "Nameplate terminal voltage")
            EngineeringInputField(label = "Rated Full Load Current (In)", value = ratedCurrent, onValueChange = { ratedCurrent = it }, unit = "A", tooltip = "Nominal continuous FLA")
        }

        CalculatorResultsCard(result = result)
    }
}

// --- Reusable Input Panel Card ---
@Composable
fun CalculatorInputCard(
    title: String,
    onCalculate: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "INPUT PANEL: $title",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            content()

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onCalculate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricalBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Calculate Engineering Parameters",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- Reusable Results Panel Card ---
@Composable
fun CalculatorResultsCard(
    result: CalculationResult
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B192C)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                listOf(Color(0xFF0066CC), Color(0xFF1E3A5F))
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "RESULTS PANEL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8),
                letterSpacing = 1.sp
            )

            // Primary Answer in Large Readable Industrial Typography
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = result.primaryLabel,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = result.primaryValue,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = result.primaryUnit,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            // Secondary Parameter Metrics Grid
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF11223A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    result.secondaryResults.forEach { (label, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = label, fontSize = 12.sp, color = Color(0xFFCBD5E1))
                            Text(text = value, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Formula Display
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF07111E),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF152A42)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Formula:", fontSize = 11.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                    Text(
                        text = result.formulaDisplay,
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Calculation Steps
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Calculation Steps:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBAE6FD)
                )
                result.calculationSteps.forEach { step ->
                    Text(
                        text = "• $step",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 16.sp
                    )
                }
            }

            // Warnings if any
            if (result.warnings.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF451A03),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD97706)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Column {
                            result.warnings.forEach { warning ->
                                Text(
                                    text = warning,
                                    color = Color(0xFFFEF3C7),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Engineering Notes
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Engineering & Standards Notes:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                result.engineeringNotes.forEach { note ->
                    Text(
                        text = "ℹ $note",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EngineeringInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    tooltip: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (unit.isNotBlank()) {
                Text(
                    text = "[$unit]",
                    fontSize = 11.sp,
                    color = ElectricalBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = ElectricalBlue
            ),
            shape = RoundedCornerShape(6.dp)
        )

        Text(
            text = tooltip,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
