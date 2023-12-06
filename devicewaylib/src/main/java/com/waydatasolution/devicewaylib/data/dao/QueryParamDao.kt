package com.waydatasolution.devicewaylib.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.waydatasolution.devicewaylib.data.model.QueryParam
import com.waydatasolution.devicewaylib.util.AUTH_TOKEN

@Dao
internal interface QueryParamDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(dataList: List<QueryParam>)

    @Query("SELECT * FROM QueryParam")
    fun getAll(): List<QueryParam>

    @Query("DELETE FROM QueryParam")
    fun deleteAll()

    @Query("SELECT value FROM QueryParam WHERE `key` = :authTokenKey")
    fun getAuthToken(authTokenKey: String = AUTH_TOKEN): String
}