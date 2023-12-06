package com.waydatasolution.devicewaylib.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.util.DATA_BLOCK_SIZE

@Dao
internal interface DataDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(dataList: List<Data>)

    @Query("DELETE FROM DATA WHERE DATE <= :date")
    fun deleteUntil(date: Long)

    @Query("SELECT * FROM DATA WHERE MAC = :mac ORDER BY DATE ASC LIMIT :limit")
    fun getDataBlockByMac(mac: String, limit: Int = DATA_BLOCK_SIZE): List<Data>

    @Query("SELECT * FROM DATA WHERE MAC = :mac")
    fun getAllByMac(mac: String): List<Data>

    @Query("SELECT MAC FROM DATA GROUP BY MAC ORDER BY DATE")
    fun getAllMacs(): List<String>

    @Query("SELECT * FROM DATA WHERE MAC = :mac ORDER BY DATE DESC LIMIT 2")
    fun getCurrentData(mac: String): List<Data>?

    @Query("DELETE FROM DATA")
    fun deleteAll()
}