package com.waydatasolution.devicewaylib.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class QueryParam(
    @PrimaryKey
    val key: String,
    val value: String
)