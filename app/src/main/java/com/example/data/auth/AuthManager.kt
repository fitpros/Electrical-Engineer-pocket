package com.example.data.auth

import com.example.data.dao.AuthAndAdminDao
import com.example.data.model.AccountStatus
import com.example.data.model.AuditLogEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.DeveloperProfileEntity
import com.example.data.model.GoogleSheetSyncEntity
import com.example.data.model.SyncStatus
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class AuthManager(private val dao: AuthAndAdminDao) {

    private val _currentUser = MutableStateFlow<UserAccountEntity?>(null)
    val currentUser: StateFlow<UserAccountEntity?> = _currentUser.asStateFlow()

    private val _rememberMe = MutableStateFlow(true)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()

    fun setRememberMe(value: Boolean) {
        _rememberMe.value = value
    }

    val allUsers: Flow<List<UserAccountEntity>> = dao.getAllUsers()
    val allSyncJobs: Flow<List<GoogleSheetSyncEntity>> = dao.getAllSyncJobs()
    val allAuditLogs: Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()
    val allContactMessages: Flow<List<ContactMessageEntity>> = dao.getAllContactMessages()
    val developerProfile: Flow<DeveloperProfileEntity?> = dao.getDeveloperProfile()

    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
    }

    // Local/offline credential fallback only. Production accounts should use Firebase Auth.
    // PBKDF2 uses a unique random salt per password and stores algorithm metadata with the hash.
    fun hashPassword(password: String): String {
        val iterations = 210_000
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, 256)
        val hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .encoded
        return "pbkdf2_sha256:$iterations:${android.util.Base64.encodeToString(salt, android.util.Base64.NO_WRAP)}:${android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)}"
    }

    private fun verifyPassword(password: String, stored: String): Boolean {
        val parts = stored.split(":")
        if (parts.size != 4 || parts[0] != "pbkdf2_sha256") return false

        return runCatching {
            val iterations = parts[1].toInt()
            val salt = android.util.Base64.decode(parts[2], android.util.Base64.NO_WRAP)
            val expected = android.util.Base64.decode(parts[3], android.util.Base64.NO_WRAP)
            val spec = PBEKeySpec(password.toCharArray(), salt, iterations, expected.size * 8)
            val actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded
            MessageDigest.isEqual(expected, actual)
        }.getOrDefault(false)
    }
    suspend fun initializeDefaultAdminIfEmpty() {
        // Never create or auto-login a privileged account in a distributable APK.
        // The platform owner/admin must be provisioned by the production identity backend.
        val existingProfile = dao.getDeveloperProfile().first()
        if (existingProfile == null) {
            dao.insertDeveloperProfile(
                DeveloperProfileEntity(
                    id = 1,
                    name = "Muhammad Imran",
                    title = "Electrical and IT Engineer",
                    experienceYears = 14,
                    summary = "Electrical and IT professional with field experience in power systems, oil and gas electrical projects, substations, testing, commissioning, protection, maintenance and industrial electrical systems.",
                    electricalExperience = "132 kV and 33 kV substations;33 kV overhead lines;Power transformers;HV and LV cables;Electrical testing and commissioning;Protection relays;SCADA and marshalling systems;Preventive maintenance;Predictive maintenance;Electrical fault analysis;Oil field electrical systems;Power system studies;Electrical documentation and reporting",
                    itExperience = "BSc Computer Science;Networking and IT experience;Automation, AI software and digital engineering tools",
                    softwareTools = "ETAP;AutoCAD Electrical;MATLAB;PSCAD;DIgSILENT;Revit;Microsoft Office",
                    purposeStatement = "Electrical Engineer Pocket was created to make practical electrical calculations, maintenance management, protection information, inspections, fault documentation and engineering reports easier and more organized for engineers, technicians, supervisors and students.",
                    professionalEmail = "",
                    linkedInUrl = "",
                    websiteUrl = ""
                )
            )
        }
    }
    suspend fun signUp(
        fullName: String,
        email: String,
        phone: String,
        country: String,
        city: String,
        profession: String,
        company: String,
        jobTitle: String,
        password: String
    ): Result<UserAccountEntity> {
        val normalizedEmail = email.trim().lowercase(Locale.ROOT)
        val existing = dao.findUserByEmailDirect(normalizedEmail)
        if (existing != null) {
            return Result.failure(Exception("An account with this email address already exists. Please log in instead."))
        }

        val userId = UUID.randomUUID().toString()
        val now = getCurrentTimestamp()
        val user = UserAccountEntity(
            userId = userId,
            fullName = fullName.trim(),
            email = normalizedEmail,
            passwordHash = hashPassword(password),
            phone = phone.trim(),
            country = country.trim(),
            city = city.trim(),
            profession = profession.trim(),
            company = company.trim(),
            jobTitle = jobTitle.trim(),
            role = UserRole.STANDARD_USER,
            registrationDate = now,
            lastLogin = now,
            accountStatus = AccountStatus.PENDING_VERIFICATION,
            emailVerified = false,
            registrationSource = "LOCAL_EMAIL_PASSWORD",
            onboardingCompleted = false
        )

        dao.insertUser(user)

        // Queue Google Sheet Synchronization
        queueGoogleSheetSync(user, "NEW_REGISTRATION")

        // Record Audit Event
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = now,
                userId = userId,
                userEmail = normalizedEmail,
                action = "ACCOUNT_CREATED",
                details = "User registered with profession: $profession, country: $country. Queued for Google Sheets sync."
            )
        )

        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun login(email: String, password: String): Result<UserAccountEntity> {
        val normalizedEmail = email.trim().lowercase(Locale.ROOT)
        val user = dao.findUserByEmailDirect(normalizedEmail)
            ?: return Result.failure(Exception("No account found with this email. Please check your credentials or Sign Up."))

        if (user.accountStatus == AccountStatus.DISABLED) {
            return Result.failure(Exception("This account has been disabled by the system administrator."))
        }

        if (!verifyPassword(password, user.passwordHash)) {
            return Result.failure(Exception("Incorrect password. Please verify and try again or use Forgot Password."))
        }

        val now = getCurrentTimestamp()
        val updatedUser = user.copy(lastLogin = now)
        dao.updateUser(updatedUser)

        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = now,
                userId = user.userId,
                userEmail = user.email,
                action = "LOGIN",
                details = "Successful login via Email/Password authentication."
            )
        )

        _currentUser.value = updatedUser
        return Result.success(updatedUser)
    }

    suspend fun googleSignIn(googleEmail: String, googleName: String): Result<UserAccountEntity> {
        // Do not simulate Google OAuth using caller-supplied name/email.
        // This method intentionally fails until Firebase Auth + Credential Manager is configured.
        return Result.failure(
            IllegalStateException(
                "Google Sign-In is not configured yet. Connect Firebase Authentication and verify the Google ID token before enabling this option."
            )
        )
    }
    suspend fun logout() {
        val user = _currentUser.value
        if (user != null) {
            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = getCurrentTimestamp(),
                    userId = user.userId,
                    userEmail = user.email,
                    action = "LOGOUT",
                    details = "User signed out."
                )
            )
        }
        _currentUser.value = null
    }

    suspend fun completeOnboarding(role: UserRole, interests: List<String>) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            role = if (current.role == UserRole.ADMIN) UserRole.ADMIN else role,
            onboardingCompleted = true,
            mainInterests = interests.joinToString(";")
        )
        dao.updateUser(updated)
        _currentUser.value = updated
    }

    suspend fun updateUserProfile(updated: UserAccountEntity) {
        dao.updateUser(updated)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                userId = updated.userId,
                userEmail = updated.email,
                action = "PROFILE_UPDATED",
                details = "User profile updated for ${updated.fullName}."
            )
        )
        _currentUser.value = updated
        queueGoogleSheetSync(updated, "PROFILE_UPDATE")
    }

    suspend fun changePassword(oldPass: String, newPass: String): Result<Unit> {
        val current = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        if (current.passwordHash != hashPassword(oldPass)) {
            return Result.failure(Exception("Current password does not match."))
        }
        val updated = current.copy(passwordHash = hashPassword(newPass))
        dao.updateUser(updated)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                userId = current.userId,
                userEmail = current.email,
                action = "PASSWORD_CHANGED",
                details = "User successfully updated account password."
            )
        )
        _currentUser.value = updated
        return Result.success(Unit)
    }

    suspend fun resetPassword(email: String, newPass: String): Result<Unit> {
        // A reset based only on an email address is insecure. Production reset must be
        // performed by Firebase Auth (or another identity provider) using a verified reset link.
        return Result.failure(
            IllegalStateException(
                "Secure password reset is not configured yet. Enable Firebase Authentication password-reset email flow before release."
            )
        )
    }
    suspend fun deleteAccount(userId: String): Result<Unit> {
        val user = dao.getUserById(userId)
        dao.deleteUserById(userId)
        dao.deleteSyncJobsForUser(userId)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                userId = userId,
                userEmail = "anonymized@deleted",
                action = "ACCOUNT_DELETED",
                details = "User requested complete account deletion and data anonymization."
            )
        )
        if (_currentUser.value?.userId == userId) {
            _currentUser.value = null
        }
        return Result.success(Unit)
    }

    suspend fun setUserStatus(userId: String, status: AccountStatus) {
        val target = dao.getUserById(userId) ?: return
        dao.updateUser(target.copy(accountStatus = status))
    }

    suspend fun toggleUserAccountStatus(user: UserAccountEntity) {
        val newStatus = if (user.accountStatus == AccountStatus.ACTIVE) AccountStatus.DISABLED else AccountStatus.ACTIVE
        val updated = user.copy(accountStatus = newStatus)
        dao.updateUser(updated)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                userId = user.userId,
                userEmail = user.email,
                action = if (newStatus == AccountStatus.ACTIVE) "ACCOUNT_ENABLED" else "ACCOUNT_DISABLED",
                details = "Account status changed to ${newStatus.name} by administrator."
            )
        )
    }

    // Google Sheets synchronization queue
    private suspend fun queueGoogleSheetSync(user: UserAccountEntity, reason: String) {
        val syncJob = GoogleSheetSyncEntity(
            userId = user.userId,
            email = user.email,
            fullName = user.fullName,
            phone = user.phone,
            country = user.country,
            city = user.city,
            profession = user.profession,
            company = user.company,
            jobTitle = user.jobTitle,
            registrationDate = user.registrationDate,
            emailVerified = user.emailVerified,
            accountStatus = user.accountStatus.displayName,
            registrationSource = user.registrationSource,
            lastLogin = user.lastLogin,
            syncStatus = SyncStatus.PENDING, // Pending until a real backend confirms the Google Sheets write
            lastAttemptTimestamp = getCurrentTimestamp(),
            errorMessage = ""
        )
        dao.insertSyncJob(syncJob)
    }

    suspend fun retryGoogleSheetSync(job: GoogleSheetSyncEntity) {
        val updated = job.copy(
            syncStatus = SyncStatus.PENDING,
            lastAttemptTimestamp = getCurrentTimestamp(),
            errorMessage = "Google Sheets backend is not configured. No remote write has been confirmed."
        )
        dao.updateSyncJob(updated)
    }

    suspend fun submitContactMessage(name: String, email: String, subject: String, message: String): Result<Unit> {
        if (name.isBlank() || email.isBlank() || message.isBlank()) {
            return Result.failure(Exception("Please fill in all required fields."))
        }
        dao.insertContactMessage(
            ContactMessageEntity(
                name = name.trim(),
                email = email.trim(),
                subject = subject.trim().ifBlank { "General Engineering Inquiry" },
                message = message.trim(),
                timestamp = getCurrentTimestamp()
            )
        )
        return Result.success(Unit)
    }

    suspend fun updateDeveloperProfile(profile: DeveloperProfileEntity) {
        dao.insertDeveloperProfile(profile)
    }
}
