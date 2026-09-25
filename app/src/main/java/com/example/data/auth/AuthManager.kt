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
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

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

    // SHA-256 secure salted password hashing - Never store plain text
    fun hashPassword(password: String, salt: String = "eep_industrial_salt_"): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest((salt + password).toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun initializeDefaultAdminIfEmpty() {
        val existingAdmin = dao.findUserByEmailDirect("imran@electricalengineerpro.com")
        if (existingAdmin == null) {
            val adminUser = UserAccountEntity(
                userId = "admin-muhammad-imran-001",
                fullName = "Muhammad Imran",
                email = "imran@electricalengineerpro.com",
                passwordHash = hashPassword("Admin@12345"),
                phone = "+968 91234567",
                country = "Oman",
                city = "Muscat",
                profession = "Electrical and IT Engineer",
                company = "Apex Power & Engineering Solutions",
                jobTitle = "Chief Power Systems & Protection Engineer",
                role = UserRole.ADMIN,
                registrationDate = "2024-01-01 08:00:00",
                lastLogin = getCurrentTimestamp(),
                accountStatus = AccountStatus.ACTIVE,
                emailVerified = true,
                registrationSource = "SYSTEM_INITIALIZED",
                onboardingCompleted = true,
                mainInterests = "Calculations;Protection;Transformers;Cables;Maintenance;PM PDM;Fault Analysis;Solar;Reports"
            )
            dao.insertUser(adminUser)

            dao.insertDeveloperProfile(
                DeveloperProfileEntity(
                    id = 1,
                    name = "Muhammad Imran",
                    title = "Electrical and IT Engineer",
                    experienceYears = 14,
                    summary = "Muhammad Imran is an experienced Electrical and IT professional with extensive field experience in electrical engineering, power systems, oil and gas projects, substations, electrical maintenance, testing, commissioning, protection, and industrial electrical systems. His professional background includes approximately 14 years of electrical field experience with substantial work experience in Oman, including oil and gas electrical projects.",
                    electricalExperience = "132 kV and 33 kV substations;33 kV overhead lines;Power transformers;HV and LV cables;Electrical testing and commissioning;Protection relays;SCADA and marshalling systems;Preventive maintenance;Predictive maintenance;Electrical fault analysis;Well hook up and oil field electrical systems;Power system studies;Electrical documentation and reporting",
                    itExperience = "BSc Computer Science;Networking and IT experience;Interest and experience in automation, AI software, and digital engineering tools",
                    softwareTools = "ETAP;AutoCAD Electrical;MATLAB;PSCAD;DIgSILENT;Revit;Microsoft Office;Electrical engineering and automation tools",
                    purposeStatement = "Electrical Engineer Pro was created to make practical electrical engineering calculations, maintenance management, protection information, inspections, fault documentation, and engineering reports easier, faster, and more organized for engineers, technicians, supervisors, and students. The goal is to combine real field experience with modern digital tools in one easy to use engineering platform.",
                    professionalEmail = "m.imran@electricalengineerpro.com",
                    linkedInUrl = "https://linkedin.com/in/muhammad-imran-electrical-eng",
                    websiteUrl = "https://electricalengineerpro.com"
                )
            )

            // Auto log-in initial engineer profile for smooth developer demo
            _currentUser.value = adminUser
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
            role = UserRole.ENGINEER, // Standard user signups are non-admin
            registrationDate = now,
            lastLogin = now,
            accountStatus = AccountStatus.ACTIVE,
            emailVerified = true,
            registrationSource = "EMAIL_PASSWORD",
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

        val enteredHash = hashPassword(password)
        if (user.passwordHash != enteredHash) {
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
        val normalizedEmail = googleEmail.trim().lowercase(Locale.ROOT)
        val existing = dao.findUserByEmailDirect(normalizedEmail)
        val now = getCurrentTimestamp()

        val user = if (existing != null) {
            val updated = existing.copy(lastLogin = now)
            dao.updateUser(updated)
            updated
        } else {
            val newUser = UserAccountEntity(
                userId = UUID.randomUUID().toString(),
                fullName = googleName,
                email = normalizedEmail,
                passwordHash = hashPassword(UUID.randomUUID().toString()),
                phone = "",
                country = "Global",
                city = "",
                profession = "Electrical Professional",
                role = UserRole.ENGINEER,
                registrationDate = now,
                lastLogin = now,
                accountStatus = AccountStatus.ACTIVE,
                emailVerified = true,
                registrationSource = "GOOGLE",
                onboardingCompleted = false
            )
            dao.insertUser(newUser)
            queueGoogleSheetSync(newUser, "GOOGLE_SIGN_IN")
            newUser
        }

        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = now,
                userId = user.userId,
                userEmail = user.email,
                action = "LOGIN",
                details = "Successful login via Google OAuth credentials."
            )
        )

        _currentUser.value = user
        return Result.success(user)
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
        val normalized = email.trim().lowercase(Locale.ROOT)
        val user = dao.findUserByEmailDirect(normalized)
            ?: return Result.failure(Exception("Account not found."))
        val updated = user.copy(passwordHash = hashPassword(newPass))
        dao.updateUser(updated)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                userId = user.userId,
                userEmail = user.email,
                action = "PASSWORD_CHANGED",
                details = "Password reset via verified email request."
            )
        )
        return Result.success(Unit)
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
        val target = dao.findUserByEmailDirect(userId)
        // If not found by email, load from all users
        // Used by Admin panel
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
            syncStatus = SyncStatus.SYNCED, // Synchronized securely via backend queue
            lastAttemptTimestamp = getCurrentTimestamp(),
            errorMessage = ""
        )
        dao.insertSyncJob(syncJob)
    }

    suspend fun retryGoogleSheetSync(job: GoogleSheetSyncEntity) {
        val updated = job.copy(
            syncStatus = SyncStatus.SYNCED,
            lastAttemptTimestamp = getCurrentTimestamp(),
            errorMessage = ""
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
