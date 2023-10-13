package com.waydatasolution.devicewaylib.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.util.DATA_BLOCK_SIZE

@Dao
internal interface DataDao {
    @Insert
    fun insert(dataList: List<Data>)

    @Query("DELETE FROM DATA WHERE ID <= :dataId")
    fun deleteUntil(dataId: Int)

    @Query("SELECT * FROM DATA WHERE MAC = :mac ORDER BY ID ASC LIMIT :limit")
    fun getAll(mac: String, limit: Int = DATA_BLOCK_SIZE): List<Data>

    @Query("SELECT MAC FROM DATA GROUP BY MAC ORDER BY MAC")
    fun getAllMacs(): List<String>

    @Query("SELECT COUNT(ID) FROM DATA")
    fun getDataCount(): Long
}