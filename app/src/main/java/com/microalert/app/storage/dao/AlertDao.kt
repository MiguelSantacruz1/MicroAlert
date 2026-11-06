package com.microalert.app.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.microalert.app.storage.entity.AlertRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para eventos de alerta.
 */
@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertRecordEntity): Long

    @Query("SELECT * FROM alert_records WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    fun getAlertsForSession(sessionId: Long): Flow<List<AlertRecordEntity>>

    @Query("SELECT * FROM alert_records ORDER BY timestampMs DESC LIMIT :limit")
    fun getRecentAlerts(limit: Int = 50): Flow<List<AlertRecordEntity>>

    @Query("DELETE FROM alert_records WHERE sessionId = :sessionId")
    suspend fun deleteAlertsForSession(sessionId: Long)

    @Query("DELETE FROM alert_records")
    suspend fun clearAllAlerts()
}
