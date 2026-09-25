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
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScadaProtocolType
import com.example.data.model.ScadaTelemetryEntity
import com.example.data.scada.ScadaTelemetryManager
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusPassGreen
import com.example.ui.theme.StatusWarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScadaScreen(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val telemetryMap by ScadaTelemetryManager.liveTelemetry.collectAsState()
    val isLiveActive by ScadaTelemetryManager.isLiveFeedActive.collectAsState()
    val activeProtocol by ScadaTelemetryManager.activeProtocol.collectAsState()

    var showSimulatePacketDialog by remember { mutableStateOf(false) }

    val trTelemetry = telemetryMap["132-TR-01"]
    val cbTelemetry = telemetryMap["33-CB-102"]
    val ryTelemetry = telemetryMap["615-RY-04"]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // SCADA Header & Telecontrol Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(if (isLiveActive) StatusPassGreen else Color.Gray, CircleShape)
                            )
                            Text(
                                text = "SCADA Telemetry & Telecontrol Gateway",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Industrial Protocol Ingestion Engine (Modbus TCP, IEC 60870-5-104, DNP3)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { ScadaTelemetryManager.toggleLiveStream(coroutineScope) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLiveActive) Color(0xFF1E293B) else ElectricalBlue
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(
                                if (isLiveActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isLiveActive) "Pause Feed" else "Resume Feed", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { showSimulatePacketDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Ingest Packet", fontSize = 11.sp)
                        }
                    }
                }

                // Active Protocol and Gateway Status Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F1E33), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE PROTOCOL: ${activeProtocol.protocolName} (Port ${activeProtocol.defaultPort})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "POLL INTERVAL: 2500ms • LATENCY: 14ms • HEALTH: 100%",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
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
            // Live Substation Mimic / Single-Line Diagram (SLD) View
            SubstationSldMimic(
                tr = trTelemetry,
                cb = cbTelemetry
            )

            // Equipment 1: Power Transformer Live Operational Parameters
            trTelemetry?.let { tr ->
                TransformerTelemetryCard(telemetry = tr)
            }

            // Equipment 2: 33kV Circuit Breaker Live Operational Parameters
            cbTelemetry?.let { cb ->
                CircuitBreakerTelemetryCard(telemetry = cb)
            }

            // Protocol Specification & API Integration Definition
            ProtocolApiSpecificationCard(
                activeProtocol = activeProtocol,
                onSelectProtocol = { ScadaTelemetryManager.setProtocol(it) }
            )
        }
    }

    if (showSimulatePacketDialog) {
        SimulatePacketDialog(
            onDismiss = { showSimulatePacketDialog = false },
            onInject = { tag, v, ia, ib, ic, temp, status ->
                ScadaTelemetryManager.ingestScadaPacket(tag, v, ia, ib, ic, temp, status)
                showSimulatePacketDialog = false
            }
        )
    }
}

// Live Substation SLD Mimic Canvas
@Composable
fun SubstationSldMimic(
    tr: ScadaTelemetryEntity?,
    cb: ScadaTelemetryEntity?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF07111E)),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SUBSTATION SINGLE-LINE MIMIC (BAY 2 & 4)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF16A34A).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E))
                ) {
                    Text(
                        text = "BUSBAR ENERGIZED",
                        color = Color(0xFF4ADE80),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height

                    // 132 kV Top Busbar (Red line)
                    drawLine(
                        color = Color(0xFFDC2626),
                        start = Offset(20f, 15f),
                        end = Offset(w - 20f, 15f),
                        strokeWidth = 4f
                    )

                    // Transformer tap-off line down
                    val midX = w * 0.35f
                    drawLine(
                        color = Color(0xFFDC2626),
                        start = Offset(midX, 15f),
                        end = Offset(midX, 40f),
                        strokeWidth = 3f
                    )

                    // Transformer symbol (2 circles)
                    drawCircle(color = Color(0xFF0284C7), radius = 14f, center = Offset(midX, 50f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f))
                    drawCircle(color = Color(0xFF38BDF8), radius = 14f, center = Offset(midX, 68f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f))

                    // Line to 33kV Busbar
                    drawLine(
                        color = Color(0xFF0284C7),
                        start = Offset(midX, 82f),
                        end = Offset(midX, 95f),
                        strokeWidth = 3f
                    )

                    // 33 kV Middle Busbar (Cyan line)
                    drawLine(
                        color = Color(0xFF0284C7),
                        start = Offset(20f, 95f),
                        end = Offset(w - 20f, 95f),
                        strokeWidth = 4f
                    )

                    // Breaker tap-off to Feeder
                    val cbX = w * 0.70f
                    drawLine(
                        color = Color(0xFF0284C7),
                        start = Offset(cbX, 95f),
                        end = Offset(cbX, 110f),
                        strokeWidth = 3f
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "132 kV Main Grid Bus", fontSize = 10.sp, color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold)
                Text(text = "33 kV Plant Distribution Bus", fontSize = 10.sp, color = Color(0xFF7DD3FC), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Transformer Live Parameters Card
@Composable
fun TransformerTelemetryCard(telemetry: ScadaTelemetryEntity) {
    val isTempWarning = telemetry.windingTempC >= 95.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = ElectricalBlue)
                    Column {
                        Text(text = "132-TR-01 LIVE TELEMETRY", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = telemetry.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isTempWarning) StatusCriticalRed.copy(alpha = 0.2f) else StatusPassGreen.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isTempWarning) StatusCriticalRed else StatusPassGreen)
                ) {
                    Text(
                        text = if (isTempWarning) "WTI ALARM STAGE 1" else "NORMAL OPERATION",
                        color = if (isTempWarning) StatusCriticalRed else StatusPassGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Temperature Gauges
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Winding Temperature
                TelemetryGaugeBox(
                    label = "Winding Temp (WTI)",
                    value = "%.1f°C".format(telemetry.windingTempC),
                    maxLimit = "Alarm: 95°C",
                    ratio = (telemetry.windingTempC / 120.0).toFloat().coerceIn(0f, 1f),
                    color = if (isTempWarning) StatusCriticalRed else ElectricalBlue,
                    modifier = Modifier.weight(1f)
                )

                // Top Oil Temperature
                TelemetryGaugeBox(
                    label = "Top Oil Temp (OTI)",
                    value = "%.1f°C".format(telemetry.topOilTempC),
                    maxLimit = "Alarm: 85°C",
                    ratio = (telemetry.topOilTempC / 100.0).toFloat().coerceIn(0f, 1f),
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                )
            }

            // Power & 3-Phase Line Currents
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C192C), RoundedCornerShape(6.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TelemetryStat("Active Power", "%.1f MW".format(telemetry.activePowerMw))
                TelemetryStat("Reactive Power", "%.1f MVAR".format(telemetry.reactivePowerMvar))
                TelemetryStat("Phase A Current", "%.1f A".format(telemetry.currentPhaseA))
                TelemetryStat("OLTC Tap", "Step ${telemetry.tapPosition}")
            }
        }
    }
}

