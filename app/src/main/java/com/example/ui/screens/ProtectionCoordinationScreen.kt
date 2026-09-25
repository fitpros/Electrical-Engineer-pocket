package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.CoordinationAnalysisResult
import com.example.calculator.CoordinationStatus
import com.example.calculator.ElectricalCalculators
import com.example.calculator.ProtectionCoordinationEngine
import com.example.calculator.RelayDeviceSetting
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.ElectricalBlueCyan
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusPassGreen
import com.example.ui.theme.StatusWarningAmber
import kotlin.math.log10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectionCoordinationScreen(
    modifier: Modifier = Modifier
) {
    // Upstream Incomer Relay Settings (e.g. 132/33kV Incomer)
    var upstreamCurve by remember { mutableStateOf(ElectricalCalculators.IecCurve.NORMAL_INVERSE) }
    var upstreamPickup by remember { mutableStateOf("600") }
    var upstreamTms by remember { mutableStateOf("0.28") }
    var upstreamInst by remember { mutableStateOf("6500") }

    // Downstream Feeder Relay Settings (e.g. 33kV Outgoing Feeder)
    var downstreamCurve by remember { mutableStateOf(ElectricalCalculators.IecCurve.NORMAL_INVERSE) }
    var downstreamPickup by remember { mutableStateOf("250") }
    var downstreamTms by remember { mutableStateOf("0.12") }
    var downstreamInst by remember { mutableStateOf("3500") }

    // System Fault Levels
    var minFaultCurrent by remember { mutableStateOf("1200") }
    var maxFaultCurrent by remember { mutableStateOf("4500") }

    // Calculation result
    val upSetting = RelayDeviceSetting(
        id = "RY-UP",
        name = "Upstream Incomer Relay (132/33kV)",
        tag = "132-RY-INCOMER",
        curve = upstreamCurve,
        pickupCurrentAmps = upstreamPickup.toDoubleOrNull() ?: 600.0,
        timeMultiplier = upstreamTms.toDoubleOrNull() ?: 0.28,
        instantaneousPickupAmps = upstreamInst.toDoubleOrNull(),
        colorHex = 0xFF0066CC
    )

    val downSetting = RelayDeviceSetting(
        id = "RY-DOWN",
        name = "Downstream Feeder Relay (Bay 4)",
        tag = "33-RY-FEEDER-04",
        curve = downstreamCurve,
        pickupCurrentAmps = downstreamPickup.toDoubleOrNull() ?: 250.0,
        timeMultiplier = downstreamTms.toDoubleOrNull() ?: 0.12,
        instantaneousPickupAmps = downstreamInst.toDoubleOrNull(),
        colorHex = 0xFF0EA5E9
    )

    val iMin = minFaultCurrent.toDoubleOrNull() ?: 1200.0
    val iMax = maxFaultCurrent.toDoubleOrNull() ?: 4500.0

    val analysis = remember(upSetting, downSetting, iMin, iMax) {
        ProtectionCoordinationEngine.analyzeCoordination(
            upstreamRelay = upSetting,
            downstreamRelay = downSetting,
            minFaultCurrentAmps = iMin,
            maxFaultCurrentAmps = iMax
        )
    }

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
                        text = "Protective Relay Coordination (TCC)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Time-Current Curves, Discrimination & Grading Margin Verification (IEEE 242 / IEC 60255)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (analysis.status != CoordinationStatus.OPTIMAL) {
                    Button(
                        onClick = {
                            upstreamTms = "%.2f".format(analysis.suggestedUpstreamTms)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Auto-Optimize TMS", fontSize = 12.sp)
                    }
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
            // TCC Coordination Visual Curve Chart
            TccCoordinationChart(
                upstreamPoints = analysis.upstreamCurvePoints,
                downstreamPoints = analysis.curvePoints,
                minFaultAmps = iMin,
                maxFaultAmps = iMax,
                analysis = analysis
            )

            // Discrimination & Grading Evaluation Card
            CoordinationEvaluationCard(
                analysis = analysis,
                onApplySuggestedTms = {
                    upstreamTms = "%.2f".format(analysis.suggestedUpstreamTms)
                }
            )

            // Settings Inputs: Upstream & Downstream Relays
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Downstream Relay Panel (Left)
                RelayConfigCard(
                    title = "DOWNSTREAM FEEDER RELAY",
                    subtitle = "33-RY-FEEDER-04 (Bay 4)",
                    color = Color(0xFF0EA5E9),
                    curve = downstreamCurve,
                    onCurveChange = { downstreamCurve = it },
                    pickup = downstreamPickup,
                    onPickupChange = { downstreamPickup = it },
                    tms = downstreamTms,
                    onTmsChange = { downstreamTms = it },
                    instantaneous = downstreamInst,
                    onInstantaneousChange = { downstreamInst = it },
                    modifier = Modifier.weight(1f)
                )

                // Upstream Incomer Relay Panel (Right)
                RelayConfigCard(
                    title = "UPSTREAM INCOMER RELAY",
                    subtitle = "132-RY-INCOMER (Bay 2)",
                    color = Color(0xFF0066CC),
                    curve = upstreamCurve,
                    onCurveChange = { upstreamCurve = it },
                    pickup = upstreamPickup,
                    onPickupChange = { upstreamPickup = it },
                    tms = upstreamTms,
                    onTmsChange = { upstreamTms = it },
                    instantaneous = upstreamInst,
                    onInstantaneousChange = { upstreamInst = it },
                    modifier = Modifier.weight(1f)
                )
            }

            // System Fault Level Inputs
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SYSTEM FAULT CURRENT INTERVALS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = ElectricalBlue,
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = minFaultCurrent,
                            onValueChange = { minFaultCurrent = it },
                            label = { Text("Minimum Fault (If,min)") },
                            suffix = { Text("A") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = maxFaultCurrent,
                            onValueChange = { maxFaultCurrent = it },
                            label = { Text("Maximum Fault (If,max)") },
                            suffix = { Text("A") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// Visual Time-Current Characteristic Canvas Plot
@Composable
fun TccCoordinationChart(
    upstreamPoints: List<Pair<Double, Double>>,
    downstreamPoints: List<Pair<Double, Double>>,
    minFaultAmps: Double,
    maxFaultAmps: Double,
    analysis: CoordinationAnalysisResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF091626)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                listOf(Color(0xFF1E3A5F), Color(0xFF0066CC))
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Chart Title & Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TIME-CURRENT COORDINATION (TCC) LOG CURVES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Log-Log Representation (100 A to 10,000 A vs 0.01s to 10s)",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(10.dp, 3.dp).background(Color(0xFF0EA5E9)))
                        Text(text = "Downstream", fontSize = 10.sp, color = Color(0xFF0EA5E9), fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(10.dp, 3.dp).background(Color(0xFF38BDF8)))
                        Text(text = "Upstream", fontSize = 10.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Canvas Graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val pLeft = 45f
                    val pBottom = 25f
                    val chartW = w - pLeft - 10f
                    val chartH = h - pBottom - 10f

                    // Log scale ranges:
                    // Current: 100A to 10000A -> log range = 2.0 to 4.0 (span = 2.0)
                    val logImin = 2.0
                    val logImax = 4.0
                    val logIspan = logImax - logImin

                    // Time: 0.01s to 10s -> log range = -2.0 to 1.0 (span = 3.0)
                    val logTmin = -2.0
                    val logTmax = 1.0
                    val logTspan = logTmax - logTmin

                    fun mapX(iAmps: Double): Float {
                        val logVal = log10(iAmps.coerceIn(100.0, 10000.0))
                        return (pLeft + ((logVal - logImin) / logIspan) * chartW).toFloat()
                    }

                    fun mapY(tSec: Double): Float {
                        val logVal = log10(tSec.coerceIn(0.01, 10.0))
                        return (chartH * (1.0 - (logVal - logTmin) / logTspan) + 5f).toFloat()
                    }

                    // Draw Log Grid Lines
                    // Current decades: 100A, 200A, 500A, 1000A, 2000A, 5000A, 10000A
                    listOf(100.0, 200.0, 500.0, 1000.0, 2000.0, 5000.0, 10000.0).forEach { iVal ->
                        val x = mapX(iVal)
                        val isDecade = iVal == 100.0 || iVal == 1000.0 || iVal == 10000.0
                        drawLine(
                            color = if (isDecade) Color(0xFF1E3A5F) else Color(0xFF11223A),
                            start = Offset(x, 0f),
                            end = Offset(x, chartH + 5f),
                            strokeWidth = if (isDecade) 1.2f else 0.8f
                        )
                    }

                    // Time decades: 0.01s, 0.1s, 1.0s, 10.0s
                    listOf(0.01, 0.02, 0.05, 0.1, 0.2, 0.5, 1.0, 2.0, 5.0, 10.0).forEach { tVal ->
                        val y = mapY(tVal)
                        val isDecade = tVal == 0.01 || tVal == 0.1 || tVal == 1.0 || tVal == 10.0
                        drawLine(
                            color = if (isDecade) Color(0xFF1E3A5F) else Color(0xFF11223A),
                            start = Offset(pLeft, y),
                            end = Offset(w, y),
                            strokeWidth = if (isDecade) 1.2f else 0.8f
                        )
                    }

                    // Draw Fault Current Vertical Bounds (iMin, iMax)
                    val xMinFault = mapX(minFaultAmps)
                    val xMaxFault = mapX(maxFaultAmps)
                    drawLine(
                        color = Color(0xFFF59E0B).copy(alpha = 0.6f),
                        start = Offset(xMinFault, 0f),
                        end = Offset(xMinFault, chartH + 5f),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = Color(0xFFEF4444).copy(alpha = 0.6f),
                        start = Offset(xMaxFault, 0f),
                        end = Offset(xMaxFault, chartH + 5f),
                        strokeWidth = 1.5f
                    )

                    // Draw Downstream Curve (Cyan)
                    if (downstreamPoints.isNotEmpty()) {
                        val pathDown = Path()
                        downstreamPoints.forEachIndexed { idx, (i, t) ->
                            val x = mapX(i)
                            val y = mapY(t)
                            if (idx == 0) pathDown.moveTo(x, y) else pathDown.lineTo(x, y)
                        }
                        drawPath(pathDown, color = Color(0xFF0EA5E9), style = Stroke(width = 3f, cap = StrokeCap.Round))
                    }

                    // Draw Upstream Curve (Electrical Blue / White)
                    if (upstreamPoints.isNotEmpty()) {
                        val pathUp = Path()
                        upstreamPoints.forEachIndexed { idx, (i, t) ->
                            val x = mapX(i)
                            val y = mapY(t)
                            if (idx == 0) pathUp.moveTo(x, y) else pathUp.lineTo(x, y)
                        }
                        drawPath(pathUp, color = Color(0xFF38BDF8), style = Stroke(width = 3f, cap = StrokeCap.Round))
                    }
                }
            }

            // X-Axis Decade Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 38.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "100A", fontSize = 9.sp, color = Color(0xFF94A3B8))
                Text(text = "500A", fontSize = 9.sp, color = Color(0xFF94A3B8))
                Text(text = "1 kA", fontSize = 9.sp, color = Color(0xFF94A3B8))
                Text(text = "2 kA", fontSize = 9.sp, color = Color(0xFF94A3B8))
                Text(text = "5 kA", fontSize = 9.sp, color = Color(0xFF94A3B8))
                Text(text = "10 kA", fontSize = 9.sp, color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
fun CoordinationEvaluationCard(
    analysis: CoordinationAnalysisResult,
    onApplySuggestedTms: () -> Unit
) {
    val (statusColor, statusBg, icon) = when (analysis.status) {
        CoordinationStatus.OPTIMAL -> Triple(StatusPassGreen, Color(0xFF052E16), Icons.Default.CheckCircle)
        CoordinationStatus.INSUFFICIENT_MARGIN -> Triple(StatusWarningAmber, Color(0xFF451A03), Icons.Default.Warning)
        CoordinationStatus.OVERLAP_TRIP -> Triple(StatusCriticalRed, Color(0xFF450A0A), Icons.Default.Error)
        CoordinationStatus.EXCESSIVE_DELAY -> Triple(Color(0xFF38BDF8), Color(0xFF082F49), Icons.Default.Info)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = statusBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                listOf(statusColor, statusColor.copy(alpha = 0.5f))
            )
        )
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(22.dp))
                    Text(
                        text = analysis.status.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
                ) {
                    Text(
                        text = "Margin: %.3fs".format(analysis.marginAtMaxFault),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Trip times at Max Fault
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Downstream Feeder Trip", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                        Text(text = "%.3f s".format(analysis.downstreamTripTimeAtMaxFault), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0EA5E9))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Upstream Incomer Trip", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                        Text(text = "%.3f s".format(analysis.upstreamTripTimeAtMaxFault), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Grading Delta (Δt)", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                        Text(text = "%.3f s".format(analysis.marginAtMaxFault), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = statusColor)
                    }
                }
            }

            // Diagnostic Notes & Explanations
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                analysis.analysisNotes.forEach { note ->
                    Text(
                        text = "• $note",
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 16.sp
                    )
                }
            }

            // One-click Auto Adjust Suggestion Button
            if (analysis.status != CoordinationStatus.OPTIMAL) {
                Button(
                    onClick = onApplySuggestedTms,
                    colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Apply Recommended Upstream TMS = %.2f (Restores 0.30s Margin)".format(analysis.suggestedUpstreamTms),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RelayConfigCard(
    title: String,
    subtitle: String,
    color: Color,
    curve: ElectricalCalculators.IecCurve,
    onCurveChange: (ElectricalCalculators.IecCurve) -> Unit,
    pickup: String,
    onPickupChange: (String) -> Unit,
    tms: String,
    onTmsChange: (String) -> Unit,
    instantaneous: String,
    onInstantaneousChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
                Column {
                    Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
                    Text(text = subtitle, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }

            // Curve Dropdown selector
            CurveSelector(selectedCurve = curve, onSelect = onCurveChange)

            OutlinedTextField(
                value = pickup,
                onValueChange = onPickupChange,
                label = { Text("Pickup Is", fontSize = 10.sp) },
                suffix = { Text("A", fontSize = 10.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )

            OutlinedTextField(
                value = tms,
                onValueChange = onTmsChange,
                label = { Text("Time Multiplier (TMS)", fontSize = 10.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )

            OutlinedTextField(
                value = instantaneous,
                onValueChange = onInstantaneousChange,
                label = { Text("High-Set Instantaneous I>>", fontSize = 10.sp) },
                suffix = { Text("A", fontSize = 10.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurveSelector(
    selectedCurve: ElectricalCalculators.IecCurve,
    onSelect: (ElectricalCalculators.IecCurve) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedCurve.label.replace("IEC ", ""),
            onValueChange = {},
            readOnly = true,
            label = { Text("IEC 60255 Curve", fontSize = 10.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth().height(48.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ElectricalCalculators.IecCurve.values().forEach { c ->
                DropdownMenuItem(
                    text = { Text(c.label, fontSize = 11.sp) },
                    onClick = {
                        onSelect(c)
                        expanded = false
                    }
                )
            }
        }
    }
}
