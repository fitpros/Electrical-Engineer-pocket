package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val displayName: String) {
    ADMIN("Administrator"),
    ENGINEER("Electrical Engineer"),
    SUPERVISOR("Electrical Supervisor"),
    TECHNICIAN("Industrial Technician"),
    STANDARD_USER("Standard User"),
    VIEWER("Viewer")
}

enum class AccountStatus(val displayName: String) {
    ACTIVE("Active"),
    DISABLED("Disabled by Admin"),
    PENDING_VERIFICATION("Pending Email Verification")
}

enum class SyncStatus(val displayName: String) {
    SYNCED("Synced to Google Sheet"),
    PENDING("Queued / Pending"),
    FAILED("Sync Failed - Retry Needed")
}

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val userId: String,
    val fullName: String,
    val email: String,
    val passwordHash: String, // SHA-256 salted hash, never plaintext
    val phone: String,
    val country: String,
    val city: String = "",
    val profession: String,
    val company: String = "",
    val jobTitle: String = "",
    val profilePhotoUri: String = "",
    val role: UserRole = UserRole.ENGINEER,
    val registrationDate: String,
    val lastLogin: String,
    val accountStatus: AccountStatus = AccountStatus.ACTIVE,
    val emailVerified: Boolean = true,
    val registrationSource: String = "EMAIL_PASSWORD", // EMAIL_PASSWORD, GOOGLE
    val onboardingCompleted: Boolean = false,
    val mainInterests: String = "Calculations;Protection;Transformers;Maintenance" // Semicolon-delimited
)

@Entity(tableName = "google_sheet_sync_queue")
data class GoogleSheetSyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val country: String,
    val city: String,
    val profession: String,
    val company: String,
    val jobTitle: String,
    val registrationDate: String,
    val emailVerified: Boolean,
    val accountStatus: String,
    val registrationSource: String,
    val lastLogin: String,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastAttemptTimestamp: String,
    val errorMessage: String = ""
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: String,
    val userId: String,
    val userEmail: String,
    val action: String, // ACCOUNT_CREATED, LOGIN, LOGOUT, PROFILE_UPDATED, PASSWORD_CHANGED, REPORT_GENERATED, RECORD_DELETED, ACCOUNT_DISABLED, ACCOUNT_DELETED
    val details: String
)

@Entity(tableName = "contact_messages")
data class ContactMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val subject: String,
    val message: String,
    val timestamp: String,
    val status: String = "UNREAD" // UNREAD, REVIEWED
)

@Entity(tableName = "developer_profile")
data class DeveloperProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Muhammad Imran",
    val title: String = "Electrical and IT Engineer",
    val experienceYears: Int = 14,
    val summary: String = "Muhammad Imran is an experienced Electrical and IT professional with extensive field experience in electrical engineering, power systems, oil and gas projects, substations, electrical maintenance, testing, commissioning, protection, and industrial electrical systems. His professional background includes approximately 14 years of electrical field experience with substantial work experience in Oman, including oil and gas electrical projects.",
    val electricalExperience: String = "132 kV and 33 kV substations;33 kV overhead lines;Power transformers;HV and LV cables;Electrical testing and commissioning;Protection relays;SCADA and marshalling systems;Preventive maintenance;Predictive maintenance;Electrical fault analysis;Well hook up and oil field electrical systems;Power system studies;Electrical documentation and reporting",
    val itExperience: String = "BSc Computer Science;Networking and IT systems;Industrial automation, AI software and digital engineering tools;Cloud infrastructure & telemetry",
    val softwareTools: String = "ETAP;AutoCAD Electrical;MATLAB;PSCAD;DIgSILENT;Revit;Microsoft Office;Electrical automation software",
    val purposeStatement: String = "Electrical Engineer Pro was created to make practical electrical engineering calculations, maintenance management, protection information, inspections, fault documentation, and engineering reports easier, faster, and more organized for engineers, technicians, supervisors, and students. The goal is to combine real field experience with modern digital tools in one easy-to-use engineering platform.",
    val professionalEmail: String = "",
    val linkedInUrl: String = "",
    val websiteUrl: String = "",
    val photoUri: String = ""
)
