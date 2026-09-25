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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.auth.AuthManager
import com.example.ui.components.SubstationSwitchyardHeroCanvas
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusPassGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutDeveloperScreen(
    authManager: AuthManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val devProfile by authManager.developerProfile.collectAsState(initial = null)

    // Contact Form state
    var contactName by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var contactSubject by remember { mutableStateOf("") }
    var contactMessage by remember { mutableStateOf("") }
    var contactFeedback by remember { mutableStateOf<String?>(null) }
    var isSending by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Navigation Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "About the Developer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Section: Substation background with dark overlay & profile badge
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF071426)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF1E3A5F), Color(0xFF0052A3))
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Profile photo placeholder
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color(0xFF0F1E33), CircleShape)
                                    .border(2.5.dp, ElectricalBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (devProfile?.photoUri?.isNotBlank() == true) {
                                    AsyncImage(
                                        model = devProfile!!.photoUri,
                                        contentDescription = "Muhammad Imran",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                                    )
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.Engineering, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(32.dp))
                                        Text(text = "Photo", fontSize = 9.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = devProfile?.name ?: "Muhammad Imran",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = devProfile?.title ?: "Electrical and IT Engineer",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                                Text(
                                    text = "Power Systems • Substations • Protection • Maintenance • Digital Engineering",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // Key Highlights 5-Card Matrix
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DeveloperPillCard("Electrical Engineering", "14 Years Field Experience", Icons.Default.ElectricBolt, Modifier.weight(1f))
                DeveloperPillCard("Oil and Gas", "Oman Projects", Icons.Default.PrecisionManufacturing, Modifier.weight(1f))
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DeveloperPillCard("Substations", "132 kV & 33 kV Systems", Icons.Default.Business, Modifier.weight(1f))
                DeveloperPillCard("Protection & SCADA", "Maintenance & Commissioning", Icons.Default.Shield, Modifier.weight(1f))
                DeveloperPillCard("IT & Automation", "BSc Computer Science", Icons.Default.Computer, Modifier.weight(1f))
            }

            // Purpose of Electrical Engineer Pro
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B192C)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(Color(0xFF0066CC), Color(0xFF1E3A5F)))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "PURPOSE OF ELECTRICAL ENGINEER PRO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = devProfile?.purposeStatement ?: "Electrical Engineer Pro was created to make practical electrical engineering calculations, maintenance management, protection information, inspections, fault documentation, and engineering reports easier, faster, and more organized for engineers, technicians, supervisors, and students. The goal is to combine real field experience with modern digital tools in one easy to use engineering platform.",
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 18.sp
                    )
                }
            }

            // Section 1: Professional Summary & Electrical Engineering Experience
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Electrical & Field Engineering Experience", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = devProfile?.summary ?: "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Text(text = "Technical Competencies & Systems:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ElectricalBlue)
                    val electricalItems = devProfile?.electricalExperience?.split(";") ?: emptyList()
                    electricalItems.forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = StatusPassGreen, modifier = Modifier.size(15.dp))
                            Text(text = item, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // Section 2: IT, Software Tools & Digital Engineering
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Information Technology & Digital Skills", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "Holds a Bachelor of Science (BSc) in Computer Science, combining electrical power engineering with networking, industrial protocols, and digital software development.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Text(text = "Power System & Engineering Software:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ElectricalBlue)
                    val tools = devProfile?.softwareTools?.split(";") ?: emptyList()
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        tools.forEach { tool ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF0F1E33),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F))
                            ) {
                                Text(
                                    text = tool,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Section 3: Owner Contact Information & Interactive Contact Form
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Contact the Engineering Team", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "Submit technical inquiries, substation study requests, or tool collaboration messages.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (contactFeedback != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusPassGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusPassGreen)
                        ) {
                            Text(
                                text = contactFeedback!!,
                                color = StatusPassGreen,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { contactName = it },
                        label = { Text("Your Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = { contactEmail = it },
                        label = { Text("Email Address *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = contactSubject,
                        onValueChange = { contactSubject = it },
                        label = { Text("Subject") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = contactMessage,
                        onValueChange = { contactMessage = it },
                        label = { Text("Message / Engineering Inquiry *") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (contactName.isBlank() || contactEmail.isBlank() || contactMessage.isBlank()) {
                                contactFeedback = "Please fill in Name, Email, and Message."
                            } else {
                                isSending = true
                                coroutineScope.launch {
                                    val res = authManager.submitContactMessage(contactName, contactEmail, contactSubject, contactMessage)
                                    isSending = false
                                    if (res.isSuccess) {
                                        contactFeedback = "Thank you! Your message has been securely submitted to Muhammad Imran's team."
                                        contactName = ""
                                        contactEmail = ""
                                        contactSubject = ""
                                        contactMessage = ""
                                    } else {
                                        contactFeedback = res.exceptionOrNull()?.message ?: "Submission failed."
                                    }
                                }
                            }
                        },
                        enabled = !isSending,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isSending) "Sending..." else "Send Message", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DeveloperPillCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E33)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(20.dp))
            Column {
                Text(text = title, fontSize = 10.sp, color = Color(0xFF94A3B8))
                Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
