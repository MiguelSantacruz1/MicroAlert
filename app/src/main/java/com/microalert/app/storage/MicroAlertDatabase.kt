package com.microalert.app.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.microalert.app.storage.dao.AlertDao
import com.microalert.app.storage.dao.SessionDao
import com.microalert.app.storage.entity.AlertRecordEntity
import com.microalert.app.storage.entity.MonitoringSessionEntity

/**
 * Base de datos principal de Room para MICROALERT.
 * 100% local, privada y sin dependencias de red.
 */
@Database(
    entities = [
        MonitoringSessionEntity::class,
        AlertRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MicroAlertDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: MicroAlertDatabase? = null

        fun getInstance(context: Context): MicroAlertDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MicroAlertDatabase::class.java,
                    "microalert_local.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
