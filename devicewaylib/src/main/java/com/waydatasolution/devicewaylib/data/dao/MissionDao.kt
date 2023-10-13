package com.waydatasolution.devicewaylib.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.waydatasolution.devicewaylib.data.model.Mission

@Dao
internal interface MissionDao {
    @Insert
    fun insert(mission: Mission)

    @Delete
    fun delete(mission: Mission)

    @Query("SELECT * FROM MISSION WHERE bluetoothId = :bluetoothId")
    fun getLastMissionOfId(bluetoothId: String): List<Mission>
}