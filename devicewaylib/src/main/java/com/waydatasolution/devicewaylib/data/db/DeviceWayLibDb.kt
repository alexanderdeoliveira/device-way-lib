package com.waydatasolution.devicewaylib.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.waydatasolution.devicewaylib.data.dao.DataDao
import com.waydatasolution.devicewaylib.data.dao.MissionDao
import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.data.model.Mission
import com.waydatasolution.devicewaylib.util.DATA_BASE_NAME

@Database(
    entities = [Data::class, Mission::class],
    version = 1,
    exportSchema = true
)

internal abstract class DeviceWayLibDb : RoomDatabase() {
    abstract fun dataDao(): DataDao
    abstract fun missionDao(): MissionDao

    companion object {
        @Volatile
        private var INSTANCE: DeviceWayLibDb? = null

        fun getDatabase(context: Context): DeviceWayLibDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    DeviceWayLibDb::class.java,
                    DATA_BASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}