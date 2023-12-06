package com.waydatasolution.devicewaylib.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.waydatasolution.devicewaylib.data.model.Mission

@Dao
internal interface MissionDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(mission: Mission)

    @Query("DELETE FROM DATA")
    fun deleteAll()

    @Query("SELECT * FROM MISSION WHERE bluetoothId = :bluetoothId")
    fun getMissionById(bluetoothId: String): Mission?
}