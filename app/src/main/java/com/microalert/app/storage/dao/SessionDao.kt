package com.microalert.app.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.microalert.app.storage.entity.MonitoringSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para sesiones de monitoreo.
 */
@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: MonitoringSessionEntity): Long

    @Query("SELECT * FROM monitoring_sessions ORDER BY startTimestampMs DESC")
    fun getAllSessions(): Flow<List<MonitoringSessionEntity>>

    @Query("SELECT * FROM monitoring_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): MonitoringSessionEntity?

    @Query("DELETE FROM monitoring_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    @Query("DELETE FROM monitoring_sessions")
    suspend fun clearAllSessions()

    @Query("SELECT COUNT(*) FROM monitoring_sessions")
    suspend fun getSessionCount(): Int
}
