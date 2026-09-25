package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SubstationSwitchyardHeroCanvas
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.ElectricalBlueLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PublicLandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToContact: () -> Unit,
    onNavigateToFeatures: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Public Top Navigation Bar
        Surface(
            color = Color(0xFF071426),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF0A192F), RoundedCornerShape(6.dp))
                            .border(1.5.dp, ElectricalBlue, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Electrical Engineer Pro",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onNavigateToLogin,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text(text = "Sign In", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onNavigateToSignUp,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(text = "Get Started", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Hero Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF071426)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF1E3A5F), Color(0xFF0052A3), Color(0xFF132A4A))
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                SubstationSwitchyardHeroCanvas(modifier = Modifier.matchParentSize())

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF06101E).copy(alpha = 0.95f),
                                    Color(0xFF0A192F).copy(alpha = 0.90f),
                                    Color(0xFF081528).copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ElectricalBlue.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                    ) {
                        Text(
                            text = "INDUSTRIAL POWER SYSTEMS & ENGINEERING PLATFORM",
                            color = Color(0xFFE2E8F0),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "Electrical Engineer Pro",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "Smart Engineering Calculations, Maintenance, Protection, and Reporting in One Platform",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFBAE6FD),
                        lineHeight = 22.sp
                    )

                    Text(
                        text = "Engineered for electrical companies, utilities, oil & gas operators, and commissioning technicians to streamline calculations, track equipment health, analyze faults, and generate authorized compliance certificates.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 18.sp
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToSignUp,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text("Get Started Free", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }

                        OutlinedButton(
                            onClick = onNavigateToLogin,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text("Sign In to Account", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onNavigateToAbout,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFBAE6FD)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text("About Developer", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Feature Highlights Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "CORE INDUSTRIAL CAPABILITIES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricalBlue,
                letterSpacing = 1.sp
            )

            val features = listOf(
                Triple(Icons.Default.Calculate, "Engineering Calculators", "IEC 60076, 60255 & 60364 compliant formulas for Full Load Current, Voltage Drop, Transformer Fault Level, Cable Derating, and Motor Inrush."),
                Triple(Icons.Default.Shield, "Protective Relay Coordination (TCC)", "Interactive Time-Current curve plotting, discrimination margin verification (IEEE 242), and automated TMS optimization."),
                Triple(Icons.Default.Sensors, "Real-Time SCADA Integration", "Live telemetry gateway with Modbus TCP/IP, IEC 60870-5-104, and DNP3 ingestion for power transformers and circuit breakers."),
                Triple(Icons.Default.PrecisionManufacturing, "Asset Management & Photo Evidence", "9-tab equipment details with nameplate verification, vector group specifications, and site photo evidence gallery."),
                Triple(Icons.Default.Build, "PM and PdM Maintenance", "Due Today, Due This Week, and Overdue tracking with field inspection checklists and reliability metrics."),
                Triple(Icons.Default.Warning, "Diagnostic Fault Root Cause Analysis", "3-tier structured diagnostic investigation matching SCADA trip logs, physical checks, and insulation test findings."),
                Triple(Icons.Default.Assessment, "Professional Test Reports", "Official test certificates with corporate branding, client witness declarations, and PDF compliant formatting.")
            )

            features.forEach { (icon, title, desc) ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF0F1E33), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(20.dp))
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }

        // About Developer Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B192C)),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(ElectricalBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Engineering, contentDescription = null, tint = Color(0xFF38BDF8))
                        }
                        Column {
                            Text(text = "ABOUT THE DEVELOPER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), letterSpacing = 1.sp)
                            Text(text = "Muhammad Imran", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(text = "14 Years Field Exp", fontSize = 10.sp, color = Color(0xFFCBD5E1), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }

                Text(
                    text = "Electrical and IT Engineer with extensive field experience in electrical engineering, power systems, oil and gas projects, substations, testing, commissioning, protection, and industrial systems. Substantial work experience in Oman on 132/33 kV substations and oilfield electrical infrastructures.",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 17.sp
                )

                Button(
                    onClick = onNavigateToAbout,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "Read Full Engineering Background", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Footer & Legal links
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "Privacy Policy", fontSize = 11.sp, color = ElectricalBlue, modifier = Modifier.clickable(onClick = onNavigateToPrivacy))
                    Text(text = "Terms & Conditions", fontSize = 11.sp, color = ElectricalBlue, modifier = Modifier.clickable(onClick = onNavigateToTerms))
                    Text(text = "About", fontSize = 11.sp, color = ElectricalBlue, modifier = Modifier.clickable(onClick = onNavigateToAbout))
                    Text(text = "Contact", fontSize = 11.sp, color = ElectricalBlue, modifier = Modifier.clickable(onClick = onNavigateToContact))
                }

                Text(
                    text = "© 2026 Electrical Engineer Pro • Developed by Muhammad Imran",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
