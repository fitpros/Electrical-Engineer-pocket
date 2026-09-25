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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthManager
import com.example.data.model.AccountStatus
import com.example.data.model.AuditLogEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.DeveloperProfileEntity
import com.example.data.model.GoogleSheetSyncEntity
import com.example.data.model.SyncStatus
import com.example.data.model.UserAccountEntity
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusPassGreen
import com.example.ui.theme.StatusWarningAmber
import kotlinx.coroutines.launch

enum class AdminTab(val title: String) {
    OVERVIEW("Overview"),
    USERS("Users Directory"),
    SHEETS_SYNC("Google Sheets Sync"),
    CONTACT_MESSAGES("Inquiries & Feedback"),
    ABOUT_EDITOR("About Page Editor"),
    AUDIT_LOGS("System Audit Logs")
}

@Composable
fun AdminPanelScreen(
    authManager: AuthManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(AdminTab.OVERVIEW) }

    val allUsers by authManager.allUsers.collectAsState(initial = emptyList())
    val allSyncJobs by authManager.allSyncJobs.collectAsState(initial = emptyList())
    val allMessages by authManager.allContactMessages.collectAsState(initial = emptyList())
    val allAuditLogs by authManager.allAuditLogs.collectAsState(initial = emptyList())
    val devProfile by authManager.developerProfile.collectAsState(initial = null)

    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = ElectricalBlue)
                        Column {
                            Text(text = "System Administration Panel", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "Privileged Access: Chief Engineer & Platform Administrator", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = ElectricalBlue
                ) {
                    AdminTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(text = tab.title, fontSize = 12.sp, fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal)
                            }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                AdminTab.OVERVIEW -> AdminOverviewTab(
                    totalUsers = allUsers.size,
                    activeUsers = allUsers.count { it.accountStatus == AccountStatus.ACTIVE },
                    syncedCount = allSyncJobs.count { it.syncStatus == SyncStatus.SYNCED },
                    pendingSync = allSyncJobs.count { it.syncStatus == SyncStatus.PENDING },
                    messagesCount = allMessages.size
                )

                AdminTab.USERS -> AdminUsersTab(
                    users = allUsers,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onToggleStatus = { user ->
                        coroutineScope.launch {
                            authManager.toggleUserAccountStatus(user)
                        }
                    }
                )

                AdminTab.SHEETS_SYNC -> AdminSheetsSyncTab(
                    syncJobs = allSyncJobs,
                    onRetrySync = { job ->
                        coroutineScope.launch {
                            authManager.retryGoogleSheetSync(job)
                        }
                    }
                )

                AdminTab.CONTACT_MESSAGES -> AdminContactMessagesTab(messages = allMessages)

                AdminTab.ABOUT_EDITOR -> devProfile?.let { prof ->
                    AdminAboutEditorTab(
                        profile = prof,
                        onSaveProfile = { updated ->
                            coroutineScope.launch {
                                authManager.updateDeveloperProfile(updated)
                            }
                        }
                    )
                }

                AdminTab.AUDIT_LOGS -> AdminAuditLogsTab(logs = allAuditLogs)
            }
        }
    }
}

@Composable
fun AdminOverviewTab(
    totalUsers: Int,
    activeUsers: Int,
    syncedCount: Int,
    pendingSync: Int,
    messagesCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminMetricCard("Total Users", totalUsers.toString(), ElectricalBlue, Modifier.weight(1f))
            AdminMetricCard("Active Accounts", activeUsers.toString(), StatusPassGreen, Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminMetricCard("Google Sheets Synced", syncedCount.toString(), StatusPassGreen, Modifier.weight(1f))
            AdminMetricCard("Sync Pending / Retry", pendingSync.toString(), StatusWarningAmber, Modifier.weight(1f))
        }

        AdminMetricCard("Inquiries & Feedback", messagesCount.toString(), Color(0xFF0284C7), Modifier.fillMaxWidth())

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Security & Synchronization Integrity", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    text = "• Google Sheets integration automatically receives user profile rows without passwords.\n• All passwords hashed via salted SHA-256.\n• Full audit trails stored for account events and compliance.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun AdminMetricCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AdminUsersTab(
    users: List<UserAccountEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onToggleStatus: (UserAccountEntity) -> Unit
) {
    val filtered = users.filter { u ->
        searchQuery.isBlank() ||
                u.fullName.contains(searchQuery, ignoreCase = true) ||
                u.email.contains(searchQuery, ignoreCase = true) ||
                u.profession.contains(searchQuery, ignoreCase = true)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search by name, email, or profession...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricalBlue) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { it.userId }) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = user.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = user.email, fontSize = 11.sp, color = ElectricalBlue)
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (user.accountStatus == AccountStatus.ACTIVE) StatusPassGreen.copy(alpha = 0.2f) else StatusCriticalRed.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = user.accountStatus.displayName,
                                    color = if (user.accountStatus == AccountStatus.ACTIVE) StatusPassGreen else StatusCriticalRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Role: ${user.role.displayName} • Country: ${user.country} • Registered: ${user.registrationDate}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Last Login: ${user.lastLogin}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            OutlinedButton(
                                onClick = { onToggleStatus(user) },
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(text = if (user.accountStatus == AccountStatus.ACTIVE) "Disable" else "Enable", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSheetsSyncTab(
    syncJobs: List<GoogleSheetSyncEntity>,
    onRetrySync: (GoogleSheetSyncEntity) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0F1E33),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Google Sheets Backend Synchronization Queue", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF38BDF8))
                Text(
                    text = "Columns: User ID, Full Name, Email, Phone, Country, City, Profession, Company, Job Title, Registration Date, Status. Passwords are never sent.",
                    fontSize = 10.sp,
                    color = Color(0xFFCBD5E1)
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(syncJobs, key = { it.id }) { job ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = job.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = job.email, fontSize = 11.sp, color = ElectricalBlue)
                            Text(text = "Synced: ${job.lastAttemptTimestamp}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StatusPassGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusPassGreen)
                        ) {
                            Text(
                                text = job.syncStatus.displayName,
                                color = StatusPassGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminContactMessagesTab(messages: List<ContactMessageEntity>) {
    if (messages.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No contact inquiries received yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(messages, key = { it.id }) { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "${msg.name} (${msg.email})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = msg.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(text = "Subject: ${msg.subject}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ElectricalBlue)
                        Text(text = msg.message, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAboutEditorTab(
    profile: DeveloperProfileEntity,
    onSaveProfile: (DeveloperProfileEntity) -> Unit
) {
    var name by remember(profile) { mutableStateOf(profile.name) }
    var title by remember(profile) { mutableStateOf(profile.title) }
    var summary by remember(profile) { mutableStateOf(profile.summary) }
    var email by remember(profile) { mutableStateOf(profile.professionalEmail) }
    var savedNotice by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Edit Founder & Developer Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        if (savedNotice) {
            Surface(shape = RoundedCornerShape(4.dp), color = StatusPassGreen.copy(alpha = 0.2f)) {
                Text(text = "Profile updated successfully!", color = StatusPassGreen, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Developer Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Professional Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = summary,
            onValueChange = { summary = it },
            label = { Text("Professional Summary & Experience") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Contact Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSaveProfile(profile.copy(name = name, title = title, summary = summary, professionalEmail = email))
                savedNotice = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricalBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Developer Profile")
        }
    }
}

@Composable
fun AdminAuditLogsTab(logs: List<AuditLogEntity>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(logs, key = { it.id }) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ElectricalBlue)
                        Text(text = log.timestamp, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(text = "User: ${log.userEmail}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = log.details, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
