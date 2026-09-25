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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthManager
import com.example.data.model.UserRole
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusPassGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    authManager: AuthManager,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var currentStep by remember { mutableIntStateOf(1) }

    var selectedRole by remember { mutableStateOf(UserRole.ENGINEER) }
    val availableRoles = listOf(
        UserRole.ENGINEER to "Electrical Engineer",
        UserRole.SUPERVISOR to "Electrical Supervisor",
        UserRole.ENGINEER to "Protection Engineer",
        UserRole.ENGINEER to "Maintenance Engineer",
        UserRole.TECHNICIAN to "Technician",
        UserRole.STANDARD_USER to "Student",
        UserRole.STANDARD_USER to "Other"
    )

    val interestOptions = listOf(
        "Calculations",
        "Protection",
        "Transformers",
        "Cables",
        "Maintenance",
        "PM PDM",
        "Fault Analysis",
        "Solar",
        "Reports"
    )
    val selectedInterests = remember { mutableStateOf(mutableSetOf("Calculations", "Protection", "Transformers", "Maintenance")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Step progress header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "STEP $currentStep OF 4",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricalBlue,
                letterSpacing = 1.sp
            )

            LinearProgressIndicator(
                progress = { currentStep / 4f },
                color = ElectricalBlue,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }

        // Step Content Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentStep) {
                    1 -> {
                        // Step 1: Welcome
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(Color(0xFF0A192F), RoundedCornerShape(14.dp))
                                .border(2.dp, ElectricalBlue, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(40.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Welcome to Electrical Engineer Pro",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your all-in-one industrial platform for high-voltage power calculations, SCADA telemetry, protective relay coordination, asset management, and compliance reporting.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }

                    2 -> {
                        // Step 2: Select Role
                        Text(
                            text = "Select Your Professional Role",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Tailors your calculations and reporting workflows.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            availableRoles.forEach { (role, label) ->
                                val isSel = selectedRole == role
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) ElectricalBlue else MaterialTheme.colorScheme.surface,
                                    border = if (isSel) null else CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedRole = role }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = label,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 13.sp
                                        )
                                        if (isSel) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Step 3: Choose Main Interests
                        Text(
                            text = "Choose Your Primary Interests",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Select modules to pin to your personal dashboard.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            interestOptions.forEach { interest ->
                                val isSelected = selectedInterests.value.contains(interest)
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) ElectricalBlue else MaterialTheme.colorScheme.surface,
                                    border = if (isSelected) null else CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier.clickable {
                                        val updated = selectedInterests.value.toMutableSet()
                                        if (isSelected) updated.remove(interest) else updated.add(interest)
                                        selectedInterests.value = updated
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                        }
                                        Text(
                                            text = interest,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    4 -> {
                        // Step 4: Finish Setup
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(StatusPassGreen.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = StatusPassGreen, modifier = Modifier.size(36.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Setup Completed!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Your engineering workspace is ready. You can calculate, manage equipment, review live SCADA telemetry, and coordinate protection relays anytime.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Navigation Footer Button
        Button(
            onClick = {
                if (currentStep < 4) {
                    currentStep++
                } else {
                    coroutineScope.launch {
                        authManager.completeOnboarding(selectedRole, selectedInterests.value.toList())
                        onComplete()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = if (currentStep == 4) "Go to Dashboard" else "Next Step",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}
