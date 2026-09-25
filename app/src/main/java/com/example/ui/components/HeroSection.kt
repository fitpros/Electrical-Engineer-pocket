package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.ElectricalBlueCyan
import com.example.ui.theme.ElectricalBlueLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EngineeringHeroSection(
    onOpenCalculators: () -> Unit,
    onStartInspection: () -> Unit,
    onAddEquipment: () -> Unit,
    onCreateReport: () -> Unit,
    onOpenScada: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF071426)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(Color(0xFF1E3A5F), Color(0xFF0052A3), Color(0xFF132A4A))
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // High Voltage Substation / Gantry Switchyard Stylized Canvas
            SubstationSwitchyardHeroCanvas(
                modifier = Modifier
                    .matchParentSize()
            )

            // Dark gradient overlay to preserve contrast and readability
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF06101E).copy(alpha = 0.94f),
                                Color(0xFF0A192F).copy(alpha = 0.88f),
                                Color(0xFF081528).copy(alpha = 0.94f)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Engineering Substation Tag & Live Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ElectricalBlue.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
                        ),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "132/33 kV SUBSTATION",
                                color = Color(0xFFE2E8F0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF16A34A).copy(alpha = 0.22f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.linearGradient(listOf(Color(0xFF22C55E), Color(0xFF15803D)))
                        ),
                        modifier = Modifier.clickable(onClick = onOpenScada)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                            )
                            Icon(
                                Icons.Default.Sensors,
                                contentDescription = null,
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "SYSTEM ONLINE",
                                color = Color(0xFF4ADE80),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                // Main Heading
                Text(
                    text = "Electrical Engineer Pro",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp
                )

                // Subtitle
                Text(
                    text = "Engineering Calculations, Maintenance, Protection and Asset Management in One Platform",
                    color = Color(0xFFBAE6FD),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )

                Text(
                    text = "Quickly calculate, inspect, track, analyze, and generate professional compliance reports.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Action Buttons
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenCalculators,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricalBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Open Calculators", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onStartInspection,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E3A5F),
                            contentColor = Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            Icons.Default.FactCheck,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Start Inspection", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = onAddEquipment,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Add Equipment", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    OutlinedButton(
                        onClick = onCreateReport,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFBAE6FD)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            Icons.Default.Assessment,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Create Report", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun SubstationSwitchyardHeroCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // High Voltage Gantry towers silhouette
        val towerWidth = 36f
        for (i in 0..4) {
            val tx = (w * 0.18f * i) + 40f
            // Tower legs
            drawLine(
                color = Color(0xFF162D4A),
                start = Offset(tx - towerWidth / 2, h),
                end = Offset(tx - 6f, 20f),
                strokeWidth = 2f
            )
            drawLine(
                color = Color(0xFF162D4A),
                start = Offset(tx + towerWidth / 2, h),
                end = Offset(tx + 6f, 20f),
                strokeWidth = 2f
            )
            // Cross bracing
            drawLine(
                color = Color(0xFF13243D),
                start = Offset(tx - towerWidth / 2, h * 0.7f),
                end = Offset(tx + towerWidth / 2, h * 0.3f),
                strokeWidth = 1.5f
            )
            drawLine(
                color = Color(0xFF13243D),
                start = Offset(tx - towerWidth / 2, h * 0.3f),
                end = Offset(tx + towerWidth / 2, h * 0.7f),
                strokeWidth = 1.5f
            )
        }

        // Horizontal bus conductors strung across gantries
        val y1 = h * 0.25f
        val y2 = h * 0.40f
        val y3 = h * 0.55f
        drawLine(
            color = Color(0xFF0F3159),
            start = Offset(0f, y1),
            end = Offset(w, y1 + 10f),
            strokeWidth = 2f
        )
        drawLine(
            color = Color(0xFF0F3159),
            start = Offset(0f, y2),
            end = Offset(w, y2 + 10f),
            strokeWidth = 2f
        )
        drawLine(
            color = Color(0xFF0F3159),
            start = Offset(0f, y3),
            end = Offset(w, y3 + 10f),
            strokeWidth = 2f
        )
    }
}