// Breaker Live Parameters Card
@Composable
fun CircuitBreakerTelemetryCard(telemetry: ScadaTelemetryEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Power, contentDescription = null, tint = ElectricalBlue)
                    Column {
                        Text(text = "33-CB-102 VCB BREAKER TELEMETRY", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = telemetry.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = StatusPassGreen.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusPassGreen)
                ) {
                    Text(
                        text = "BREAKER ${telemetry.breakerStatus}",
                        color = StatusPassGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TelemetryStatBox("SF6 Gas Pressure", "%.2f bar".format(telemetry.sf6PressureBar), "Nominal (> 5.5 bar)", StatusPassGreen, Modifier.weight(1f))
                TelemetryStatBox("Spring Mechanism", if (telemetry.springCharged) "CHARGED" else "DISCHARGED", "Motor Status: Ready", StatusPassGreen, Modifier.weight(1f))
                TelemetryStatBox("Primary Current (Ia)", "%.1f A".format(telemetry.currentPhaseA), "Rating: 1250 A", ElectricalBlue, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun TelemetryGaugeBox(
    label: String,
    value: String,
    maxLimit: String,
    ratio: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF091626),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = label, fontSize = 10.sp, color = Color(0xFF94A3B8))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            LinearProgressIndicator(
                progress = { ratio },
                color = color,
                trackColor = Color(0xFF132338),
                modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(2.dp))
            )
            Text(text = maxLimit, fontSize = 9.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun TelemetryStat(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 9.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun TelemetryStatBox(label: String, value: String, sub: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF091626),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = label, fontSize = 9.sp, color = Color(0xFF94A3B8))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = sub, fontSize = 8.sp, color = Color(0xFF64748B))
        }
    }
}

// Protocol Definition & Ingestion API Documentation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolApiSpecificationCard(
    activeProtocol: ScadaProtocolType,
    onSelectProtocol: (ScadaProtocolType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "SCADA PROTOCOL SPECIFICATION & INGESTION SCHEMA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricalBlue,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScadaProtocolType.values().forEach { proto ->
                    val isSel = activeProtocol == proto
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSel) ElectricalBlue else Color(0xFF0B192C),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ElectricalBlue else Color(0xFF1E3A5F)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectProtocol(proto) }
                    ) {
                        Text(
                            text = proto.protocolName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else Color(0xFFCBD5E1),
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF081424),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF132A4A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Gateway Ingestion Format:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                    Text(
                        text = when (activeProtocol) {
                            ScadaProtocolType.MODBUS_TCP -> "Holding Registers: 40001 (kV), 40002 (Ia), 40003 (Ib), 40004 (Ic), 40005 (WTI °C), 40006 (OTI °C), 40007 (Breaker Status Bitmap)"
                            ScadaProtocolType.IEC_60870_5_104 -> "ASDU Type 36 (Measured value, float with time tag CP56Time2a), Information Object Addresses (IOA): 101-108"
                            ScadaProtocolType.DNP3 -> "Object Group 30 (Analog Inputs), Variation 2 (16-bit with flag), Class 1/2 Poll Event Logging"
                            ScadaProtocolType.REST_JSON_GATEWAY -> "POST /api/v1/scada/ingest - Payload: {\"tag\":\"132-TR-01\",\"currents\":[582.4,579.8,585.1],\"oilTempC\":64.8,\"windingTempC\":82.4}"
                        },
                        fontSize = 10.sp,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

// Ingestion simulation dialog
@Composable
fun SimulatePacketDialog(
    onDismiss: () -> Unit,
    onInject: (tag: String, v: Double, ia: Double, ib: Double, ic: Double, temp: Double, status: String) -> Unit
) {
    var tag by remember { mutableStateOf("132-TR-01") }
    var currentA by remember { mutableStateOf("650") }
    var tempC by remember { mutableStateOf("96.5") } // Above 95 to test alert trigger
    var breakerStatus by remember { mutableStateOf("CLOSED") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ingest SCADA Telemetry Packet") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Equipment Tag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = currentA,
                    onValueChange = { currentA = it },
                    label = { Text("Current Phase A (Amps)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tempC,
                    onValueChange = { tempC = it },
                    label = { Text("Winding Temp Indicator (°C)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ia = currentA.toDoubleOrNull() ?: 600.0
                    val t = tempC.toDoubleOrNull() ?: 80.0
                    onInject(tag, 33.2, ia, ia * 0.99, ia * 1.01, t, breakerStatus)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue)
            ) {
                Text("Transmit Packet")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
