package com.waydatasolution.devicewaylib.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["bluetoothId", "timestamp", "samplesCount"])
internal data class Mission(
    val bluetoothId: String,
    val timestamp: Long,
    val samplesCount: Int
)