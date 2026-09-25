package com.example.ui.components

import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.EquipmentCategory
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.ElectricalBlueCyan
import com.example.ui.theme.NavyCardBorderDark
import com.example.ui.theme.NavyCardDark

@Composable
fun EngineeringAssetImage(
    category: EquipmentCategory,
    photoUri: String?,
    searchPhrase: String,
    modifier: Modifier = Modifier,
    heightDp: Int = 180,
    onUploadClick: (() -> Unit)? = null
) {
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0C192C)
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF1E3A5F), Color(0xFF0F2642))))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = category.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // High-precision Industrial Blueprint Schematic Canvas
                EngineeringBlueprintCanvas(category = category)

                // Recommended Search Phrase overlay & Upload trigger
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.2f),
                                    Color(0xFF06101E).copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF0066CC).copy(alpha = 0.25f),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF0052A3)))
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Google Image Query: $searchPhrase",
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF1E293B),
                                modifier = Modifier.clickable {
                                    clipboardManager.setText(AnnotatedString(searchPhrase))
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy Search Term",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Copy Query",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            if (onUploadClick != null) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF0284C7).copy(alpha = 0.3f),
                                    modifier = Modifier.clickable(onClick = onUploadClick)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AddPhotoAlternate,
                                            contentDescription = "Upload Site Photo",
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "Upload Site Asset Photo",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EngineeringBlueprintCanvas(category: EquipmentCategory) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Technical Grid lines
        val gridStep = 32f
        var x = 0f
        while (x < w) {
            drawLine(
                color = Color(0xFF132A4A),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
            x += gridStep
        }
        var y = 0f
        while (y < h) {
            drawLine(
                color = Color(0xFF132A4A),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
            y += gridStep
        }

        // Draw equipment schematic outline based on category
        when (category) {
            EquipmentCategory.TRANSFORMER -> {
                // Dual interlaced inductive coils & HV bushings
                val centerY = h * 0.45f
                val centerX = w * 0.5f
                val radius = 45f

                // Transformer Tank
                drawRect(
                    color = Color(0xFF163C6B),
                    topLeft = Offset(centerX - 120f, centerY - 45f),
                    size = androidx.compose.ui.geometry.Size(240f, 90f),
                    style = Stroke(width = 2.5f)
                )

                // Coils
                drawCircle(
                    color = Color(0xFF0284C7),
                    radius = radius,
                    center = Offset(centerX - 35f, centerY),
                    style = Stroke(width = 3f)
                )
                drawCircle(
                    color = Color(0xFF38BDF8),
                    radius = radius,
                    center = Offset(centerX + 35f, centerY),
                    style = Stroke(width = 3f)
                )

                // Bushings
                for (b in -2..2) {
                    val bx = centerX + b * 45f
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = Offset(bx, centerY - 45f),
                        end = Offset(bx, centerY - 80f),
                        strokeWidth = 3f
                    )
                    drawCircle(
                        color = Color(0xFFF59E0B),
                        radius = 6f,
                        center = Offset(bx, centerY - 80f)
                    )
                }
            }

            EquipmentCategory.CIRCUIT_BREAKER -> {
                // High Voltage Circuit Breaker Switchgear Contacts & Mechanism
                val cx = w * 0.5f
                val cy = h * 0.45f
                drawRect(
                    color = Color(0xFF1E3A5F),
                    topLeft = Offset(cx - 100f, cy - 50f),
                    size = androidx.compose.ui.geometry.Size(200f, 100f),
                    style = Stroke(width = 2.5f)
                )
                // Bus terminal in & out
                drawLine(
                    color = Color(0xFF38BDF8),
                    start = Offset(cx - 140f, cy),
                    end = Offset(cx - 40f, cy),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color(0xFF38BDF8),
                    start = Offset(cx + 40f, cy),
                    end = Offset(cx + 140f, cy),
                    strokeWidth = 4f
                )
                // Vacuum bottle contacts (Open switch symbol)
                drawCircle(color = Color.White, radius = 8f, center = Offset(cx - 40f, cy))
                drawCircle(color = Color.White, radius = 8f, center = Offset(cx + 40f, cy))
                drawLine(
                    color = Color(0xFFEF4444),
                    start = Offset(cx - 40f, cy),
                    end = Offset(cx + 25f, cy - 35f),
                    strokeWidth = 4f
                )
            }

            EquipmentCategory.PROTECTION_RELAY -> {
                // Numerical Relay IED Panel & ANSI function boxes
                val cx = w * 0.5f
                val cy = h * 0.45f
                drawRect(
                    color = Color(0xFF152E52),
                    topLeft = Offset(cx - 110f, cy - 55f),
                    size = androidx.compose.ui.geometry.Size(220f, 110f),
                    style = Stroke(width = 3f)
                )
                // LCD Mimic screen
                drawRect(
                    color = Color(0xFF003875),
                    topLeft = Offset(cx - 95f, cy - 40f),
                    size = androidx.compose.ui.geometry.Size(90f, 65f)
                )
                // Protection ANSI Shield
                val shieldPath = Path().apply {
                    moveTo(cx + 45f, cy - 35f)
                    lineTo(cx + 80f, cy - 25f)
                    lineTo(cx + 80f, cy + 15f)
                    lineTo(cx + 45f, cy + 35f)
                    lineTo(cx + 10f, cy + 15f)
                    lineTo(cx + 10f, cy - 25f)
                    close()
                }
                drawPath(shieldPath, color = Color(0xFF0284C7), style = Stroke(width = 2.5f))
            }

            EquipmentCategory.CABLE -> {
                // Multi-core Trefoil Cable cross section with concentric insulation
                val cx = w * 0.5f
                val cy = h * 0.45f
                drawCircle(
                    color = Color(0xFF1E3A5F),
                    radius = 60f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 3f)
                )
                // 3 Cores in Trefoil
                val rCore = 22f
                drawCircle(color = Color(0xFFDC2626), radius = rCore, center = Offset(cx, cy - 24f))
                drawCircle(color = Color(0xFFF59E0B), radius = rCore, center = Offset(cx - 24f, cy + 18f))
                drawCircle(color = Color(0xFF2563EB), radius = rCore, center = Offset(cx + 24f, cy + 18f))
            }

            EquipmentCategory.MOTOR -> {
                // Motor Stator & Rotor circle with 'M 3~' industrial symbol
                val cx = w * 0.5f
                val cy = h * 0.45f
                drawCircle(
                    color = Color(0xFF0284C7),
                    radius = 50f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 3.5f)
                )
                // Terminal leads
                drawLine(
                    color = Color(0xFF38BDF8),
                    start = Offset(cx, cy - 50f),
                    end = Offset(cx, cy - 80f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color(0xFF38BDF8),
                    start = Offset(cx - 25f, cy - 45f),
                    end = Offset(cx - 25f, cy - 80f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color(0xFF38BDF8),
                    start = Offset(cx + 25f, cy - 45f),
                    end = Offset(cx + 25f, cy - 80f),
                    strokeWidth = 3f
                )
            }

            EquipmentCategory.SOLAR_PV -> {
                // Solar PV Array grid & Inverter wave
                val cx = w * 0.5f
                val cy = h * 0.45f
                drawRect(
                    color = Color(0xFF0284C7),
                    topLeft = Offset(cx - 90f, cy - 45f),
                    size = androidx.compose.ui.geometry.Size(180f, 90f),
                    style = Stroke(width = 2.5f)
                )
                // Internal cell lines
                drawLine(color = Color(0xFF1E3A5F), start = Offset(cx - 30f, cy - 45f), end = Offset(cx - 30f, cy + 45f), strokeWidth = 2f)
                drawLine(color = Color(0xFF1E3A5F), start = Offset(cx + 30f, cy - 45f), end = Offset(cx + 30f, cy + 45f), strokeWidth = 2f)
                drawLine(color = Color(0xFF1E3A5F), start = Offset(cx - 90f, cy), end = Offset(cx + 90f, cy), strokeWidth = 2f)
            }
        }
    }
}
