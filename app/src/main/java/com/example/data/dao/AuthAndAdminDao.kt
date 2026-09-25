package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AuditLogEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.DeveloperProfileEntity
import com.example.data.model.GoogleSheetSyncEntity
import com.example.data.model.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthAndAdminDao {
    // --- User Accounts ---
    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun findUserByEmailDirect(email: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE userId = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts ORDER BY registrationDate DESC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Delete
    suspend fun deleteUser(user: UserAccountEntity)

    @Query("DELETE FROM user_accounts WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)

    // --- Google Sheets Sync Queue ---
    @Query("SELECT * FROM google_sheet_sync_queue ORDER BY id DESC")
    fun getAllSyncJobs(): Flow<List<GoogleSheetSyncEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncJob(job: GoogleSheetSyncEntity): Long

    @Update
    suspend fun updateSyncJob(job: GoogleSheetSyncEntity)

    @Query("DELETE FROM google_sheet_sync_queue WHERE userId = :userId")
    suspend fun deleteSyncJobsForUser(userId: String)

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY id DESC LIMIT 200")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity): Long

    // --- Contact Messages ---
    @Query("SELECT * FROM contact_messages ORDER BY id DESC")
    fun getAllContactMessages(): Flow<List<ContactMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactMessage(msg: ContactMessageEntity): Long

    // --- Developer Profile ---
    @Query("SELECT * FROM developer_profile WHERE id = 1 LIMIT 1")
    fun getDeveloperProfile(): Flow<DeveloperProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeveloperProfile(profile: DeveloperProfileEntity)
}
