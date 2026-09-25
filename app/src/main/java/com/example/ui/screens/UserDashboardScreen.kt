package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.auth.AuthManager
import com.example.data.model.EquipmentEntity
import com.example.data.model.MaintenanceStatus
import com.example.data.model.MaintenanceTaskEntity
import com.example.data.model.TestRecordEntity
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserRole
import com.example.ui.components.NavDestination
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusPassGreen
import com.example.ui.theme.StatusWarningAmber
import kotlinx.coroutines.launch

@Composable
fun UserDashboardScreen(
    user: UserAccountEntity,
    authManager: AuthManager,
    equipmentList: List<EquipmentEntity>,
    tasks: List<MaintenanceTaskEntity>,
    tests: List<TestRecordEntity>,
    onNavigate: (NavDestination) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val pendingTasks = tasks.count { it.status != MaintenanceStatus.COMPLETED }
    val completedTests = tests.size

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF0F1E33), CircleShape)
                            .border(2.dp, ElectricalBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (user.profilePhotoUri.isNotBlank()) {
                            AsyncImage(
                                model = user.profilePhotoUri,
                                contentDescription = user.fullName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(
                                text = user.fullName.firstOrNull()?.toString() ?: "U",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = user.fullName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (user.emailVerified) {
                                Icon(
                                    Icons.Default.VerifiedUser,
                                    contentDescription = "Verified Email",
                                    tint = StatusPassGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "${user.profession} • ${user.role.displayName}",
                            fontSize = 12.sp,
                            color = ElectricalBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (user.company.isNotBlank()) {
                            Text(
                                text = "${user.company} (${user.country})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Google Sheets Sync Status Pill
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = StatusPassGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusPassGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = StatusPassGreen, modifier = Modifier.size(12.dp))
                            Text(text = "Synced", fontSize = 10.sp, color = StatusPassGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Action buttons row: Edit Profile, Change Password, Logout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Edit Profile", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showChangePasswordDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Password", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                authManager.logout()
                                onLogout()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Logout", fontSize = 11.sp)
                    }
                }

                // If Admin, show link to Admin panel
                if (user.role == UserRole.ADMIN) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ElectricalBlue.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricalBlue.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(NavDestination.ADMIN_PANEL) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = ElectricalBlue)
                                Column {
                                    Text(text = "Enterprise Admin Panel & Google Sheets Sync", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricalBlue)
                                    Text(text = "Manage users, inspect sync queue, audit security logs", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Quick Actions Grid
        Text(
            text = "ENGINEERING MODULE SHORTCUTS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricalBlue,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UserQuickActionCard("Calculations", Icons.Default.Calculate, Modifier.weight(1f)) {
                onNavigate(NavDestination.CALCULATORS)
            }
            UserQuickActionCard("My Equipment", Icons.Default.PrecisionManufacturing, Modifier.weight(1f)) {
                onNavigate(NavDestination.EQUIPMENT)
            }
            UserQuickActionCard("Inspections", Icons.Default.FactCheck, Modifier.weight(1f)) {
                onNavigate(NavDestination.INSPECTIONS)
            }
            UserQuickActionCard("Reports", Icons.Default.Assessment, Modifier.weight(1f)) {
                onNavigate(NavDestination.REPORTS)
            }
        }

        // Summary Metric Rows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricPillCard("Registered Assets", equipmentList.size.toString(), Icons.Default.PrecisionManufacturing, Modifier.weight(1f))
            MetricPillCard("Pending PM Tasks", pendingTasks.toString(), Icons.Default.Build, Modifier.weight(1f))
            MetricPillCard("Test Certificates", completedTests.toString(), Icons.Default.Assessment, Modifier.weight(1f))
        }

        // User Profile Database Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Profile & Account Specifications",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                ProfileDataRow("User ID", user.userId)
                ProfileDataRow("Email", user.email)
                ProfileDataRow("Phone", if (user.phone.isNotBlank()) user.phone else "Not specified")
                ProfileDataRow("Location", "${user.city.ifBlank { "N/A" }}, ${user.country}")
                ProfileDataRow("Profession", user.profession)
                ProfileDataRow("Company", user.company.ifBlank { "N/A" })
                ProfileDataRow("Job Title", user.jobTitle.ifBlank { "N/A" })
                ProfileDataRow("Account Role", user.role.displayName)
                ProfileDataRow("Registration Date", user.registrationDate)
                ProfileDataRow("Last Login", user.lastLogin)
                ProfileDataRow("Status", user.accountStatus.displayName)
                ProfileDataRow("Email Verified", if (user.emailVerified) "Yes (Verified)" else "Pending Verification")
                ProfileDataRow("Registration Source", user.registrationSource)

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Delete Account",
                        color = StatusCriticalRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showDeleteAccountDialog = true }
                    )
                }
            }
        }

        // Recent Activity & Operations
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Recent Engineering Activity", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                listOf(
                    "Logged Insulation Resistance test certificate for 132-TR-01" to "Today 09:15",
                    "Completed secondary current injection on ABB REF615 relay" to "Yesterday 14:30",
                    "Reviewed 33kV switchgear contact resistance and gas pressure" to "22 Sep 2026",
                    "Verified Time-Current Coordination (TCC) for Feeder Bay 4" to "20 Sep 2026"
                ).forEach { (activity, time) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusPassGreen, modifier = Modifier.size(15.dp))
                            Text(text = activity, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(text = time, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                }
            }
        }
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmNewPassword by remember { mutableStateOf("") }
        var passError by remember { mutableStateOf<String?>(null) }
        var passSuccess by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Account Password", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (passError != null) {
                        Text(passError!!, color = StatusCriticalRed, fontSize = 12.sp)
                    }
                    if (passSuccess) {
                        Text("Password changed successfully!", color = StatusPassGreen, fontSize = 12.sp)
                    }
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password (Min. 6 chars)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Confirm New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            oldPassword.isBlank() -> passError = "Enter current password"
                            newPassword.length < 6 -> passError = "Password must be at least 6 characters"
                            newPassword != confirmNewPassword -> passError = "Passwords do not match"
                            else -> {
                                passError = null
                                coroutineScope.launch {
                                    val res = authManager.changePassword(oldPassword, newPassword)
                                    if (res.isSuccess) {
                                        passSuccess = true
                                        showChangePasswordDialog = false
                                    } else {
                                        passError = res.exceptionOrNull()?.message ?: "Update failed"
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue)
                ) {
                    Text("Save Password")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var editFullName by remember { mutableStateOf(user.fullName) }
        var editPhone by remember { mutableStateOf(user.phone) }
        var editCountry by remember { mutableStateOf(user.country) }
        var editCity by remember { mutableStateOf(user.city) }
        var editProfession by remember { mutableStateOf(user.profession) }
        var editCompany by remember { mutableStateOf(user.company) }
        var editJobTitle by remember { mutableStateOf(user.jobTitle) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Professional Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editFullName,
                        onValueChange = { editFullName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Mobile Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCountry,
                        onValueChange = { editCountry = it },
                        label = { Text("Country") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCity,
                        onValueChange = { editCity = it },
                        label = { Text("City") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editProfession,
                        onValueChange = { editProfession = it },
                        label = { Text("Profession") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCompany,
                        onValueChange = { editCompany = it },
                        label = { Text("Company") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editJobTitle,
                        onValueChange = { editJobTitle = it },
                        label = { Text("Job Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val updated = user.copy(
                                fullName = editFullName.ifBlank { user.fullName },
                                phone = editPhone,
                                country = editCountry.ifBlank { user.country },
                                city = editCity,
                                profession = editProfession.ifBlank { user.profession },
                                company = editCompany,
                                jobTitle = editJobTitle
                            )
                            authManager.updateUserProfile(updated)
                            showEditProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Confirm Account Deletion", color = StatusCriticalRed, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete your account (${user.email})? All your personal sync records and settings will be permanently erased.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            authManager.deleteAccount(user.userId)
                            showDeleteAccountDialog = false
                            onLogout()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCriticalRed)
                ) {
                    Text("Permanently Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileDataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun UserQuickActionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(20.dp))
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
fun MetricPillCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Icon(icon, contentDescription = null, tint = ElectricalBlue, modifier = Modifier.size(16.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}
